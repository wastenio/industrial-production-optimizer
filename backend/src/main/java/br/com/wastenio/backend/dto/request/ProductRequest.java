package br.com.wastenio.backend.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductRequest(

        @NotBlank(message = "Code is required")
        @Size(max = 50, message = "Code must have at most 50 characters")
        String code,

        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must have at most 150 characters")
        String name,

        @NotNull(message = "Sale price is required")
        @DecimalMin(
                value = "0.01",
                inclusive = true,
                message = "Sale price must be greater than zero"
        )
        @Digits(
                integer = 13,
                fraction = 2,
                message = "Sale price must have at most 13 integer digits and 2 decimal places"
        )
        BigDecimal salePrice,

        @NotEmpty(message = "Product composition is required")
        @Valid
        List<ProductCompositionRequest> compositions

) {
}