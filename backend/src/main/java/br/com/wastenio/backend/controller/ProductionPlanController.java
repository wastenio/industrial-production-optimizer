package br.com.wastenio.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.wastenio.backend.dto.response.ProductionPlanResponse;
import br.com.wastenio.backend.service.ProductionOptimizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/production-plans")
@Tag(
        name = "Production Plans",
        description = "Endpoints for calculating optimized production plans"
)
public class ProductionPlanController {

    private final ProductionOptimizationService productionOptimizationService;

    public ProductionPlanController(
            ProductionOptimizationService productionOptimizationService) {

        this.productionOptimizationService = productionOptimizationService;
    }

    @PostMapping("/calculate")
    @Operation(
            summary = "Calculate best production plan",
            description = "Calculates the best combination of products to manufacture based on available raw material stock and product sale prices."
    )
    public ResponseEntity<ProductionPlanResponse> calculateBestProductionPlan() {
        ProductionPlanResponse response =
                productionOptimizationService.calculateBestProductionPlan();

        return ResponseEntity.ok(response);
    }
}