package br.com.wastenio.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RawMaterialResponse(

        Long id,
        String code,
        String name,
        BigDecimal stockQuantity,
        String unit,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}