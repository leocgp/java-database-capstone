package com.project.back_end.services;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.*;
import com.project.back_end.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class MainService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    public Map<String, Object> validateToken(String token, String user) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isValid = tokenService.validateToken(token, user);

            if (!isValid) {
                response.put("message", "Unauthorized. Invalid or expired token.");
            }
            return response;

        } catch (Exception e) {
            System.out.println("Error validating token: " + e.getMessage());
            response.put("message", "An error occurred during token validation.");
            return response;
        }
    }

    public ResponseEntity<Map<String, Object>> validateAdmin(Admin receivedAdmin) {
        Map<String, Object> response = new HashMap<>();
        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

            if (admin == null) {
                response.put("message", "Admin not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (!admin.getPassword().equals(receivedAdmin.getPassword())) {
                response.put("message", "Invalid credentials.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            String token = tokenService.generateToken(admin.getUsername());
            response.put("token", token);
            response.put("message", "Login successful.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error validating admin: " + e.getMessage());
            response.put("message", "An error occurred during admin validation.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public Map<String, Object> filterDoctor(String name, String time, String specialization) {
        boolean hasName           = name           != null && !name.equals("null")           && !name.isEmpty();
        boolean hasSpecialization = specialization != null && !specialization.equals("null") && !specialization.isEmpty();
        boolean hasTime           = time           != null && !time.equals("null")           && !time.isEmpty();

        System.out.println("hasName: " + hasName + ", hasSpecialization: " + hasSpecialization + ", hasTime: " + hasTime);

        if (hasName && hasSpecialization && hasTime) {
            System.out.println("Filtering by name + specialization + time");
            return doctorService.filterDoctorsByNameSpecializationAndTime(name, specialization, time);
        }
        if (hasName && hasSpecialization) {
            System.out.println("Filtering by name + specialization");
            return doctorService.filterDoctorByNameAndSpecialization(name, specialization);
        }
        if (hasName && hasTime) {
            System.out.println("Filtering by name + time");
            return doctorService.filterDoctorByNameAndTime(name, time);
        }
        if (hasSpecialization && hasTime) {
            System.out.println("Filtering by specialization + time");
            return doctorService.filterDoctorByTimeAndSpecialization(specialization, time);
        }
        if (hasName) {
            System.out.println("Filtering by name only");
            return doctorService.findDoctorByName(name);
        }
        if (hasSpecialization) {
            System.out.println("Filtering by specialization only");
            return doctorService.filterDoctorBySpecialization(specialization);
        }
        if (hasTime) {
            System.out.println("Filtering by time only");
            return doctorService.filterDoctorsByTime(time);
        }

        System.out.println("No filters — returning all doctors");
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());
        return response;
    }


    public Map<String, Object> validateAppointment(Appointment appointment) {
        Map<String, Object> response = new HashMap<>();
        try {
            Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId())
                    .orElse(null);

            if (doctor == null) {
                response.put("message", "Doctor not found.");
                return response;
            }

            LocalDate date = appointment.getAppointmentTime().toLocalDate();
            List<String> availableSlots = doctorService.getDoctorAvailability(doctor.getId(), date);

            String appointmentTime = appointment.getAppointmentTime()
                    .toLocalTime().toString();

            boolean isAvailable = availableSlots.stream()
                    .anyMatch(slot -> slot.contains(appointmentTime));

            if (!isAvailable) {
                response.put("message", "Appointment time is unavailable.");
            }

            return response;

        } catch (Exception e) {
            System.out.println("Error validating appointment: " + e.getMessage());
            response.put("message", "An error occurred during appointment validation.");
            return response;
        }
    }

    public boolean validatePatient(Patient patient) {
        try {
            Patient existing = patientRepository.findByEmailOrPhone(
                    patient.getEmail(),
                    patient.getPhone()
            );
            return existing == null;

        } catch (Exception e) {
            System.out.println("Error validating patient: " + e.getMessage());
            return false;
        }
    }

    public ResponseEntity<Map<String, Object>> validatePatientLogin(Login login) {
        Map<String, Object> response = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(login.getIdentifier());

            if (patient == null) {
                response.put("message", "Patient not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (!patient.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid credentials.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(patient.getEmail());
            response.put("token", token);
            response.put("message", "Login successful.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error validating patient login: " + e.getMessage());
            response.put("message", "An error occurred during login.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(
            String condition, String name, String token) {
        try {
            String email    = tokenService.getEmailFromToken(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Unauthorized. Patient not found.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            Long patientId = patient.getId();

            boolean hasCondition = condition != null && !condition.equals("null") && !condition.isEmpty();
            boolean hasName      = name      != null && !name.equals("null")      && !name.isEmpty();

            if (hasCondition && hasName) {
                return patientService.filterByDoctorAndCondition(condition, name, patientId);
            }

            if (hasCondition) {
                return patientService.filterByCondition(condition, patientId);
            }

            if (hasName) {
                return patientService.filterByDoctor(name, patientId);
            }

            return patientService.getPatientAppointment(patientId, token);

        } catch (Exception e) {
            System.out.println("Error filtering patient appointments: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "An error occurred while filtering appointments.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
// 1. **@Service Annotation**
// The @Service annotation marks this class as a service component in Spring. This allows Spring to automatically detect it through component scanning
// and manage its lifecycle, enabling it to be injected into controllers or other services using @Autowired or constructor injection.

// 2. **Constructor Injection for Dependencies**
// The constructor injects all required dependencies (TokenService, Repositories, and other Services). This approach promotes loose coupling, improves testability,
// and ensures that all required dependencies are provided at object creation time.

// 3. **validateToken Method**
// This method checks if the provided JWT token is valid for a specific user. It uses the TokenService to perform the validation.
// If the token is invalid or expired, it returns a 401 Unauthorized response with an appropriate error message. This ensures security by preventing
// unauthorized access to protected resources.

// 4. **validateAdmin Method**
// This method validates the login credentials for an admin user.
// - It first searches the admin repository using the provided username.
// - If an admin is found, it checks if the password matches.
// - If the password is correct, it generates and returns a JWT token (using the admin’s username) with a 200 OK status.
// - If the password is incorrect, it returns a 401 Unauthorized status with an error message.
// - If no admin is found, it also returns a 401 Unauthorized.
// - If any unexpected error occurs during the process, a 500 Internal Server Error response is returned.
// This method ensures that only valid admin users can access secured parts of the system.

// 5. **filterDoctor Method**
// This method provides filtering functionality for doctors based on name, specialization, and available time slots.
// - It supports various combinations of the three filters.
// - If none of the filters are provided, it returns all available doctors.
// This flexible filtering mechanism allows the frontend or consumers of the API to search and narrow down doctors based on user criteria.

// 6. **validateAppointment Method**
// This method validates if the requested appointment time for a doctor is available.
// - It first checks if the doctor exists in the repository.
// - Then, it retrieves the list of available time slots for the doctor on the specified date.
// - It compares the requested appointment time with the start times of these slots.
// - If a match is found, it returns 1 (valid appointment time).
// - If no matching time slot is found, it returns 0 (invalid).
// - If the doctor doesn’t exist, it returns -1.
// This logic prevents overlapping or invalid appointment bookings.

// 7. **validatePatient Method**
// This method checks whether a patient with the same email or phone number already exists in the system.
// - If a match is found, it returns false (indicating the patient is not valid for new registration).
// - If no match is found, it returns true.
// This helps enforce uniqueness constraints on patient records and prevent duplicate entries.

// 8. **validatePatientLogin Method**
// This method handles login validation for patient users.
// - It looks up the patient by email.
// - If found, it checks whether the provided password matches the stored one.
// - On successful validation, it generates a JWT token and returns it with a 200 OK status.
// - If the password is incorrect or the patient doesn't exist, it returns a 401 Unauthorized with a relevant error.
// - If an exception occurs, it returns a 500 Internal Server Error.
// This method ensures only legitimate patients can log in and access their data securely.

// 9. **filterPatient Method**
// This method filters a patient's appointment history based on condition and doctor name.
// - It extracts the email from the JWT token to identify the patient.
// - Depending on which filters (condition, doctor name) are provided, it delegates the filtering logic to PatientService.
// - If no filters are provided, it retrieves all appointments for the patient.
// This flexible method supports patient-specific querying and enhances user experience on the client side.
