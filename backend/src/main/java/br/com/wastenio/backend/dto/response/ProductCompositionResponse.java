package br.com.wastenio.backend.dto.response;

import java.math.BigDecimal;

public record ProductCompositionResponse(

        Long id,
        Long rawMaterialId,
        String rawMaterialCode,
        String rawMaterialName,
        BigDecimal requiredQuantity,
        String unit

) {
}