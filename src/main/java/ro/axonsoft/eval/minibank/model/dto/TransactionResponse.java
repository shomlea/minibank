package ro.axonsoft.eval.minibank.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.axonsoft.eval.minibank.model.Transaction;
import ro.axonsoft.eval.minibank.model.enums.CurrencyType;
import ro.axonsoft.eval.minibank.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        @NotNull
        Long id,
        @NotNull
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
                timezone = "UTC"
        )
        Instant timestamp,
        @NotBlank
        TransactionType type,
        @NotNull
        BigDecimal amount,
        @NotBlank
        CurrencyType currency,
        @NotNull
        BigDecimal balanceAfter,
        @NotBlank
        String counterPartyIban,
        @NotNull
        Long transferId
) {
    public static TransactionResponse fromEntity(ro.axonsoft.eval.minibank.model.Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getTimestamp(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getBalanceAfter(),
                transaction.getCounterpartyIban(),
                transaction.getTransferId()

        );
    }
}
