package ro.axonsoft.eval.minibank.components;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.axonsoft.eval.minibank.model.Account;
import ro.axonsoft.eval.minibank.model.enums.AccountType;
import ro.axonsoft.eval.minibank.model.enums.CurrencyType;
import ro.axonsoft.eval.minibank.repository.AccountRepository;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(AccountRepository repository) {
        return args -> {
                Account systemAccount = new Account();
                systemAccount.setOwnerName("SYSTEM_BANK");
                systemAccount.setIban("RO49AAAA1B31007593840000");
                systemAccount.setCurrency(CurrencyType.RON);
                systemAccount.setAccountType(AccountType.CHECKING);

                systemAccount.setBalance(new BigDecimal("1000000000.00"));

                repository.save(systemAccount);
        };
    }
}