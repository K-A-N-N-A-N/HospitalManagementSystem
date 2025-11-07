# 🏥 Hospital Management System

## 📘 Description
The **Hospital Management System** is a Spring Boot–based application designed to streamline hospital operations including doctor scheduling, appointment management, prescriptions, and patient visit history.  
It ensures efficient coordination between doctors, patients, and administrative workflows using a RESTful architecture and DTO-driven data exchange.

---

## ⚙️ Tech Stack
- **Backend:** Spring Boot, Spring Data JPA, Maven
- **Database:** MySQL
- **Tools:** Swagger (API Testing), Lombok (Boilerplate Reduction)
- **Language:** Java 17

---

## 🧩 Core Modules
- **Doctor Management:** Create and manage doctor profiles with specialization and contact info.
- **Doctor Slot Management:** Automatically generate and manage available time slots.
- **Appointment Scheduling:** Book, validate, and manage appointments linked to doctor slots.
- **Prescription Module:** Create prescriptions tied to completed appointments.
- **Patient Visit History:** Track historical appointments and medical records per patient.

---

## 🚀 Key Features
- Real-time slot availability check during appointment booking
- Automatic appointment status update upon prescription generation
- DTO-based data handling to prevent lazy-loading issues
- Exception handling for booking conflicts and invalid slot selections
- Swagger UI integration for API visualization and testing

---

## 🗂️ Project Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/hospital-management-system.git
2. **Navigate to the project directory**
   ```bash
    cd hospital-management-system

3. **Build and run**
   ```bash
    mvn clean install
    mvn spring-boot:run


4. **Access API Documentation**
    http://localhost:8080/swagger-ui/index.html