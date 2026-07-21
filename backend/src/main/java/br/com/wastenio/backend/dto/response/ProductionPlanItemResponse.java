package br.com.wastenio.backend.dto.response;

import java.math.BigDecimal;

public record ProductionPlanItemResponse(

        Long productId,
        String productCode,
        String productName,
        Integer quantity,
        BigDecimal unitSalePrice,
        BigDecimal totalSaleValue

) {
}