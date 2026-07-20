package br.com.wastenio.backend.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.wastenio.backend.dto.request.ProductCompositionRequest;
import br.com.wastenio.backend.dto.request.ProductRequest;
import br.com.wastenio.backend.dto.response.ProductResponse;
import br.com.wastenio.backend.entity.Product;
import br.com.wastenio.backend.entity.ProductComposition;
import br.com.wastenio.backend.entity.RawMaterial;
import br.com.wastenio.backend.exception.BusinessException;
import br.com.wastenio.backend.exception.ResourceNotFoundException;
import br.com.wastenio.backend.mapper.ProductMapper;
import br.com.wastenio.backend.repository.ProductRepository;
import br.com.wastenio.backend.repository.RawMaterialRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductMapper productMapper;

    public ProductService(
            ProductRepository productRepository,
            RawMaterialRepository rawMaterialRepository,
            ProductMapper productMapper) {

        this.productRepository = productRepository;
        this.rawMaterialRepository = rawMaterialRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        String normalizedCode = productMapper.normalizeCode(request.code());

        validateDuplicatedCode(normalizedCode);
        validateDuplicatedRawMaterialsInComposition(request.compositions());

        Product product = productMapper.toEntity(request);

        addCompositionsToProduct(product, request.compositions());

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = findEntityById(id);

        return productMapper.toResponse(product);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findEntityById(id);

        String normalizedCode = productMapper.normalizeCode(request.code());

        if (productRepository.existsByCodeAndIdNot(normalizedCode, id)) {
            throw new BusinessException(
                    "A product with code '" + normalizedCode + "' already exists"
            );
        }

        validateDuplicatedRawMaterialsInComposition(request.compositions());

        productMapper.updateEntityFromRequest(request, product);

        product.clearCompositions();
        addCompositionsToProduct(product, request.compositions());

        Product updatedProduct = productRepository.save(product);

        return productMapper.toResponse(updatedProduct);
    }

    @Transactional
    public void delete(Long id) {
        Product product = findEntityById(id);

        productRepository.delete(product);
    }

    private Product findEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with id " + id + " was not found"
                ));
    }

    private void validateDuplicatedCode(String normalizedCode) {
        if (productRepository.existsByCode(normalizedCode)) {
            throw new BusinessException(
                    "A product with code '" + normalizedCode + "' already exists"
            );
        }
    }

    private void validateDuplicatedRawMaterialsInComposition(
            List<ProductCompositionRequest> compositions) {

        Set<Long> rawMaterialIds = new HashSet<>();

        for (ProductCompositionRequest composition : compositions) {
            boolean added = rawMaterialIds.add(composition.rawMaterialId());

            if (!added) {
                throw new BusinessException(
                        "Raw material with id " + composition.rawMaterialId()
                                + " is duplicated in product composition"
                );
            }
        }
    }

    private void addCompositionsToProduct(
            Product product,
            List<ProductCompositionRequest> compositionRequests) {

        for (ProductCompositionRequest compositionRequest : compositionRequests) {
            RawMaterial rawMaterial = findRawMaterialById(
                    compositionRequest.rawMaterialId()
            );

            ProductComposition composition = new ProductComposition(
                    rawMaterial,
                    compositionRequest.requiredQuantity()
            );

            product.addComposition(composition);
        }
    }

    private RawMaterial findRawMaterialById(Long id) {
        return rawMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Raw material with id " + id + " was not found"
                ));
    }
}