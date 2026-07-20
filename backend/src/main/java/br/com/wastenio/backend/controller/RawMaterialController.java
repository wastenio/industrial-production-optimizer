package br.com.wastenio.backend.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.wastenio.backend.dto.request.RawMaterialRequest;
import br.com.wastenio.backend.dto.response.RawMaterialResponse;
import br.com.wastenio.backend.service.RawMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/raw-materials")
@Tag(
        name = "Raw Materials",
        description = "Endpoints for managing raw materials and stock quantities"
)
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    public RawMaterialController(RawMaterialService rawMaterialService) {
        this.rawMaterialService = rawMaterialService;
    }

    @PostMapping
    @Operation(
            summary = "Create a raw material",
            description = "Creates a new raw material with code, name, stock quantity and unit of measurement."
    )
    public ResponseEntity<RawMaterialResponse> create(
            @Valid @RequestBody RawMaterialRequest request) {

        RawMaterialResponse response = rawMaterialService.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping
    @Operation(
            summary = "List all raw materials",
            description = "Returns all registered raw materials."
    )
    public ResponseEntity<List<RawMaterialResponse>> findAll() {
        List<RawMaterialResponse> response = rawMaterialService.findAll();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Find raw material by ID",
            description = "Returns a raw material according to its unique identifier."
    )
    public ResponseEntity<RawMaterialResponse> findById(
            @PathVariable Long id) {

        RawMaterialResponse response = rawMaterialService.findById(id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a raw material",
            description = "Updates all editable information of an existing raw material."
    )
    public ResponseEntity<RawMaterialResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RawMaterialRequest request) {

        RawMaterialResponse response = rawMaterialService.update(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a raw material",
            description = "Deletes an existing raw material according to its unique identifier."
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rawMaterialService.delete(id);

        return ResponseEntity.noContent().build();
    }
}