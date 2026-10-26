CREATE TABLE app_users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(100),
    mobile_number VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    account_locked BIT DEFAULT 0,
    mfa_enabled BIT DEFAULT 0,
    password_expiry_date DATETIME2,
    created_at DATETIME2 DEFAULT GETDATE()
);

CREATE TABLE decompile_history (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    jar_name VARCHAR(255) NOT NULL,
    output_directory VARCHAR(500) NOT NULL,
    decompiled_class_count INT DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    processed_at DATETIME2 DEFAULT GETDATE(),
    processed_by VARCHAR(50)
);

CREATE TABLE trade_transactions (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ref_number VARCHAR(100) NOT NULL UNIQUE,
    customer_id VARCHAR(50) NOT NULL,
    product_type VARCHAR(50) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    fee_amount DECIMAL(18,2) NOT NULL,
    margin_pct DECIMAL(5,2) NOT NULL,
    calculated_profit DECIMAL(18,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    processed_date DATETIME2 DEFAULT GETDATE()
);
