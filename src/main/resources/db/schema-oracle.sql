CREATE TABLE app_users (
    id NUMBER(19) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR2(50) NOT NULL UNIQUE,
    password VARCHAR2(255) NOT NULL,
    full_name VARCHAR2(100),
    email VARCHAR2(100),
    mobile_number VARCHAR2(20),
    role VARCHAR2(20) NOT NULL,
    account_locked NUMBER(1) DEFAULT 0,
    mfa_enabled NUMBER(1) DEFAULT 0,
    password_expiry_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE decompile_history (
    id NUMBER(19) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    jar_name VARCHAR2(255) NOT NULL,
    output_directory VARCHAR2(500) NOT NULL,
    decompiled_class_count NUMBER(10) DEFAULT 0,
    status VARCHAR2(50) NOT NULL,
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_by VARCHAR2(50)
);

CREATE TABLE trade_transactions (
    id NUMBER(19) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ref_number VARCHAR2(100) NOT NULL UNIQUE,
    customer_id VARCHAR2(50) NOT NULL,
    product_type VARCHAR2(50) NOT NULL,
    currency VARCHAR2(10) NOT NULL,
    amount NUMBER(18,2) NOT NULL,
    fee_amount NUMBER(18,2) NOT NULL,
    margin_pct NUMBER(5,2) NOT NULL,
    calculated_profit NUMBER(18,2) NOT NULL,
    status VARCHAR2(50) NOT NULL,
    processed_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
