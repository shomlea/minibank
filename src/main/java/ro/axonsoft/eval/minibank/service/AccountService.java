package ro.axonsoft.eval.minibank.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ro.axonsoft.eval.minibank.model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {

    BigDecimal getBalance(Long id);

    Account save(Account account, String key);

    Optional<Account> findById(Long id);

    Optional<Account> findByIban(String iban);

    Page<Account> getAll(Pageable pageable);

    void updateBalance(Long id, BigDecimal balance);
}
