package br.com.wastenio.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.wastenio.backend.dto.response.ProductionPlanResponse;
import br.com.wastenio.backend.entity.Product;
import br.com.wastenio.backend.entity.ProductComposition;
import br.com.wastenio.backend.entity.RawMaterial;
import br.com.wastenio.backend.repository.ProductRepository;
import br.com.wastenio.backend.repository.RawMaterialRepository;

@ExtendWith(MockitoExtension.class)
class ProductionOptimizationServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    private ProductionOptimizationService productionOptimizationService;

    @BeforeEach
    void setUp() {
        productionOptimizationService = new ProductionOptimizationService(
                productRepository,
                rawMaterialRepository
        );
    }

    @Test
    void shouldCalculateBestProductionPlanSuccessfully() {
        RawMaterial steel = createRawMaterial(
                1L,
                "MP-001",
                "Aço",
                new BigDecimal("500.000"),
                "KG"
        );

        RawMaterial aluminum = createRawMaterial(
                2L,
                "MP-002",
                "Alumínio",
                new BigDecimal("250.000"),
                "KG"
        );

        Product table = createProduct(
                1L,
                "PRD-001",
                "Mesa Industrial",
                new BigDecimal("850.00")
        );

        table.addComposition(
                new ProductComposition(
                        steel,
                        new BigDecimal("15.000")
                )
        );

        table.addComposition(
                new ProductComposition(
                        aluminum,
                        new BigDecimal("20.000")
                )
        );

        when(productRepository.findAll())
                .thenReturn(List.of(table));

        when(rawMaterialRepository.findAll())
                .thenReturn(List.of(steel, aluminum));

        ProductionPlanResponse response =
                productionOptimizationService.calculateBestProductionPlan();

        assertThat(response).isNotNull();
        assertThat(response.items()).hasSize(1);

        assertThat(response.items().get(0).productId()).isEqualTo(1L);
        assertThat(response.items().get(0).productCode()).isEqualTo("PRD-001");
        assertThat(response.items().get(0).productName()).isEqualTo("Mesa Industrial");
        assertThat(response.items().get(0).quantity()).isEqualTo(12);
        assertThat(response.items().get(0).unitSalePrice()).isEqualByComparingTo("850.00");
        assertThat(response.items().get(0).totalSaleValue()).isEqualByComparingTo("10200.00");

        assertThat(response.totalProductionUnits()).isEqualTo(12);
        assertThat(response.totalSaleValue()).isEqualByComparingTo("10200.00");

        assertThat(response.remainingRawMaterials()).hasSize(2);

        assertThat(response.remainingRawMaterials().get(0).rawMaterialCode()).isEqualTo("MP-001");
        assertThat(response.remainingRawMaterials().get(0).usedQuantity()).isEqualByComparingTo("180.000");
        assertThat(response.remainingRawMaterials().get(0).remainingQuantity()).isEqualByComparingTo("320.000");

        assertThat(response.remainingRawMaterials().get(1).rawMaterialCode()).isEqualTo("MP-002");
        assertThat(response.remainingRawMaterials().get(1).usedQuantity()).isEqualByComparingTo("240.000");
        assertThat(response.remainingRawMaterials().get(1).remainingQuantity()).isEqualByComparingTo("10.000");

        verify(productRepository).findAll();
        verify(rawMaterialRepository).findAll();
    }

    @Test
    void shouldChooseCombinationWithHighestTotalSaleValue() {
        RawMaterial wood = createRawMaterial(
                1L,
                "MP-010",
                "Madeira",
                new BigDecimal("10.000"),
                "M"
        );

        Product expensiveProduct = createProduct(
                1L,
                "PRD-010",
                "Produto Caro",
                new BigDecimal("1000.00")
        );

        expensiveProduct.addComposition(
                new ProductComposition(
                        wood,
                        new BigDecimal("10.000")
                )
        );

        Product profitableProduct = createProduct(
                2L,
                "PRD-011",
                "Produto Mais Rentável",
                new BigDecimal("600.00")
        );

        profitableProduct.addComposition(
                new ProductComposition(
                        wood,
                        new BigDecimal("5.000")
                )
        );

        when(productRepository.findAll())
                .thenReturn(List.of(expensiveProduct, profitableProduct));

        when(rawMaterialRepository.findAll())
                .thenReturn(List.of(wood));

        ProductionPlanResponse response =
                productionOptimizationService.calculateBestProductionPlan();

        assertThat(response).isNotNull();
        assertThat(response.items()).hasSize(1);

        assertThat(response.items().get(0).productCode()).isEqualTo("PRD-011");
        assertThat(response.items().get(0).quantity()).isEqualTo(2);
        assertThat(response.items().get(0).totalSaleValue()).isEqualByComparingTo("1200.00");

        assertThat(response.totalProductionUnits()).isEqualTo(2);
        assertThat(response.totalSaleValue()).isEqualByComparingTo("1200.00");

        assertThat(response.remainingRawMaterials()).hasSize(1);
        assertThat(response.remainingRawMaterials().get(0).usedQuantity()).isEqualByComparingTo("10.000");
        assertThat(response.remainingRawMaterials().get(0).remainingQuantity()).isEqualByComparingTo("0.000");

        verify(productRepository).findAll();
        verify(rawMaterialRepository).findAll();
    }

    @Test
    void shouldReturnEmptyPlanWhenThereAreNoProducts() {
        RawMaterial steel = createRawMaterial(
                1L,
                "MP-001",
                "Aço",
                new BigDecimal("500.000"),
                "KG"
        );

        when(productRepository.findAll())
                .thenReturn(List.of());

        when(rawMaterialRepository.findAll())
                .thenReturn(List.of(steel));

        ProductionPlanResponse response =
                productionOptimizationService.calculateBestProductionPlan();

        assertThat(response).isNotNull();
        assertThat(response.items()).isEmpty();
        assertThat(response.totalProductionUnits()).isZero();
        assertThat(response.totalSaleValue()).isEqualByComparingTo("0");

        assertThat(response.remainingRawMaterials()).hasSize(1);
        assertThat(response.remainingRawMaterials().get(0).rawMaterialCode()).isEqualTo("MP-001");
        assertThat(response.remainingRawMaterials().get(0).usedQuantity()).isEqualByComparingTo("0");
        assertThat(response.remainingRawMaterials().get(0).remainingQuantity()).isEqualByComparingTo("500.000");

        verify(productRepository).findAll();
        verify(rawMaterialRepository).findAll();
    }

    @Test
    void shouldReturnEmptyItemsWhenStockIsInsufficient() {
        RawMaterial steel = createRawMaterial(
                1L,
                "MP-001",
                "Aço",
                new BigDecimal("5.000"),
                "KG"
        );

        Product table = createProduct(
                1L,
                "PRD-001",
                "Mesa Industrial",
                new BigDecimal("850.00")
        );

        table.addComposition(
                new ProductComposition(
                        steel,
                        new BigDecimal("15.000")
                )
        );

        when(productRepository.findAll())
                .thenReturn(List.of(table));

        when(rawMaterialRepository.findAll())
                .thenReturn(List.of(steel));

        ProductionPlanResponse response =
                productionOptimizationService.calculateBestProductionPlan();

        assertThat(response).isNotNull();
        assertThat(response.items()).isEmpty();
        assertThat(response.totalProductionUnits()).isZero();
        assertThat(response.totalSaleValue()).isEqualByComparingTo("0");

        assertThat(response.remainingRawMaterials()).hasSize(1);
        assertThat(response.remainingRawMaterials().get(0).usedQuantity()).isEqualByComparingTo("0");
        assertThat(response.remainingRawMaterials().get(0).remainingQuantity()).isEqualByComparingTo("5.000");

        verify(productRepository).findAll();
        verify(rawMaterialRepository).findAll();
    }

    private Product createProduct(
            Long id,
            String code,
            String name,
            BigDecimal salePrice) {

        Product product = new Product(
                code,
                name,
                salePrice
        );

        setId(product, id);

        return product;
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