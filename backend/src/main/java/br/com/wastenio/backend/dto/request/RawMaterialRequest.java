package br.com.wastenio.backend.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RawMaterialRequest(
        @NotBlank(message = "O código é obrigatório.")
        @Size(max = 50, message = "O código deve ter no máximo 50 caracteres.")
        String code,

        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String name,

        @NotNull(message = "A quantidade em estoque é obrigatória.")
        @DecimalMin(value = "0.000", inclusive = true, message = "A quantidade em estoque deve ser maior ou igual a zero.")
        @Digits(integer = 12, fraction = 3, message = "A quantidade em estoque deve ter no máximo 12 dígitos inteiros e 3 casas decimais.")
        BigDecimal stockQuantity,

        @NotBlank(message = "A unidade é obrigatória.")
        @Size(max = 20, message = "A unidade deve ter no máximo 20 caracteres.")
        String unit
) {}