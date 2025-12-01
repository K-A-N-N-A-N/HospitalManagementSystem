-- ============================================
-- 1. Pharmacy Medicines Table
-- ============================================
CREATE TABLE pharmacy_medicines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255),
    unit VARCHAR(50),            -- Tablet, ml, mg, etc.
    strength VARCHAR(50),        -- 500mg, 10ml, etc.
    price DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    stock INT NOT NULL DEFAULT 0,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    version BIGINT DEFAULT 0     -- optional optimistic locking
);

-- ============================================
-- 2. Purchase Log Table
-- ============================================
CREATE TABLE pharmacy_purchase_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    purchase_id VARCHAR(100) NOT NULL,
    patient_id VARCHAR(100),
    prescription_id VARCHAR(100),

    request_payload JSON,
    response_payload JSON,

    status VARCHAR(50),     -- SUCCESS, FAILURE, PARTIAL_FAILURE
    error_message TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for faster lookup
CREATE INDEX idx_purchase_log_purchase_id ON pharmacy_purchase_log(purchase_id);
