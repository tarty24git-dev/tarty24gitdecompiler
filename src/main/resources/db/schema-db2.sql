CREATE TABLE app_users (
    id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(100),
    mobile_number VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    account_locked BOOLEAN DEFAULT FALSE,
    mfa_enabled BOOLEAN DEFAULT FALSE,
    password_expiry_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE decompile_history (
    id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    jar_name VARCHAR(255) NOT NULL,
    output_directory VARCHAR(500) NOT NULL,
    decompiled_class_count INT DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_by VARCHAR(50)
);

CREATE TABLE trade_transactions (
    id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ref_number VARCHAR(100) NOT NULL UNIQUE,
    customer_id VARCHAR(50) NOT NULL,
    product_type VARCHAR(50) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    fee_amount DECIMAL(18,2) NOT NULL,
    margin_pct DECIMAL(5,2) NOT NULL,
    calculated_profit DECIMAL(18,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    processed_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
