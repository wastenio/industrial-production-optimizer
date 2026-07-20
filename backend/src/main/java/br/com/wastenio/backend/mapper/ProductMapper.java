package br.com.wastenio.backend.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.wastenio.backend.dto.request.ProductRequest;
import br.com.wastenio.backend.dto.response.ProductCompositionResponse;
import br.com.wastenio.backend.dto.response.ProductResponse;
import br.com.wastenio.backend.entity.Product;
import br.com.wastenio.backend.entity.ProductComposition;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request) {
        return new Product(
                normalizeCode(request.code()),
                request.name().trim(),
                request.salePrice()
        );
    }

    public ProductResponse toResponse(Product product) {
        List<ProductCompositionResponse> compositionResponses = product.getCompositions()
                .stream()
                .map(this::toCompositionResponse)
                .toList();

        return new ProductResponse(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getSalePrice(),
                compositionResponses,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private ProductCompositionResponse toCompositionResponse(
            ProductComposition composition) {

        return new ProductCompositionResponse(
                composition.getId(),
                composition.getRawMaterial().getId(),
                composition.getRawMaterial().getCode(),
                composition.getRawMaterial().getName(),
                composition.getRequiredQuantity(),
                composition.getRawMaterial().getUnit()
        );
    }

    public void updateEntityFromRequest(
            ProductRequest request,
            Product product) {

        product.setCode(normalizeCode(request.code()));
        product.setName(request.name().trim());
        product.setSalePrice(request.salePrice());
    }

    public String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }
}