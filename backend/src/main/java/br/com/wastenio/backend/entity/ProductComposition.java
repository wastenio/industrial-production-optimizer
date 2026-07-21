package br.com.wastenio.backend.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_composition")
public class ProductComposition {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "raw_material_id", nullable = false)
	private RawMaterial rawMaterial;

	@Column(name = "required_quantity", nullable = false, precision = 15, scale = 3)
	private BigDecimal requiredQuantity;

	public ProductComposition() {
	}

	public ProductComposition(RawMaterial rawMaterial, BigDecimal requiredQuantity) {

		this.rawMaterial = rawMaterial;
		this.requiredQuantity = requiredQuantity;
	}

	public Long getId() {
		return id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public RawMaterial getRawMaterial() {
		return rawMaterial;
	}

	public void setRawMaterial(RawMaterial rawMaterial) {
		this.rawMaterial = rawMaterial;
	}

	public BigDecimal getRequiredQuantity() {
		return requiredQuantity;
	}

	public void setRequiredQuantity(BigDecimal requiredQuantity) {
		this.requiredQuantity = requiredQuantity;
	}
}