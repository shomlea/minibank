package ro.axonsoft.eval.minibank.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.axonsoft.eval.minibank.model.enums.AccountType;
import ro.axonsoft.eval.minibank.model.enums.CurrencyType;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
        @NotNull
        Long id,

        @NotBlank
        String ownerName,

        @NotBlank
        String iban,

        @NotNull
        BigDecimal balance,

        @NotNull
        CurrencyType currency,

        @NotNull
        AccountType accountType,

        @NotNull
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
                timezone = "UTC"
        )
        Instant createdAt
) {
    public static AccountResponse fromEntity(ro.axonsoft.eval.minibank.model.Account account) {
        return new AccountResponse(
                account.getId(),
                account.getOwnerName(),
                account.getIban(),
                account.getBalance(),
                account.getCurrency(),
                account.getAccountType(),
                account.getCreatedAt()
        );
    }
}