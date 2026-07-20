package br.com.wastenio.backend.mapper;

import org.springframework.stereotype.Component;

import br.com.wastenio.backend.dto.request.RawMaterialRequest;
import br.com.wastenio.backend.dto.response.RawMaterialResponse;
import br.com.wastenio.backend.entity.RawMaterial;

@Component
public class RawMaterialMapper {

    public RawMaterial toEntity(RawMaterialRequest request) {
        return new RawMaterial(
                normalizeCode(request.code()),
                request.name().trim(),
                request.stockQuantity(),
                normalizeUnit(request.unit())
        );
    }

    public RawMaterialResponse toResponse(RawMaterial rawMaterial) {
        return new RawMaterialResponse(
                rawMaterial.getId(),
                rawMaterial.getCode(),
                rawMaterial.getName(),
                rawMaterial.getStockQuantity(),
                rawMaterial.getUnit(),
                rawMaterial.getCreatedAt(),
                rawMaterial.getUpdatedAt()
        );
    }

    public void updateEntityFromRequest(
            RawMaterialRequest request,
            RawMaterial rawMaterial) {

        rawMaterial.setCode(normalizeCode(request.code()));
        rawMaterial.setName(request.name().trim());
        rawMaterial.setStockQuantity(request.stockQuantity());
        rawMaterial.setUnit(normalizeUnit(request.unit()));
    }

    public String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    public String normalizeUnit(String unit) {
        return unit.trim().toUpperCase();
    }
}