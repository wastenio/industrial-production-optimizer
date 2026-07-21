package br.com.wastenio.backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.wastenio.backend.dto.response.ProductionPlanItemResponse;
import br.com.wastenio.backend.dto.response.ProductionPlanResponse;
import br.com.wastenio.backend.dto.response.RemainingRawMaterialResponse;
import br.com.wastenio.backend.entity.Product;
import br.com.wastenio.backend.entity.ProductComposition;
import br.com.wastenio.backend.entity.RawMaterial;
import br.com.wastenio.backend.repository.ProductRepository;
import br.com.wastenio.backend.repository.RawMaterialRepository;

@Service
public class ProductionOptimizationService {

    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    public ProductionOptimizationService(
            ProductRepository productRepository,
            RawMaterialRepository rawMaterialRepository) {

        this.productRepository = productRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }

    @Transactional(readOnly = true)
    public ProductionPlanResponse calculateBestProductionPlan() {
        List<Product> products = productRepository.findAll();
        List<RawMaterial> rawMaterials = rawMaterialRepository.findAll();

        if (products.isEmpty()) {
            return emptyPlan(rawMaterials);
        }

        Map<Long, BigDecimal> availableStock = buildAvailableStock(rawMaterials);

        List<Integer> maxQuantities = products.stream()
                .map(product -> calculateMaxQuantityForProduct(product, availableStock))
                .toList();

        BestPlanHolder bestPlanHolder = new BestPlanHolder(products.size());

        searchBestCombination(
                products,
                availableStock,
                maxQuantities,
                0,
                new int[products.size()],
                bestPlanHolder
        );

        return buildResponse(
                products,
                rawMaterials,
                bestPlanHolder.quantities
        );
    }

    private Map<Long, BigDecimal> buildAvailableStock(List<RawMaterial> rawMaterials) {
        Map<Long, BigDecimal> availableStock = new HashMap<>();

        for (RawMaterial rawMaterial : rawMaterials) {
            availableStock.put(
                    rawMaterial.getId(),
                    rawMaterial.getStockQuantity()
            );
        }

        return availableStock;
    }

    private Integer calculateMaxQuantityForProduct(
            Product product,
            Map<Long, BigDecimal> availableStock) {

        Integer maxQuantity = null;

        for (ProductComposition composition : product.getCompositions()) {
            Long rawMaterialId = composition.getRawMaterial().getId();

            BigDecimal availableQuantity = availableStock.getOrDefault(
                    rawMaterialId,
                    BigDecimal.ZERO
            );

            BigDecimal requiredQuantity = composition.getRequiredQuantity();

            if (requiredQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                return 0;
            }

            int possibleQuantity = availableQuantity
                    .divideToIntegralValue(requiredQuantity)
                    .intValue();

            if (maxQuantity == null || possibleQuantity < maxQuantity) {
                maxQuantity = possibleQuantity;
            }
        }

        return Objects.requireNonNullElse(maxQuantity, 0);
    }

    private void searchBestCombination(
            List<Product> products,
            Map<Long, BigDecimal> availableStock,
            List<Integer> maxQuantities,
            int productIndex,
            int[] currentQuantities,
            BestPlanHolder bestPlanHolder) {

        if (productIndex == products.size()) {
            if (isValidCombination(products, availableStock, currentQuantities)) {
                BigDecimal totalSaleValue = calculateTotalSaleValue(
                        products,
                        currentQuantities
                );

                int totalUnits = calculateTotalUnits(currentQuantities);

                if (isBetterPlan(
                        totalSaleValue,
                        totalUnits,
                        bestPlanHolder.totalSaleValue,
                        bestPlanHolder.totalUnits)) {

                    bestPlanHolder.totalSaleValue = totalSaleValue;
                    bestPlanHolder.totalUnits = totalUnits;
                    bestPlanHolder.quantities = currentQuantities.clone();
                }
            }

            return;
        }

        int maxQuantity = maxQuantities.get(productIndex);

        for (int quantity = 0; quantity <= maxQuantity; quantity++) {
            currentQuantities[productIndex] = quantity;

            if (isPartialCombinationValid(
                    products,
                    availableStock,
                    currentQuantities,
                    productIndex)) {

                searchBestCombination(
                        products,
                        availableStock,
                        maxQuantities,
                        productIndex + 1,
                        currentQuantities,
                        bestPlanHolder
                );
            }
        }

        currentQuantities[productIndex] = 0;
    }

    private boolean isPartialCombinationValid(
            List<Product> products,
            Map<Long, BigDecimal> availableStock,
            int[] quantities,
            int currentProductIndex) {

        Map<Long, BigDecimal> usedRawMaterials = new HashMap<>();

        for (int index = 0; index <= currentProductIndex; index++) {
            Product product = products.get(index);
            int quantity = quantities[index];

            addProductConsumption(
                    usedRawMaterials,
                    product,
                    quantity
            );
        }

        return isWithinAvailableStock(usedRawMaterials, availableStock);
    }

    private boolean isValidCombination(
            List<Product> products,
            Map<Long, BigDecimal> availableStock,
            int[] quantities) {

        Map<Long, BigDecimal> usedRawMaterials = calculateUsedRawMaterials(
                products,
                quantities
        );

        return isWithinAvailableStock(usedRawMaterials, availableStock);
    }

    private Map<Long, BigDecimal> calculateUsedRawMaterials(
            List<Product> products,
            int[] quantities) {

        Map<Long, BigDecimal> usedRawMaterials = new HashMap<>();

        for (int index = 0; index < products.size(); index++) {
            Product product = products.get(index);
            int quantity = quantities[index];

            addProductConsumption(
                    usedRawMaterials,
                    product,
                    quantity
            );
        }

        return usedRawMaterials;
    }

