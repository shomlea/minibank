package ro.axonsoft.eval.minibank.service;

import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.axonsoft.eval.minibank.model.Account;
import ro.axonsoft.eval.minibank.model.IdempotencyRecord;
import ro.axonsoft.eval.minibank.repository.AccountRepository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private IdempotencyService idempotencyService;
    public AccountServiceImpl(AccountRepository accountRepository,  IdempotencyService idempotencyService) {
        this.accountRepository = accountRepository;
        this.idempotencyService = idempotencyService;
    }

    @Override
    public BigDecimal getBalance(Long id) {
        Optional<Account> foundAccount = accountRepository.findById(id);

        if (foundAccount.isEmpty()) {
            throw new NoSuchElementException();
        }
        else {
            return foundAccount.get().getBalance();
        }

    }

    boolean isValidIban(String iban) {
        if (iban == null) return false;

        if(iban.length() < 15) {
            return false;
        }

        String rearranged = iban.substring(4) + iban.substring(0, 4);

        StringBuilder numericIban = new StringBuilder();
        for (int i = 0; i < rearranged.length(); i++) {
            char ch = rearranged.charAt(i);
            if (Character.isLetter(ch)) {
                numericIban.append(Character.getNumericValue(ch));
            } else if (Character.isDigit(ch)) {
                numericIban.append(ch);
            } else {
                return false;
            }
        }

        BigInteger ibanNumber = new BigInteger(numericIban.toString());
        return ibanNumber.remainder(BigInteger.valueOf(97)).intValue() == 1;
    }

    @Override
    @Transactional
    public Account save(Account account, String key) {

        Optional<IdempotencyRecord> existing = idempotencyService.findByRequestKey(key);

        if (existing.isPresent()) {
            IdempotencyRecord idempotencyRecord = existing.get();
            Account foundAccount = accountRepository.findById(idempotencyRecord.getResourceId())
                    .orElseThrow(() -> new IllegalStateException("Record exists but account is missing"));

            if(!foundAccount.getOwnerName().equals(account.getOwnerName())
                || !foundAccount.getIban().equals(account.getIban())
                || !foundAccount.getCurrency().equals(account.getCurrency())
                    || !foundAccount.getAccountType().equals(account.getAccountType())) {
                throw new DataIntegrityViolationException("The same Idempotency key was used for 2 different accounts");
            }

            return foundAccount;
        }

        if(!isValidIban(account.getIban())) {
            throw new IllegalArgumentException("Invalid IBAN");
        }

        if(accountRepository.findByIban(account.getIban()).isPresent()) {
            throw new DataIntegrityViolationException("IBAN already exists");
        }

        Account savedAccount = accountRepository.save(account);

        IdempotencyRecord savedIdempotencyRecord = new IdempotencyRecord(key, "account", savedAccount.getId());

        idempotencyService.save(savedIdempotencyRecord);

        return savedAccount;
    }

    @Override
    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public Optional<Account> findByIban(String iban) {
        return accountRepository.findByIban(iban);
    }

    @Override
    public Page<Account> getAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }



    @Override
    @Transactional
    public void updateBalance(Long id, BigDecimal balance) {
        Optional<Account> foundAccount = accountRepository.findById(id);

        if (foundAccount.isEmpty()) {
            throw new NoSuchElementException();
        }
        else {
            foundAccount.get().setBalance(balance);
            accountRepository.save(foundAccount.get());
        }
    }
}
