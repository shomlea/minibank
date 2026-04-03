package ro.axonsoft.eval.minibank.service;

import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.axonsoft.eval.minibank.components.ExchangeRateConfig;
import ro.axonsoft.eval.minibank.model.Account;
import ro.axonsoft.eval.minibank.model.IdempotencyRecord;
import ro.axonsoft.eval.minibank.model.Transaction;
import ro.axonsoft.eval.minibank.model.Transfer;
import ro.axonsoft.eval.minibank.model.enums.CurrencyType;
import ro.axonsoft.eval.minibank.model.enums.TransactionType;
import ro.axonsoft.eval.minibank.repository.AccountRepository;
import ro.axonsoft.eval.minibank.repository.TransactionRepository;
import ro.axonsoft.eval.minibank.repository.TransferRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TransferServiceImpl implements TransferService {

    private TransferRepository transferRepository;
    private IdempotencyService idempotencyService;
    private AccountService accountService;
    private AccountRepository accountRepository;
    private TransactionService transactionService;
    private ExchangeRateConfig exchangeRateConfig;

    public TransferServiceImpl(TransferRepository transferRepository, IdempotencyService idempotencyService, AccountService accountService, AccountRepository accountRepository, TransactionService transactionService, ExchangeRateConfig exchangeRateConfig) {
        this.transferRepository = transferRepository;
        this.idempotencyService = idempotencyService;
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
        this.exchangeRateConfig = exchangeRateConfig;
    }

    public boolean isSepa(String sourceIban, String targetIban){
        String sourceCountry = sourceIban.substring(0, 2).toUpperCase();
        String destinationCountry = targetIban.substring(0, 2).toUpperCase();

        List<String> sepaCountries = List.of("AT", "BE", "BG", "CY", "CZ",
                "DE", "DK", "EE", "ES", "FI", "FR", "GR", "HR", "HU", "IE", "IT",
                "LT", "LU", "LV", "MT", "NL", "PL", "PT", "RO", "SE", "SI", "SK",
                "AD", "CH", "GB", "IS", "LI", "MC", "NO", "SM", "VA");
        return sepaCountries.contains(sourceCountry) && sepaCountries.contains(destinationCountry);
    }

    @Override
    @Transactional
    public Transfer save(Transfer transfer, String key) {
        Optional<IdempotencyRecord> existing = idempotencyService.findByRequestKey(key);

//        if (existing.isPresent()) {
//            IdempotencyRecord idempotencyRecord = existing.get();
//            return transferRepository.findById(idempotencyRecord.getResourceId())
//                    .orElseThrow(() -> new IllegalStateException("Record exists but transfer is missing"));
//
//        }

        if (existing.isPresent()) {
            IdempotencyRecord idempotencyRecord = existing.get();
            Transfer foundTransfer = transferRepository.findById(idempotencyRecord.getResourceId())
                    .orElseThrow(() -> new IllegalStateException("Record exists but transfer is missing"));

            if(foundTransfer.getAmount().compareTo(transfer.getAmount()) != 0
               || !foundTransfer.getSourceIban().equals(transfer.getSourceIban())
                || !foundTransfer.getTargetIban().equals(transfer.getTargetIban())){
                throw new DataIntegrityViolationException("The same Idempotency key was used for 2 different transfers");
            }

            return foundTransfer;
        }

        if(transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        Account sourceAccount = accountService.findByIban(transfer.getSourceIban())
                .orElseThrow(() -> new IllegalArgumentException("Account with IBAN " + transfer.getSourceIban() + " doesn't exist"));
        Account targetAccount = accountService.findByIban(transfer.getTargetIban())
                .orElseThrow(() -> new IllegalArgumentException("Account with IBAN " + transfer.getTargetIban() + " doesn't exist"));

        if(transfer.getSourceIban().equals(transfer.getTargetIban())) {
            throw new IllegalArgumentException("Source and Target IBAN can't be the same");
        }

        if(!isSepa(transfer.getSourceIban(), transfer.getTargetIban())) {
            throw new IllegalArgumentException("At least one IBAN is not from a SEPA country");
        }

        boolean isSystemSource = sourceAccount.getId().equals(1L);

        if(sourceAccount.getBalance().compareTo(transfer.getAmount()) < 0 && !isSystemSource) {
            throw new IllegalArgumentException("Not enough money in account to send this transaction");
        }

        if("SAVINGS".equals(sourceAccount.getAccountType().name())) {
            List<Transfer> transfers = transferRepository.findBySourceIban(transfer.getSourceIban());

            Instant startOfToday = LocalDate.now()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant();

            BigDecimal totalSpentToday = transfers.stream()
                    .filter(t -> t.getCreatedAt().isAfter(startOfToday))
                    .map(Transfer::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);


            BigDecimal totalWithCurrent = totalSpentToday.add(transfer.getAmount());


            BigDecimal sourceToRonRate = exchangeRateConfig.getRates().get(sourceAccount.getCurrency().name());
//            BigDecimal totalInRon = totalWithCurrent.multiply(sourceToRonRate);

            BigDecimal eurRate = exchangeRateConfig.getRates().get("EUR");
//            BigDecimal totalInEur = totalInRon.divide(eurRate, 2, RoundingMode.HALF_EVEN);

            BigDecimal effectiveRate = sourceToRonRate.divide(eurRate, 6, RoundingMode.HALF_EVEN);

            BigDecimal totalInEur = totalWithCurrent.multiply(effectiveRate);

            if (totalInEur.compareTo(new BigDecimal("5000")) > 0) {
                throw new IllegalArgumentException("Savings account daily limit of 5000 EUR exceeded");
            }
        }

        BigDecimal convertedAmount = null;



        if(sourceAccount.getCurrency() != targetAccount.getCurrency()) {
            BigDecimal sourceToRon = exchangeRateConfig.getRates().get(sourceAccount.getCurrency().name());
            BigDecimal targetToRon = exchangeRateConfig.getRates().get(targetAccount.getCurrency().name());

            BigDecimal effectiveRate = sourceToRon.divide(targetToRon, 6, RoundingMode.HALF_EVEN);

//            transfer.setExchangeRate(sourceToRon.divide(targetToRon, 2, RoundingMode.HALF_EVEN));

            transfer.setExchangeRate(effectiveRate);

//            BigDecimal amountInRon = transfer.getAmount().multiply(sourceToRon);
//            convertedAmount = amountInRon.divide(targetToRon, 2, RoundingMode.HALF_EVEN);

            convertedAmount = transfer.getAmount()
                    .multiply(effectiveRate)
                    .setScale(2, RoundingMode.HALF_EVEN);

            transfer.setConvertedAmount(convertedAmount);

        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(transfer.getAmount()));
        if(convertedAmount != null) {
            targetAccount.setBalance(targetAccount.getBalance().add(convertedAmount));
            transfer.setConvertedAmount(convertedAmount);
        }
        else targetAccount.setBalance(targetAccount.getBalance().add(transfer.getAmount()));

        transfer.setCurrency(sourceAccount.getCurrency());
        transfer.setTargetCurrency(targetAccount.getCurrency());
        transfer.setCreatedAt(Instant.now());

        Transfer savedTransfer = transferRepository.save(transfer);

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        Transaction firstTransaction = new Transaction();
        if(sourceAccount.getId() != 1L){
            firstTransaction.setAccountId(sourceAccount.getId());
            firstTransaction.setAmount(transfer.getAmount());
            firstTransaction.setTimestamp(transfer.getCreatedAt());
            firstTransaction.setCurrency(sourceAccount.getCurrency());
            firstTransaction.setBalanceAfter(sourceAccount.getBalance());
            if(targetAccount.getId() != 1L) {
                firstTransaction.setCounterpartyIban(transfer.getTargetIban());
                firstTransaction.setType(TransactionType.valueOf("TRANSFER_OUT"));
            }
            else {
                firstTransaction.setCounterpartyIban(null);
                firstTransaction.setType(TransactionType.valueOf("WITHDRAWAL"));
            }
            firstTransaction.setTransferId(transfer.getId());
        }
        Transaction secondTransaction = new Transaction();
        if(targetAccount.getId() != 1L){
            secondTransaction.setAccountId(targetAccount.getId());

            if(convertedAmount == null)
                secondTransaction.setAmount(transfer.getAmount());
            else secondTransaction.setAmount(convertedAmount);
            secondTransaction.setTimestamp(transfer.getCreatedAt());
            secondTransaction.setCurrency(targetAccount.getCurrency());
            secondTransaction.setBalanceAfter(targetAccount.getBalance());
            if(sourceAccount.getId() != 1L) {
                secondTransaction.setCounterpartyIban(transfer.getSourceIban());
                secondTransaction.setType(TransactionType.valueOf("TRANSFER_IN"));
            }
            else {
                secondTransaction.setCounterpartyIban(null);
                secondTransaction.setType(TransactionType.valueOf("DEPOSIT"));
            }
            secondTransaction.setTransferId(transfer.getId());
        }

        if(sourceAccount.getId() != 1L)
            transactionService.save(firstTransaction);
        if(targetAccount.getId() != 1L)
            transactionService.save(secondTransaction);

        IdempotencyRecord savedIdempotencyRecord = new IdempotencyRecord(key, "transfer", savedTransfer.getId());

        idempotencyService.save(savedIdempotencyRecord);

        return savedTransfer;

    }

    @Override
    public Optional<Transfer> findById(Long id) {
        return transferRepository.findById(id);
    }

    @Override
    public List<Transfer> getAll() {
        return transferRepository.findAll();
    }

    @Override
    public Page<Transfer> getAll(Pageable pageable){
        return transferRepository.findAll(pageable);
    }


}
