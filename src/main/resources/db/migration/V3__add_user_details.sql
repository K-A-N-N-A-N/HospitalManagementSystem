CREATE TABLE users (
    id CHAR(36) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BIT(1) DEFAULT 1,
    doctor_id CHAR(36),
    patient_id CHAR(36),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),

    PRIMARY KEY (id),

    CONSTRAINT fk_user_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES doctor (id)
        ON DELETE SET NULL,

    CONSTRAINT fk_user_patient
        FOREIGN KEY (patient_id)
        REFERENCES patient (id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
