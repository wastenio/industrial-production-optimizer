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
        @NotBlank(message = "O código é obrigatório.")
        @Size(max = 50, message = "O código deve ter no máximo 50 caracteres.")
        String code,

        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String name,

        @NotNull(message = "O valor de venda é obrigatório.")
        @DecimalMin(value = "0.01", inclusive = true, message = "O valor de venda deve ser maior que zero.")
        @Digits(integer = 13, fraction = 2, message = "O valor de venda deve ter no máximo 13 dígitos inteiros e 2 casas decimais.")
        BigDecimal salePrice,

        @NotEmpty(message = "A composição do produto é obrigatória.")
        @Valid
        List<ProductCompositionRequest> compositions
) {}