package ro.axonsoft.eval.minibank.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import ro.axonsoft.eval.minibank.model.enums.AccountType;
import ro.axonsoft.eval.minibank.model.enums.CurrencyType;

public record AccountRequest(
        @NotBlank String ownerName,
        @NotBlank String iban,
        @NotNull CurrencyType currency,
        @NotNull AccountType accountType
) {
}
