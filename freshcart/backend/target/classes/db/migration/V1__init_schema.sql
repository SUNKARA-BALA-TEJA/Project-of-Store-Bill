CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    account_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE store_settings (
    id BIGSERIAL PRIMARY KEY,
    store_name VARCHAR(100) NOT NULL DEFAULT 'FreshCart',
    logo_path VARCHAR(255),
    currency VARCHAR(10) NOT NULL DEFAULT 'INR',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    image_path VARCHAR(255),
    unit VARCHAR(20) NOT NULL,
    current_selling_price DECIMAL(10,2) NOT NULL,
    minimum_stock_threshold INTEGER DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_products_name ON products(name);

CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT UNIQUE NOT NULL REFERENCES products(id),
    current_stock DECIMAL(10,3) NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE stock_movements (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id),
    movement_type VARCHAR(20) NOT NULL,
    quantity_change DECIMAL(10,3) NOT NULL,
    operator_username VARCHAR(50),
    related_invoice_number VARCHAR(50),
    reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bills (
    id BIGSERIAL PRIMARY KEY,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    user_id BIGINT REFERENCES users(id),
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0,
    charges DECIMAL(10,2) NOT NULL DEFAULT 0,
    discount DECIMAL(10,2) NOT NULL DEFAULT 0,
    final_total DECIMAL(10,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE INDEX idx_bills_created_at ON bills(created_at);
CREATE INDEX idx_bills_invoice ON bills(invoice_number);

CREATE TABLE bill_items (
    id BIGSERIAL PRIMARY KEY,
    bill_id BIGINT NOT NULL REFERENCES bills(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES products(id),
    product_name_snapshot VARCHAR(100) NOT NULL,
    unit_snapshot VARCHAR(20) NOT NULL,
    quantity DECIMAL(10,3) NOT NULL,
    actual_unit_price_charged DECIMAL(10,2) NOT NULL,
    line_subtotal DECIMAL(10,2) NOT NULL
);

CREATE TABLE price_history (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id),
    previous_price DECIMAL(10,2),
    new_price DECIMAL(10,2) NOT NULL,
    changed_by VARCHAR(50),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO store_settings (store_name) VALUES ('FreshCart');
