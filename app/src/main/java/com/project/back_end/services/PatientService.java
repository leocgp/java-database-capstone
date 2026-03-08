package com.project.back_end.services;
import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PatientService {
    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;

        } catch (Exception e) {
            System.out.println("Error creating patient: " + e.getMessage());
            return 0;
        }
    }

    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.getEmailFromToken(token);

            Doctor doctor = doctorRepository.findByEmail(email);
            if (doctor == null) {
                Patient tokenPatient = patientRepository.findByEmail(email);

                if (tokenPatient == null) {
                    response.put("message", "Unauthorized. Invalid token.");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
                if (!tokenPatient.getId().equals(id)) {
                    response.put("message", "Unauthorized. ID mismatch.");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            }

            List<Appointment> appointments = appointmentRepository.findByPatientId(id);
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(a -> new AppointmentDTO(
                            a.getId(),
                            a.getDoctor().getId(),
                            a.getDoctor().getFullName(),
                            a.getPatient().getId(),
                            a.getPatient().getFullName(),
                            a.getPatient().getEmail(),
                            a.getPatient().getPhone(),
                            a.getPatient().getAddress(),
                            a.getAppointmentTime(),
                            a.getStatus()
                    ))
                    .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error fetching patient appointments: " + e.getMessage());
            response.put("message", "An error occurred while fetching appointments.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            int status = condition.equalsIgnoreCase("past") ? 1 : 0;

            List<Appointment> appointments = appointmentRepository
                    .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status);
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(a -> new AppointmentDTO(
                            a.getId(),
                            a.getDoctor().getId(),
                            a.getDoctor().getFullName(),
                            a.getPatient().getId(),
                            a.getPatient().getFullName(),
                            a.getPatient().getEmail(),
                            a.getPatient().getPhone(),
                            a.getPatient().getAddress(),
                            a.getAppointmentTime(),
                            a.getStatus()
                    ))
                    .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error filtering by condition: " + e.getMessage());
            response.put("message", "An error occurred while filtering appointments.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Appointment> appointments = appointmentRepository
                    .searchByDoctorNameAndPatientId(name, patientId);

            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(a -> new AppointmentDTO(
                            a.getId(),
                            a.getDoctor().getId(),
                            a.getDoctor().getFullName(),
                            a.getPatient().getId(),
                            a.getPatient().getFullName(),
                            a.getPatient().getEmail(),
                            a.getPatient().getPhone(),
                            a.getPatient().getAddress(),
                            a.getAppointmentTime(),
                            a.getStatus()
                    ))
                    .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error filtering by doctor: " + e.getMessage());
            response.put("message", "An error occurred while filtering by doctor.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(
            String condition, String name, long patientId) {

        Map<String, Object> response = new HashMap<>();
        try {
            int status = condition.equalsIgnoreCase("past") ? 1 : 0;

            List<Appointment> appointments = appointmentRepository
                    .searchByDoctorNameAndPatientIdAndStatus(name, patientId, status);
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(a -> new AppointmentDTO(
                            a.getId(),
                            a.getDoctor().getId(),
                            a.getDoctor().getFullName(),
                            a.getPatient().getId(),
                            a.getPatient().getFullName(),
                            a.getPatient().getEmail(),
                            a.getPatient().getPhone(),
                            a.getPatient().getAddress(),
                            a.getAppointmentTime(),
                            a.getStatus()
                    ))
                    .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error filtering by doctor and condition: " + e.getMessage());
            response.put("message", "An error occurred while filtering appointments.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.getEmailFromToken(token);

            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("message", "Patient not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("patient", patient);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error fetching patient details: " + e.getMessage());
            response.put("message", "An error occurred while fetching patient details.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private AppointmentDTO convertToDTO(Appointment a) {
        return new AppointmentDTO(
                a.getId(),
                a.getDoctor().getId(),
                a.getDoctor().getFullName(),
                a.getPatient().getId(),
                a.getPatient().getFullName(),
                a.getPatient().getEmail(),
                a.getPatient().getPhone(),
                a.getPatient().getAddress(),
                a.getAppointmentTime(),
                a.getStatus()
        );
    }
}
// 1. **Add @Service Annotation**:
//    - The `@Service` annotation is used to mark this class as a Spring service component. 
//    - It will be managed by Spring's container and used for business logic related to patients and appointments.
//    - Instruction: Ensure that the `@Service` annotation is applied above the class declaration.

// 2. **Constructor Injection for Dependencies**:
//    - The `PatientService` class has dependencies on `PatientRepository`, `AppointmentRepository`, and `TokenService`.
//    - These dependencies are injected via the constructor to maintain good practices of dependency injection and testing.
//    - Instruction: Ensure constructor injection is used for all the required dependencies.

// 3. **createPatient Method**:
//    - Creates a new patient in the database. It saves the patient object using the `PatientRepository`.
//    - If the patient is successfully saved, the method returns `1`; otherwise, it logs the error and returns `0`.
//    - Instruction: Ensure that error handling is done properly and exceptions are caught and logged appropriately.

// 4. **getPatientAppointment Method**:
//    - Retrieves a list of appointments for a specific patient, based on their ID.
//    - The appointments are then converted into `AppointmentDTO` objects for easier consumption by the API client.
//    - This method is marked as `@Transactional` to ensure database consistency during the transaction.
//    - Instruction: Ensure that appointment data is properly converted into DTOs and the method handles errors gracefully.

// 5. **filterByCondition Method**:
//    - Filters appointments for a patient based on the condition (e.g., "past" or "future").
//    - Retrieves appointments with a specific status (0 for future, 1 for past) for the patient.
//    - Converts the appointments into `AppointmentDTO` and returns them in the response.
//    - Instruction: Ensure the method correctly handles "past" and "future" conditions, and that invalid conditions are caught and returned as errors.

// 6. **filterByDoctor Method**:
//    - Filters appointments for a patient based on the doctor's name.
//    - It retrieves appointments where the doctor’s name matches the given value, and the patient ID matches the provided ID.
//    - Instruction: Ensure that the method correctly filters by doctor's name and patient ID and handles any errors or invalid cases.

// 7. **filterByDoctorAndCondition Method**:
//    - Filters appointments based on both the doctor's name and the condition (past or future) for a specific patient.
//    - This method combines filtering by doctor name and appointment status (past or future).
//    - Converts the appointments into `AppointmentDTO` objects and returns them in the response.
//    - Instruction: Ensure that the filter handles both doctor name and condition properly, and catches errors for invalid input.

// 8. **getPatientDetails Method**:
//    - Retrieves patient details using the `tokenService` to extract the patient's email from the provided token.
//    - Once the email is extracted, it fetches the corresponding patient from the `patientRepository`.
//    - It returns the patient's information in the response body.
    //    - Instruction: Make sure that the token extraction process works correctly and patient details are fetched properly based on the extracted email.

// 9. **Handling Exceptions and Errors**:
//    - The service methods handle exceptions using try-catch blocks and log any issues that occur. If an error occurs during database operations, the service responds with appropriate HTTP status codes (e.g., `500 Internal Server Error`).
//    - Instruction: Ensure that error handling is consistent across the service, with proper logging and meaningful error messages returned to the client.

// 10. **Use of DTOs (Data Transfer Objects)**:
//    - The service uses `AppointmentDTO` to transfer appointment-related data between layers. This ensures that sensitive or unnecessary data (e.g., password or private patient information) is not exposed in the response.
//    - Instruction: Ensure that DTOs are used appropriately to limit the exposure of internal data and only send the relevant fields to the client.
