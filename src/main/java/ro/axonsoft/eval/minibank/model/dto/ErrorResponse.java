package ro.axonsoft.eval.minibank.model.dto;

public record ErrorResponse(
        String status,
        String message
) {
}
