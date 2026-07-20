package br.com.wastenio.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.wastenio.backend.dto.request.RawMaterialRequest;
import br.com.wastenio.backend.dto.response.RawMaterialResponse;
import br.com.wastenio.backend.entity.RawMaterial;
import br.com.wastenio.backend.exception.BusinessException;
import br.com.wastenio.backend.exception.ResourceNotFoundException;
import br.com.wastenio.backend.mapper.RawMaterialMapper;
import br.com.wastenio.backend.repository.RawMaterialRepository;

@Service
public class RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;
    private final RawMaterialMapper rawMaterialMapper;

    public RawMaterialService(
            RawMaterialRepository rawMaterialRepository,
            RawMaterialMapper rawMaterialMapper) {

        this.rawMaterialRepository = rawMaterialRepository;
        this.rawMaterialMapper = rawMaterialMapper;
    }

    @Transactional
    public RawMaterialResponse create(RawMaterialRequest request) {
        String normalizedCode = rawMaterialMapper.normalizeCode(request.code());

        validateDuplicatedCode(normalizedCode);

        RawMaterial rawMaterial = rawMaterialMapper.toEntity(request);

        RawMaterial savedRawMaterial = rawMaterialRepository.save(rawMaterial);

        return rawMaterialMapper.toResponse(savedRawMaterial);
    }

    @Transactional(readOnly = true)
    public List<RawMaterialResponse> findAll() {
        return rawMaterialRepository.findAll()
                .stream()
                .map(rawMaterialMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RawMaterialResponse findById(Long id) {
        RawMaterial rawMaterial = findEntityById(id);

        return rawMaterialMapper.toResponse(rawMaterial);
    }

    @Transactional
    public RawMaterialResponse update(Long id, RawMaterialRequest request) {
        RawMaterial rawMaterial = findEntityById(id);

        String normalizedCode = rawMaterialMapper.normalizeCode(request.code());

        if (rawMaterialRepository.existsByCodeAndIdNot(normalizedCode, id)) {
            throw new BusinessException(
                    "A raw material with code '" + normalizedCode + "' already exists"
            );
        }

        rawMaterialMapper.updateEntityFromRequest(request, rawMaterial);

        RawMaterial updatedRawMaterial = rawMaterialRepository.save(rawMaterial);

        return rawMaterialMapper.toResponse(updatedRawMaterial);
    }

    @Transactional
    public void delete(Long id) {
        RawMaterial rawMaterial = findEntityById(id);

        rawMaterialRepository.delete(rawMaterial);
    }

    private RawMaterial findEntityById(Long id) {
        return rawMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Raw material with id " + id + " was not found"
                ));
    }

    private void validateDuplicatedCode(String normalizedCode) {
        if (rawMaterialRepository.existsByCode(normalizedCode)) {
            throw new BusinessException(
                    "A raw material with code '" + normalizedCode + "' already exists"
            );
        }
    }
}