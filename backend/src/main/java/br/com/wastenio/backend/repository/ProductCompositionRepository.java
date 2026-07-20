package br.com.wastenio.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.wastenio.backend.entity.ProductComposition;

public interface ProductCompositionRepository extends JpaRepository<ProductComposition, Long> {
}