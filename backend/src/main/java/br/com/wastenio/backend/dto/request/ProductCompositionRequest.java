package br.com.wastenio.backend.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record ProductCompositionRequest(
        @NotNull(message = "A matéria-prima é obrigatória.")
        Long rawMaterialId,

        @NotNull(message = "A quantidade necessária é obrigatória.")
        @DecimalMin(value = "0.001", inclusive = true, message = "A quantidade necessária deve ser maior que zero.")
        @Digits(integer = 12, fraction = 3, message = "A quantidade necessária deve ter no máximo 12 dígitos inteiros e 3 casas decimais.")
        BigDecimal requiredQuantity
) {}