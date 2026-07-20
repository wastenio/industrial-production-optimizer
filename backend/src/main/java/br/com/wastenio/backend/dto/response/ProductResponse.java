package br.com.wastenio.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(

        Long id,
        String code,
        String name,
        BigDecimal salePrice,
        List<ProductCompositionResponse> compositions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}