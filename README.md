# Smart Clinic Management App

A full-stack clinic management system built with Spring Boot and Vanilla JavaScript, enabling admins, doctors, and patients to manage appointments, prescriptions, and doctor records.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java Spring Boot |
| Relational DB | MySQL (via Docker) |
| Document DB | MongoDB |
| ORM | Spring Data JPA (Hibernate) |
| Authentication | JWT (jjwt 0.12.3) |
| Frontend | Vanilla JavaScript (ES Modules) |
| Templating | Thymeleaf |
| Containerization | Docker |

---

## Project Structure

```
back-end/
├── src/main/java/com/project/back_end/
│   ├── controllers/
│   │   ├── AdminController.java
│   │   ├── AppointmentController.java
│   │   ├── DashboardController.java
│   │   ├── DoctorController.java
│   │   ├── PatientController.java
│   │   └── PrescriptionController.java
│   ├── DTO/
│   │   ├── AppointmentDTO.java
│   │   └── Login.java
│   ├── models/
│   │   ├── Admin.java
│   │   ├── Appointment.java
│   │   ├── Doctor.java
│   │   ├── Patient.java
│   │   └── Prescription.java
│   ├── repo/
│   │   ├── AdminRepository.java
│   │   ├── AppointmentRepository.java
│   │   ├── DoctorRepository.java
│   │   ├── PatientRepository.java
│   │   └── PrescriptionRepository.java
│   └── services/
│       ├── AppointmentService.java
│       ├── DoctorService.java
│       ├── MainService.java
│       ├── PatientService.java
│       ├── PrescriptionService.java
│       └── TokenService.java
└── src/main/resources/
    ├── static/
    │   ├── assets/
    │   │   ├── css/
    │   │   └── images/
    │   └── js/
    │       ├── components/
    │       │   ├── doctorCard.js
    │       │   ├── footer.js
    │       │   ├── header.js
    │       │   ├── modals.js
    │       │   └── patientRows.js
    │       ├── services/
    │       │   ├── appointmentRecordService.js
    │       │   ├── doctorServices.js
    │       │   ├── index.js
    │       │   ├── patientServices.js
    │       │   └── prescriptionServices.js
    │       ├── adminDashboard.js
    │       ├── doctorDashboard.js
    │       ├── loggedPatient.js
    │       ├── patientAppointment.js
    │       ├── patientDashboard.js
    │       ├── patientRecordServices.js
    │       ├── render.js
    │       └── util.js
    └── templates/
        ├── admin/
        │   └── adminDashboard.html
        ├── pages/
        └── doctorDashboard.html
```

---

## Features

### Admin
- Login with username and password
- View all doctors
- Add new doctors with availability slots
- Delete doctors
- Filter doctors by name, specialization, and time

### Doctor
- Login with email and password
- View appointments by date
- Search appointments by patient name
- View patient records
- Add prescriptions

### Patient
- Sign up and login
- Browse and filter doctors by name, specialization, and time
- Book appointments
- View and filter their appointments
- View prescriptions

---

## API Endpoints

### Admin
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/admin/login` | Admin login |

### Doctor
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/doctor` | Get all doctors |
| POST | `/api/doctor/{token}` | Add a doctor |
| PUT | `/api/doctor/{token}` | Update a doctor |
| DELETE | `/api/doctor/{id}/{token}` | Delete a doctor |
| POST | `/api/doctor/login` | Doctor login |
| GET | `/api/doctor/filter/{name}/{time}/{specialization}` | Filter doctors |
| GET | `/api/doctor/availability/{user}/{doctorId}/{date}/{token}` | Get doctor availability |

### Patient
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/patient` | Patient signup |
| POST | `/api/patient/login` | Patient login |
| GET | `/api/patient/{token}` | Get patient details |
| GET | `/api/patient/{id}/{token}` | Get patient appointments |
| GET | `/api/patient/filter/{condition}/{name}/{token}` | Filter patient appointments |

### Appointment
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/appointments/{date}/{patientName}/{token}` | Get appointments (doctor) |
| POST | `/api/appointments/{token}` | Book appointment |
| PUT | `/api/appointments/{token}` | Update appointment |
| DELETE | `/api/appointments/{id}/{token}` | Cancel appointment |

### Prescription
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/prescription/{token}` | Add prescription |
| GET | `/api/prescription/{appointmentId}/{token}` | Get prescription |

---

## Prerequisites

- Java 17+
- Maven
- Docker Desktop
- Node.js (optional, for tooling)
- Git

---

## Setup & Running

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd back-end
```

### 2. Start MySQL with Docker
```bash
docker run --name smartclinic-mysql \
  -e MYSQL_ROOT_PASSWORD=yourpassword \
  -e MYSQL_DATABASE=smartclinic \
  -p 3306:3306 \
  -d mysql:latest
```

### 3. Start MongoDB with Docker
```bash
docker run --name smartclinic-mongo \
  -p 27017:27017 \
  -d mongo:latest
```

### 4. Configure `application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smartclinic
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.data.mongodb.uri=mongodb://localhost:27017/smartclinic

api.path=/api/
jwt.secret=your_super_secret_key_must_be_32_chars_min
```

### 5. Run the application
```bash
mvn spring-boot:run
```

### 6. Access the app
```
http://localhost:8080
```

---

## Authentication

The app uses JWT tokens for authentication. Tokens are:
- Generated on login for admin, doctor, and patient
- Stored in `localStorage` on the frontend
- Passed as path variables in API requests
- Valid for 7 days
- Validated server-side on every protected request

---

## User Roles

| Role | Access |
|---|---|
| `admin` | Manage doctors, view all data |
| `doctor` | View appointments, add prescriptions |
| `patient` | Browse doctors, book appointments |
| `loggedPatient` | Authenticated patient with full booking access |

---

## Database

- **MySQL** — stores doctors, patients, appointments, and admin data
- **MongoDB** — stores prescriptions (document-based, linked by `appointmentId`)

---

## Environment Notes

- `spring.jpa.hibernate.ddl-auto=update` — automatically creates/updates tables on startup
- Fresh Docker deployments will recreate tables from entity definitions
- Column naming follows JPA field names (e.g. `fullName` → `full_name`)
