-- Initial schema for Hospital Management System

CREATE TABLE doctor (
  id BIGINT NOT NULL AUTO_INCREMENT,
  created_at DATETIME(6) NOT NULL,
  name VARCHAR(255),
  photo_path VARCHAR(255),
  specialization VARCHAR(255),
  updated_at DATETIME(6),
  active BIT(1),
  email VARCHAR(255),
  phone_number VARCHAR(255),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE doctor_slot (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  available BIT(1) NOT NULL,
  date DATE,
  end_time TIME(6),
  start_time TIME(6),
  doctor_id BIGINT NOT NULL,
  KEY FK_doctor_slot_doctor (doctor_id),
  CONSTRAINT FK_doctor_slot_doctor FOREIGN KEY (doctor_id) REFERENCES doctor (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE patient (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  address VARCHAR(255),
  age INT,
  created_at DATETIME(6) NOT NULL,
  gender VARCHAR(255),
  name VARCHAR(255),
  updated_at DATETIME(6),
  active BIT(1),
  email VARCHAR(255),
  phone_number VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE appointment (
  id BIGINT NOT NULL AUTO_INCREMENT,
  appointment_time DATETIME(6),
  created_at DATETIME(6) NOT NULL,
  reason TEXT,
  status VARCHAR(20),
  updated_at DATETIME(6),
  doctor_id BIGINT NOT NULL,
  patient_id BIGINT NOT NULL,
  slot_id BIGINT,
  PRIMARY KEY (id),
  KEY FK_appointment_doctor (doctor_id),
  KEY FK_appointment_patient (patient_id),
  KEY FK_appointment_slot (slot_id),
  CONSTRAINT FK_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES doctor (id),
  CONSTRAINT FK_appointment_patient FOREIGN KEY (patient_id) REFERENCES patient (id),
  CONSTRAINT FK_appointment_slot FOREIGN KEY (slot_id) REFERENCES doctor_slot (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE prescription (
  id BIGINT NOT NULL AUTO_INCREMENT,
  created_at DATETIME(6) NOT NULL,
  dosage VARCHAR(255),
  medicine_name VARCHAR(255),
  notes VARCHAR(255),
  updated_at DATETIME(6),
  appointment_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  KEY FK_prescription_appointment (appointment_id),
  CONSTRAINT FK_prescription_appointment FOREIGN KEY (appointment_id) REFERENCES appointment (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE prescription_item (
  id BIGINT NOT NULL AUTO_INCREMENT,
  dosage VARCHAR(255),
  medicine_name VARCHAR(255),
  notes VARCHAR(255),
  prescription_id BIGINT,
  PRIMARY KEY (id),
  KEY FK_prescription_item_prescription (prescription_id),
  CONSTRAINT FK_prescription_item_prescription FOREIGN KEY (prescription_id) REFERENCES prescription (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE patient_visit_history (
  id BIGINT NOT NULL AUTO_INCREMENT,
  appointment_id BIGINT,
  appointment_summary_json TEXT,
  created_at DATETIME(6) NOT NULL,
  patient_id BIGINT,
  patient_name VARCHAR(255),
  updated_at DATETIME(6),
  visit_date DATETIME(6),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE audit_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  action VARCHAR(255),
  created_at DATETIME(6) NOT NULL,
  performed_by VARCHAR(255),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
