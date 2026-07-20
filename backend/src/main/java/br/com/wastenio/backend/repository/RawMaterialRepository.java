package br.com.wastenio.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.wastenio.backend.entity.RawMaterial;

public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {

    Optional<RawMaterial> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);
}