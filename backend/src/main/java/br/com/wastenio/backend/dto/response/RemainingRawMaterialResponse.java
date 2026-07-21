package br.com.wastenio.backend.dto.response;

import java.math.BigDecimal;

public record RemainingRawMaterialResponse(

        Long rawMaterialId,
        String rawMaterialCode,
        String rawMaterialName,
        BigDecimal initialQuantity,
        BigDecimal usedQuantity,
        BigDecimal remainingQuantity,
        String unit

) {
}