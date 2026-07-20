package br.com.wastenio.backend.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record ProductCompositionRequest(

        @NotNull(message = "Raw material ID is required")
        Long rawMaterialId,

        @NotNull(message = "Required quantity is required")
        @DecimalMin(
                value = "0.001",
                inclusive = true,
                message = "Required quantity must be greater than zero"
        )
        @Digits(
                integer = 12,
                fraction = 3,
                message = "Required quantity must have at most 12 integer digits and 3 decimal places"
        )
        BigDecimal requiredQuantity

) {
}