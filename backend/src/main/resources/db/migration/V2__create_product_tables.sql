CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    sale_price NUMERIC(15, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE product_composition (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    raw_material_id BIGINT NOT NULL,
    required_quantity NUMERIC(15, 3) NOT NULL,

    CONSTRAINT fk_product_composition_product
        FOREIGN KEY (product_id)
        REFERENCES product (id),

    CONSTRAINT fk_product_composition_raw_material
        FOREIGN KEY (raw_material_id)
        REFERENCES raw_material (id),

    CONSTRAINT uk_product_composition_product_raw_material
        UNIQUE (product_id, raw_material_id)
);