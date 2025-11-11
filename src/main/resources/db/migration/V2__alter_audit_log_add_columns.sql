ALTER TABLE audit_log
ADD COLUMN entity_name VARCHAR(100) AFTER id,
ADD COLUMN entity_id VARCHAR(100) AFTER entity_name,
ADD COLUMN role VARCHAR(100) AFTER performed_by,
ADD COLUMN changes TEXT AFTER role;
