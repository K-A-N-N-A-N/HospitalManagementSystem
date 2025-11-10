-- Initial schema for Hospital Management System with UUIDs

CREATE TABLE doctor (
  id CHAR(36) NOT NULL,
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
  id CHAR(36) NOT NULL,
  available BIT(1) NOT NULL,
  date DATE,
  end_time TIME(6),
  start_time TIME(6),
  doctor_id CHAR(36) NOT NULL,
  PRIMARY KEY (id),
  KEY FK_doctor_slot_doctor (doctor_id),
  CONSTRAINT FK_doctor_slot_doctor FOREIGN KEY (doctor_id) REFERENCES doctor (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE patient (
  id CHAR(36) NOT NULL,
  address VARCHAR(255),
  age INT,
  created_at DATETIME(6) NOT NULL,
  gender VARCHAR(255),
  name VARCHAR(255),
  updated_at DATETIME(6),
  active BIT(1),
  email VARCHAR(255),
  phone_number VARCHAR(255),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE appointment (
  id CHAR(36) NOT NULL,
  appointment_time DATETIME(6),
  created_at DATETIME(6) NOT NULL,
  reason TEXT,
  status VARCHAR(20),
  updated_at DATETIME(6),
  doctor_id CHAR(36) NOT NULL,
  patient_id CHAR(36) NOT NULL,
  slot_id CHAR(36),
  PRIMARY KEY (id),
  KEY FK_appointment_doctor (doctor_id),
  KEY FK_appointment_patient (patient_id),
  KEY FK_appointment_slot (slot_id),
  CONSTRAINT FK_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES doctor (id),
  CONSTRAINT FK_appointment_patient FOREIGN KEY (patient_id) REFERENCES patient (id),
  CONSTRAINT FK_appointment_slot FOREIGN KEY (slot_id) REFERENCES doctor_slot (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE prescription (
  id CHAR(36) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  dosage VARCHAR(255),
  medicine_name VARCHAR(255),
  notes VARCHAR(255),
  updated_at DATETIME(6),
  appointment_id CHAR(36) NOT NULL,
  PRIMARY KEY (id),
  KEY FK_prescription_appointment (appointment_id),
  CONSTRAINT FK_prescription_appointment FOREIGN KEY (appointment_id) REFERENCES appointment (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE prescription_item (
  id CHAR(36) NOT NULL,
  dosage VARCHAR(255),
  medicine_name VARCHAR(255),
  notes VARCHAR(255),
  prescription_id CHAR(36),
  PRIMARY KEY (id),
  KEY FK_prescription_item_prescription (prescription_id),
  CONSTRAINT FK_prescription_item_prescription FOREIGN KEY (prescription_id) REFERENCES prescription (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE patient_visit_history (
  id CHAR(36) NOT NULL,
  appointment_id CHAR(36),
  appointment_summary_json TEXT,
  created_at DATETIME(6) NOT NULL,
  patient_id CHAR(36),
  patient_name VARCHAR(255),
  updated_at DATETIME(6),
  visit_date DATETIME(6),
  PRIMARY KEY (id),
  KEY FK_visit_history_appointment (appointment_id),
  CONSTRAINT FK_visit_history_appointment FOREIGN KEY (appointment_id) REFERENCES appointment (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE audit_log (
  id CHAR(36) NOT NULL,
  action VARCHAR(255),
  created_at DATETIME(6) NOT NULL,
  performed_by VARCHAR(255),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