    private void addProductConsumption(
            Map<Long, BigDecimal> usedRawMaterials,
            Product product,
            int quantity) {

        if (quantity <= 0) {
            return;
        }

        for (ProductComposition composition : product.getCompositions()) {
            Long rawMaterialId = composition.getRawMaterial().getId();

            BigDecimal consumedQuantity = composition.getRequiredQuantity()
                    .multiply(BigDecimal.valueOf(quantity));

            usedRawMaterials.merge(
                    rawMaterialId,
                    consumedQuantity,
                    BigDecimal::add
            );
        }
    }

    private boolean isWithinAvailableStock(
            Map<Long, BigDecimal> usedRawMaterials,
            Map<Long, BigDecimal> availableStock) {

        for (Map.Entry<Long, BigDecimal> entry : usedRawMaterials.entrySet()) {
            Long rawMaterialId = entry.getKey();
            BigDecimal usedQuantity = entry.getValue();

            BigDecimal availableQuantity = availableStock.getOrDefault(
                    rawMaterialId,
                    BigDecimal.ZERO
            );

            if (usedQuantity.compareTo(availableQuantity) > 0) {
                return false;
            }
        }

        return true;
    }

    private BigDecimal calculateTotalSaleValue(
            List<Product> products,
            int[] quantities) {

        BigDecimal total = BigDecimal.ZERO;

        for (int index = 0; index < products.size(); index++) {
            Product product = products.get(index);
            int quantity = quantities[index];

            BigDecimal productTotal = product.getSalePrice()
                    .multiply(BigDecimal.valueOf(quantity));

            total = total.add(productTotal);
        }

        return total;
    }

    private int calculateTotalUnits(int[] quantities) {
        int totalUnits = 0;

        for (int quantity : quantities) {
            totalUnits += quantity;
        }

        return totalUnits;
    }

    private boolean isBetterPlan(
            BigDecimal candidateTotalSaleValue,
            int candidateTotalUnits,
            BigDecimal currentBestTotalSaleValue,
            int currentBestTotalUnits) {

        int valueComparison = candidateTotalSaleValue.compareTo(currentBestTotalSaleValue);

        if (valueComparison > 0) {
            return true;
        }

        if (valueComparison < 0) {
            return false;
        }

        return candidateTotalUnits > currentBestTotalUnits;
    }

    private ProductionPlanResponse buildResponse(
            List<Product> products,
            List<RawMaterial> rawMaterials,
            int[] quantities) {

        List<ProductionPlanItemResponse> items = buildItems(
                products,
                quantities
        );

        Map<Long, BigDecimal> usedRawMaterials = calculateUsedRawMaterials(
                products,
                quantities
        );

        List<RemainingRawMaterialResponse> remainingRawMaterials =
                buildRemainingRawMaterials(
                        rawMaterials,
                        usedRawMaterials
                );

        BigDecimal totalSaleValue = items.stream()
                .map(ProductionPlanItemResponse::totalSaleValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalProductionUnits = items.stream()
                .mapToInt(ProductionPlanItemResponse::quantity)
                .sum();

        return new ProductionPlanResponse(
                items,
                totalProductionUnits,
                totalSaleValue,
                remainingRawMaterials
        );
    }

    private List<ProductionPlanItemResponse> buildItems(
            List<Product> products,
            int[] quantities) {

        List<ProductionPlanItemResponse> items = new ArrayList<>();

        for (int index = 0; index < products.size(); index++) {
            Product product = products.get(index);
            int quantity = quantities[index];

            if (quantity <= 0) {
                continue;
            }

            BigDecimal totalSaleValue = product.getSalePrice()
                    .multiply(BigDecimal.valueOf(quantity));

            items.add(new ProductionPlanItemResponse(
                    product.getId(),
                    product.getCode(),
                    product.getName(),
                    quantity,
                    product.getSalePrice(),
                    totalSaleValue
            ));
        }

        return items.stream()
                .sorted(Comparator.comparing(ProductionPlanItemResponse::productCode))
                .toList();
    }

    private List<RemainingRawMaterialResponse> buildRemainingRawMaterials(
            List<RawMaterial> rawMaterials,
            Map<Long, BigDecimal> usedRawMaterials) {

        return rawMaterials.stream()
                .map(rawMaterial -> {
                    BigDecimal initialQuantity = rawMaterial.getStockQuantity();

                    BigDecimal usedQuantity = usedRawMaterials.getOrDefault(
                            rawMaterial.getId(),
                            BigDecimal.ZERO
                    );

                    BigDecimal remainingQuantity = initialQuantity.subtract(usedQuantity);

                    return new RemainingRawMaterialResponse(
                            rawMaterial.getId(),
                            rawMaterial.getCode(),
                            rawMaterial.getName(),
                            initialQuantity,
                            usedQuantity,
                            remainingQuantity,
                            rawMaterial.getUnit()
                    );
                })
                .sorted(Comparator.comparing(RemainingRawMaterialResponse::rawMaterialCode))
                .toList();
    }

    private ProductionPlanResponse emptyPlan(List<RawMaterial> rawMaterials) {
        List<RemainingRawMaterialResponse> remainingRawMaterials =
                buildRemainingRawMaterials(
                        rawMaterials,
                        Map.of()
                );

        return new ProductionPlanResponse(
                List.of(),
                0,
                BigDecimal.ZERO,
                remainingRawMaterials
        );
    }

    private static class BestPlanHolder {

        private int[] quantities;
        private BigDecimal totalSaleValue;
        private int totalUnits;

        private BestPlanHolder(int productCount) {
            this.quantities = new int[productCount];
            this.totalSaleValue = BigDecimal.ZERO;
            this.totalUnits = 0;
        }
    }
}