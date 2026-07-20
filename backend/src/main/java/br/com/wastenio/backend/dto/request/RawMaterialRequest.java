package br.com.wastenio.backend.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RawMaterialRequest(

        @NotBlank(message = "Code is required")
        @Size(max = 50, message = "Code must have at most 50 characters")
        String code,

        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must have at most 150 characters")
        String name,

        @NotNull(message = "Stock quantity is required")
        @DecimalMin(
                value = "0.000",
                inclusive = true,
                message = "Stock quantity must be zero or greater"
        )
        @Digits(
                integer = 12,
                fraction = 3,
                message = "Stock quantity must have at most 12 integer digits and 3 decimal places"
        )
        BigDecimal stockQuantity,

        @NotBlank(message = "Unit is required")
        @Size(max = 20, message = "Unit must have at most 20 characters")
        String unit

) {
}