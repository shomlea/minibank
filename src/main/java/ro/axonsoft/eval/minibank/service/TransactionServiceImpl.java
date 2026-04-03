package ro.axonsoft.eval.minibank.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.axonsoft.eval.minibank.model.Account;
import ro.axonsoft.eval.minibank.model.Transaction;
import ro.axonsoft.eval.minibank.repository.TransactionRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    TransactionRepository transactionRepository;
    AccountService accountService;
    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    @Override
    @Transactional
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public List<Transaction> findByAccountId(Long accountId){
        accountService.findById(accountId)
                .orElseThrow(() -> new NoSuchElementException("No account with id " + accountId + " found."));

        return transactionRepository.findByAccountId(accountId);
    }

    @Override
    public Page<Transaction> findByAccountId(Long accountId, Pageable pageable){
        return transactionRepository.findByAccountId(accountId, pageable);
    }

    @Override
    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }
}
