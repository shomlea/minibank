package ro.axonsoft.eval.minibank.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ro.axonsoft.eval.minibank.model.enums.AccountType;
import ro.axonsoft.eval.minibank.model.enums.CurrencyType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferResponse(
        @NotNull
        Long id,
        @NotBlank
        String sourceIban,
        @NotBlank
        String targetIban,
        @NotNull
        BigDecimal amount,
        @NotNull
        CurrencyType currency,
        @NotNull
        CurrencyType targetCurrency,
        BigDecimal exchangeRate,
        BigDecimal convertedAmount,
        @NotNull
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
                timezone = "UTC"
        )
        Instant createdAt
) {
    public static TransferResponse fromEntity(ro.axonsoft.eval.minibank.model.Transfer transfer) {
        return new TransferResponse(
                transfer.getId(),
                transfer.getSourceIban(),
                transfer.getTargetIban(),
                transfer.getAmount(),
                transfer.getCurrency(),
                transfer.getTargetCurrency(),
                transfer.getExchangeRate(),
                transfer.getConvertedAmount(),
                transfer.getCreatedAt()

        );
    }
}
