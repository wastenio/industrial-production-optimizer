package br.com.wastenio.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.wastenio.backend.dto.request.ProductCompositionRequest;
import br.com.wastenio.backend.dto.request.ProductRequest;
import br.com.wastenio.backend.dto.response.ProductResponse;
import br.com.wastenio.backend.entity.Product;
import br.com.wastenio.backend.entity.RawMaterial;
import br.com.wastenio.backend.exception.BusinessException;
import br.com.wastenio.backend.exception.ResourceNotFoundException;
import br.com.wastenio.backend.mapper.ProductMapper;
import br.com.wastenio.backend.repository.ProductRepository;
import br.com.wastenio.backend.repository.RawMaterialRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    private ProductMapper productMapper;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapper();

        productService = new ProductService(
                productRepository,
                rawMaterialRepository,
                productMapper
        );
    }

    @Test
    void shouldCreateProductSuccessfully() {
        ProductRequest request = createProductRequest();

        RawMaterial steel = createRawMaterial(
                1L,
                "MP-001",
                "Aço",
                new BigDecimal("500.000"),
                "KG"
        );

        RawMaterial screw = createRawMaterial(
                2L,
                "MP-002",
                "Parafuso",
                new BigDecimal("1000.000"),
                "UN"
        );

        Product savedProduct = createProduct();
        setId(savedProduct, 1L);
        setCreatedAt(savedProduct, LocalDateTime.of(2026, 7, 20, 16, 0));

        savedProduct.addComposition(
                new br.com.wastenio.backend.entity.ProductComposition(
                        steel,
                        new BigDecimal("15.000")
                )
        );

        savedProduct.addComposition(
                new br.com.wastenio.backend.entity.ProductComposition(
                        screw,
                        new BigDecimal("20.000")
                )
        );

        when(productRepository.existsByCode("PRD-001"))
                .thenReturn(false);

        when(rawMaterialRepository.findById(1L))
                .thenReturn(Optional.of(steel));

        when(rawMaterialRepository.findById(2L))
                .thenReturn(Optional.of(screw));

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        ProductResponse response = productService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.code()).isEqualTo("PRD-001");
        assertThat(response.name()).isEqualTo("Mesa Industrial");
        assertThat(response.salePrice()).isEqualByComparingTo("850.00");
        assertThat(response.compositions()).hasSize(2);

        assertThat(response.compositions().get(0).rawMaterialId()).isEqualTo(1L);
        assertThat(response.compositions().get(0).rawMaterialCode()).isEqualTo("MP-001");
        assertThat(response.compositions().get(0).requiredQuantity()).isEqualByComparingTo("15.000");

        assertThat(response.compositions().get(1).rawMaterialId()).isEqualTo(2L);
        assertThat(response.compositions().get(1).rawMaterialCode()).isEqualTo("MP-002");
        assertThat(response.compositions().get(1).requiredQuantity()).isEqualByComparingTo("20.000");

        verify(productRepository).existsByCode("PRD-001");
        verify(rawMaterialRepository).findById(1L);
        verify(rawMaterialRepository).findById(2L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldThrowBusinessExceptionWhenCreatingProductWithDuplicatedCode() {
        ProductRequest request = createProductRequest();

        when(productRepository.existsByCode("PRD-001"))
                .thenReturn(true);

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("A product with code 'PRD-001' already exists");

        verify(productRepository).existsByCode("PRD-001");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldThrowBusinessExceptionWhenCompositionHasDuplicatedRawMaterial() {
        ProductRequest request = new ProductRequest(
                "PRD-001",
                "Mesa Industrial",
                new BigDecimal("850.00"),
                List.of(
                        new ProductCompositionRequest(1L, new BigDecimal("15.000")),
                        new ProductCompositionRequest(1L, new BigDecimal("20.000"))
                )
        );

        when(productRepository.existsByCode("PRD-001"))
                .thenReturn(false);

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Raw material with id 1 is duplicated in product composition");

        verify(productRepository).existsByCode("PRD-001");
        verify(rawMaterialRepository, never()).findById(any(Long.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenRawMaterialDoesNotExist() {
        ProductRequest request = createProductRequest();

        RawMaterial steel = createRawMaterial(
                1L,
                "MP-001",
                "Aço",
                new BigDecimal("500.000"),
                "KG"
        );

        when(productRepository.existsByCode("PRD-001"))
                .thenReturn(false);

        when(rawMaterialRepository.findById(1L))
                .thenReturn(Optional.of(steel));

        when(rawMaterialRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Raw material with id 2 was not found");

        verify(productRepository).existsByCode("PRD-001");
        verify(rawMaterialRepository).findById(1L);
        verify(rawMaterialRepository).findById(2L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldFindProductByIdSuccessfully() {
        RawMaterial steel = createRawMaterial(
                1L,
                "MP-001",
                "Aço",
                new BigDecimal("500.000"),
                "KG"
        );

        Product product = createProduct();
        setId(product, 1L);
        product.addComposition(
                new br.com.wastenio.backend.entity.ProductComposition(
                        steel,
                        new BigDecimal("15.000")
                )
        );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response = productService.findById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.code()).isEqualTo("PRD-001");
        assertThat(response.compositions()).hasSize(1);
        assertThat(response.compositions().get(0).rawMaterialId()).isEqualTo(1L);

        verify(productRepository).findById(1L);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenFindingProductByInvalidId() {
        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product with id 999 was not found");

        verify(productRepository).findById(999L);
    }

    @Test
    void shouldFindAllProducts() {
        RawMaterial steel = createRawMaterial(
                1L,
                "MP-001",
                "Aço",
                new BigDecimal("500.000"),
                "KG"
        );

        Product productOne = createProduct();
        setId(productOne, 1L);
        productOne.addComposition(
                new br.com.wastenio.backend.entity.ProductComposition(
                        steel,
                        new BigDecimal("15.000")
                )
        );

        Product productTwo = new Product(
                "PRD-002",
                "Cadeira Industrial",
                new BigDecimal("350.00")
        );
        setId(productTwo, 2L);
        productTwo.addComposition(
                new br.com.wastenio.backend.entity.ProductComposition(
                        steel,
                        new BigDecimal("8.000")
                )
        );

        when(productRepository.findAll())
                .thenReturn(List.of(productOne, productTwo));

        List<ProductResponse> response = productService.findAll();

        assertThat(response).hasSize(2);
        assertThat(response.get(0).id()).isEqualTo(1L);
        assertThat(response.get(0).code()).isEqualTo("PRD-001");
        assertThat(response.get(1).id()).isEqualTo(2L);
        assertThat(response.get(1).code()).isEqualTo("PRD-002");

        verify(productRepository).findAll();
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        Product existingProduct = createProduct();
        setId(existingProduct, 1L);

        ProductRequest request = new ProductRequest(
                "prd-001",
                "Mesa Industrial Reforçada",
                new BigDecimal("950.00"),
                List.of(
                        new ProductCompositionRequest(1L, new BigDecimal("18.000"))
                )
        );

        RawMaterial steel = createRawMaterial(
                1L,
                "MP-001",
                "Aço",
                new BigDecimal("500.000"),
                "KG"
        );

        Product updatedProduct = new Product(
                "PRD-001",
                "Mesa Industrial Reforçada",
                new BigDecimal("950.00")
        );
        setId(updatedProduct, 1L);
        updatedProduct.addComposition(
                new br.com.wastenio.backend.entity.ProductComposition(
                        steel,
                        new BigDecimal("18.000")
                )
        );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(existingProduct));

        when(productRepository.existsByCodeAndIdNot("PRD-001", 1L))
                .thenReturn(false);

        when(rawMaterialRepository.findById(1L))
                .thenReturn(Optional.of(steel));

        when(productRepository.save(existingProduct))
                .thenReturn(updatedProduct);

        ProductResponse response = productService.update(1L, request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.code()).isEqualTo("PRD-001");
        assertThat(response.name()).isEqualTo("Mesa Industrial Reforçada");
        assertThat(response.salePrice()).isEqualByComparingTo("950.00");
        assertThat(response.compositions()).hasSize(1);
        assertThat(response.compositions().get(0).requiredQuantity())
                .isEqualByComparingTo("18.000");

        verify(productRepository).findById(1L);
        verify(productRepository).existsByCodeAndIdNot("PRD-001", 1L);
        verify(rawMaterialRepository).findById(1L);
        verify(productRepository).save(existingProduct);
    }

    @Test
    void shouldThrowBusinessExceptionWhenUpdatingWithDuplicatedCode() {
        Product existingProduct = createProduct();
        setId(existingProduct, 1L);

        ProductRequest request = new ProductRequest(
                "PRD-002",
                "Mesa Industrial Reforçada",
                new BigDecimal("950.00"),
                List.of(
                        new ProductCompositionRequest(1L, new BigDecimal("18.000"))
                )
        );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(existingProduct));

        when(productRepository.existsByCodeAndIdNot("PRD-002", 1L))
                .thenReturn(true);

        assertThatThrownBy(() -> productService.update(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("A product with code 'PRD-002' already exists");

        verify(productRepository).findById(1L);
        verify(productRepository).existsByCodeAndIdNot("PRD-002", 1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        Product product = createProduct();
        setId(product, 1L);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.delete(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).delete(product);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenDeletingInvalidId() {
        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product with id 999 was not found");

        verify(productRepository).findById(999L);
        verify(productRepository, never()).delete(any(Product.class));
    }

    private ProductRequest createProductRequest() {
        return new ProductRequest(
                "prd-001",
                "Mesa Industrial",
                new BigDecimal("850.00"),
                List.of(
                        new ProductCompositionRequest(1L, new BigDecimal("15.000")),
                        new ProductCompositionRequest(2L, new BigDecimal("20.000"))
                )
        );
    }

    private Product createProduct() {
        return new Product(
                "PRD-001",
                "Mesa Industrial",
                new BigDecimal("850.00")
        );
    }

    private RawMaterial createRawMaterial(
            Long id,
            String code,
            String name,
            BigDecimal stockQuantity,
            String unit) {

        RawMaterial rawMaterial = new RawMaterial(
                code,
                name,
                stockQuantity,
                unit
        );

        setId(rawMaterial, id);

        return rawMaterial;
    }

    private void setId(Object object, Long id) {
        setField(object, "id", id);
    }

    private void setCreatedAt(Object object, LocalDateTime createdAt) {
        setField(object, "createdAt", createdAt);
    }

    private void setField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}