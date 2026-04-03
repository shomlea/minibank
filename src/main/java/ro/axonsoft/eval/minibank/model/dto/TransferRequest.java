package ro.axonsoft.eval.minibank.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.axonsoft.eval.minibank.model.enums.AccountType;
import ro.axonsoft.eval.minibank.model.enums.CurrencyType;

import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank String sourceIban,
        @NotBlank String targetIban,
        @NotNull BigDecimal amount
) {
}
