# MySQL Database Design
### Table: admins
- id: INT, Primary Key, AUTO_INCREMENT
- email: VARCHAR(255), NOT NULL, UNIQUE
- password_hash: VARCHAR(255), NOT NULL
- full_name: VARCHAR(120), NOT NULL
- created_at: DATETIME, NOT NULL (default: current timestamp)

### Table: patients
- id: INT, Primary Key, AUTO_INCREMENT
- email: VARCHAR(255), NOT NULL, UNIQUE
- password_hash: VARCHAR(255), NOT NULL
- full_name: VARCHAR(120), NOT NULL
- date_of_birth: DATE, NULL
- phone: VARCHAR(20), NULL
- created_at: DATETIME, NOT NULL (default: current timestamp)

### Table: doctors
- id: INT, Primary Key, AUTO_INCREMENT
- email: VARCHAR(255), NOT NULL, UNIQUE
- password_hash: VARCHAR(255), NOT NULL
- full_name: VARCHAR(120), NOT NULL
- specialization: VARCHAR(120), NOT NULL
- phone: VARCHAR(20), NULL
- is_active: BOOLEAN, NOT NULL (default: TRUE)
- created_at: DATETIME, NOT NULL (default: current timestamp)

**Notes:**
- is_active allows disabling a doctor without deleting history.

### Table: doctor_unavailability

- id: INT, Primary Key, AUTO_INCREMENT
- doctor_id: INT, NOT NULL, Foreign Key -> doctors(id)
- start_time: DATETIME, NOT NULL
- end_time: DATETIME, NOT NULL
- reason: VARCHAR(255), NULL

**Notes:**
- Used to block time slots when a doctor is not available.
- Business rule (enforced in code): unavailability should not overlap with CONFIRMED/SCHEDULED appointments.

### Table: appointments
- id: INT, Primary Key, AUTO_INCREMENT
- doctor_id: INT, NOT NULL, Foreign Key -> doctors(id)
- patient_id: INT, NOT NULL, Foreign Key -> patients(id)
- appointment_time: DATETIME, NOT NULL
- status: VARCHAR(20), NOT NULL  
  (example values: SCHEDULED, COMPLETED, CANCELLED)
- created_at: DATETIME, NOT NULL (default: current timestamp)

**Notes:**
- doctor_id and patient_id enforce relationships.
- constraint in patient/doctor deletion from table if allocated to an appointment and vice versa. Appointments can have its status set to CANCELLED.

# MongoDB Collection Design
### Collection: prescriptions

```json
{
  "_id": "ObjectId(\"67c123abc123abc123abc123\")",
  "patientId": 101,
  "doctorId": 12,
  "appointmentId": 5501,
  "createdAt": "2026-02-26T12:30:00Z",
  "diagnosis": "Upper respiratory infection",
  "medications": [
    {
      "name": "Amoxicillin",
      "dosage": "500mg",
      "frequency": "Every 8 hours",
      "durationDays": 7
    },
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Every 6 hours as needed",
      "durationDays": 3
    }
  ],
  "doctorNotes": "Take with food. Drink plenty of water.",
  "refillsAllowed": 1,
  "tags": ["antibiotic", "fever"],
  "attachments": [
    {
      "type": "pdf",
      "fileName": "prescription_5501.pdf",
      "uploadedAt": "2026-02-26T12:32:00Z"
    }
  ]
}