package ro.axonsoft.eval.minibank.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.axonsoft.eval.minibank.model.Account;
import ro.axonsoft.eval.minibank.model.Transaction;
import ro.axonsoft.eval.minibank.model.dto.AccountResponse;
import ro.axonsoft.eval.minibank.model.dto.TransactionResponse;
import ro.axonsoft.eval.minibank.model.dto.TransferResponse;
import ro.axonsoft.eval.minibank.service.AccountService;
import ro.axonsoft.eval.minibank.service.TransactionService;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    public AccountController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;

    }



    @PostMapping
    public ResponseEntity<AccountResponse> create(@RequestBody Account account, @RequestHeader("X-Idempotency-Key") String key) {
        Account savedAccount = accountService.save(account, key);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AccountResponse.fromEntity(savedAccount));
    }

    @GetMapping
    public ResponseEntity<Page<AccountResponse>> getAllAccounts(Pageable pageable) {
        Page<AccountResponse> page = accountService.getAll(pageable)
                .map(AccountResponse::fromEntity);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getBalance(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) {
        return accountService.findById(id)
                .map(account -> ResponseEntity.ok(AccountResponse.fromEntity(account)))
                .orElse(ResponseEntity.notFound().build());
    }



    @GetMapping("/{id}/transactions")
    public ResponseEntity<Page<TransactionResponse>> getTransactionHistory(@PathVariable Long id, Pageable pageable) {
        return accountService.findById(id)
                .map(account -> transactionService.findByAccountId(id, pageable)
                        .map(TransactionResponse::fromEntity))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}