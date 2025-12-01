-- Change ID column to CHAR(36)
ALTER TABLE pharmacy_medicines
    MODIFY COLUMN id CHAR(36) NOT NULL;

-- Change purchase log ID to CHAR(36)
ALTER TABLE pharmacy_purchase_log
    MODIFY COLUMN id CHAR(36) NOT NULL;

-- Change purchase_id to CHAR(36)
ALTER TABLE pharmacy_purchase_log
    MODIFY COLUMN purchase_id CHAR(36) NOT NULL;

-- Change patient_id & prescription_id to CHAR(36)
ALTER TABLE pharmacy_purchase_log
    MODIFY COLUMN patient_id CHAR(36),
    MODIFY COLUMN prescription_id CHAR(36);
