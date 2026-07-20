package br.com.wastenio.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.wastenio.backend.dto.request.RawMaterialRequest;
import br.com.wastenio.backend.dto.response.RawMaterialResponse;
import br.com.wastenio.backend.entity.RawMaterial;
import br.com.wastenio.backend.exception.BusinessException;
import br.com.wastenio.backend.exception.ResourceNotFoundException;
import br.com.wastenio.backend.mapper.RawMaterialMapper;
import br.com.wastenio.backend.repository.RawMaterialRepository;

@ExtendWith(MockitoExtension.class)
class RawMaterialServiceTest {

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    private RawMaterialMapper rawMaterialMapper;

    private RawMaterialService rawMaterialService;

    @BeforeEach
    void setUp() {
        rawMaterialMapper = new RawMaterialMapper();
        rawMaterialService = new RawMaterialService(
                rawMaterialRepository,
                rawMaterialMapper
        );
    }

    @Test
    void shouldCreateRawMaterialSuccessfully() {
        RawMaterialRequest request = new RawMaterialRequest(
                "mp-001",
                "Aço",
                new BigDecimal("500.000"),
                "kg"
        );

        RawMaterial savedRawMaterial = createRawMaterial();
        setId(savedRawMaterial, 1L);
        setCreatedAt(savedRawMaterial, LocalDateTime.of(2026, 7, 13, 14, 0));

        when(rawMaterialRepository.existsByCode("MP-001"))
                .thenReturn(false);

        when(rawMaterialRepository.save(any(RawMaterial.class)))
                .thenReturn(savedRawMaterial);

        RawMaterialResponse response = rawMaterialService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.code()).isEqualTo("MP-001");
        assertThat(response.name()).isEqualTo("Aço");
        assertThat(response.stockQuantity()).isEqualByComparingTo("500.000");
        assertThat(response.unit()).isEqualTo("KG");

        verify(rawMaterialRepository).existsByCode("MP-001");
        verify(rawMaterialRepository).save(any(RawMaterial.class));
    }

    @Test
    void shouldThrowBusinessExceptionWhenCreatingWithDuplicatedCode() {
        RawMaterialRequest request = new RawMaterialRequest(
                "MP-001",
                "Aço",
                new BigDecimal("500.000"),
                "KG"
        );

        when(rawMaterialRepository.existsByCode("MP-001"))
                .thenReturn(true);

        assertThatThrownBy(() -> rawMaterialService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("A raw material with code 'MP-001' already exists");

        verify(rawMaterialRepository).existsByCode("MP-001");
        verify(rawMaterialRepository, never()).save(any(RawMaterial.class));
    }

    @Test
    void shouldFindAllRawMaterials() {
        RawMaterial rawMaterialOne = createRawMaterial();
        setId(rawMaterialOne, 1L);

        RawMaterial rawMaterialTwo = new RawMaterial(
                "MP-002",
                "Alumínio",
                new BigDecimal("250.000"),
                "KG"
        );
        setId(rawMaterialTwo, 2L);

        when(rawMaterialRepository.findAll())
                .thenReturn(List.of(rawMaterialOne, rawMaterialTwo));

        List<RawMaterialResponse> response = rawMaterialService.findAll();

        assertThat(response).hasSize(2);

        assertThat(response.get(0).id()).isEqualTo(1L);
        assertThat(response.get(0).code()).isEqualTo("MP-001");

        assertThat(response.get(1).id()).isEqualTo(2L);
        assertThat(response.get(1).code()).isEqualTo("MP-002");

        verify(rawMaterialRepository).findAll();
    }

    @Test
    void shouldFindRawMaterialByIdSuccessfully() {
        RawMaterial rawMaterial = createRawMaterial();
        setId(rawMaterial, 1L);

        when(rawMaterialRepository.findById(1L))
                .thenReturn(Optional.of(rawMaterial));

        RawMaterialResponse response = rawMaterialService.findById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.code()).isEqualTo("MP-001");
        assertThat(response.name()).isEqualTo("Aço");

        verify(rawMaterialRepository).findById(1L);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenFindingByInvalidId() {
        when(rawMaterialRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> rawMaterialService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Raw material with id 999 was not found");

        verify(rawMaterialRepository).findById(999L);
    }

    @Test
    void shouldUpdateRawMaterialSuccessfully() {
        RawMaterial existingRawMaterial = createRawMaterial();
        setId(existingRawMaterial, 1L);

        RawMaterialRequest request = new RawMaterialRequest(
                "mp-001",
                "Aço carbono",
                new BigDecimal("450.500"),
                "kg"
        );

        when(rawMaterialRepository.findById(1L))
                .thenReturn(Optional.of(existingRawMaterial));

        when(rawMaterialRepository.existsByCodeAndIdNot("MP-001", 1L))
                .thenReturn(false);

        when(rawMaterialRepository.save(existingRawMaterial))
                .thenReturn(existingRawMaterial);

        RawMaterialResponse response = rawMaterialService.update(1L, request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.code()).isEqualTo("MP-001");
        assertThat(response.name()).isEqualTo("Aço carbono");
        assertThat(response.stockQuantity()).isEqualByComparingTo("450.500");
        assertThat(response.unit()).isEqualTo("KG");

        verify(rawMaterialRepository).findById(1L);
        verify(rawMaterialRepository).existsByCodeAndIdNot("MP-001", 1L);
        verify(rawMaterialRepository).save(existingRawMaterial);
    }

    @Test
    void shouldThrowBusinessExceptionWhenUpdatingWithDuplicatedCode() {
        RawMaterial existingRawMaterial = createRawMaterial();
        setId(existingRawMaterial, 1L);

        RawMaterialRequest request = new RawMaterialRequest(
                "MP-002",
                "Aço carbono",
                new BigDecimal("450.500"),
                "KG"
        );

        when(rawMaterialRepository.findById(1L))
                .thenReturn(Optional.of(existingRawMaterial));

        when(rawMaterialRepository.existsByCodeAndIdNot("MP-002", 1L))
                .thenReturn(true);

        assertThatThrownBy(() -> rawMaterialService.update(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("A raw material with code 'MP-002' already exists");

        verify(rawMaterialRepository).findById(1L);
        verify(rawMaterialRepository).existsByCodeAndIdNot("MP-002", 1L);
        verify(rawMaterialRepository, never()).save(any(RawMaterial.class));
    }

    @Test
    void shouldDeleteRawMaterialSuccessfully() {
        RawMaterial rawMaterial = createRawMaterial();
        setId(rawMaterial, 1L);

        when(rawMaterialRepository.findById(1L))
                .thenReturn(Optional.of(rawMaterial));

        rawMaterialService.delete(1L);

        verify(rawMaterialRepository).findById(1L);
        verify(rawMaterialRepository).delete(rawMaterial);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenDeletingInvalidId() {
        when(rawMaterialRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> rawMaterialService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Raw material with id 999 was not found");

        verify(rawMaterialRepository).findById(999L);
        verify(rawMaterialRepository, never()).delete(any(RawMaterial.class));
    }

    private RawMaterial createRawMaterial() {
        return new RawMaterial(
                "MP-001",
                "Aço",
                new BigDecimal("500.000"),
                "KG"
        );
    }

    private void setId(RawMaterial rawMaterial, Long id) {
        try {
            java.lang.reflect.Field field = RawMaterial.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(rawMaterial, id);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private void setCreatedAt(
            RawMaterial rawMaterial,
            LocalDateTime createdAt) {

        try {
            java.lang.reflect.Field field = RawMaterial.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(rawMaterial, createdAt);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}