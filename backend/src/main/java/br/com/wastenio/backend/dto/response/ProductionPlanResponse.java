package br.com.wastenio.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductionPlanResponse(

        List<ProductionPlanItemResponse> items,
        Integer totalProductionUnits,
        BigDecimal totalSaleValue,
        List<RemainingRawMaterialResponse> remainingRawMaterials

) {
}