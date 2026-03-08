package com.project.back_end.services;
import com.project.back_end.models.*;
import com.project.back_end.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppointmentService {
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private MainService service;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private TokenService tokenService;

    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            System.out.println("Error booking appointment: " + e.getMessage());
            return 0; // Failure
        }
    }

    public ResponseEntity<Map<String, Object>> updateAppointment(Appointment appointment) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Appointment> existing = appointmentRepository.findById(appointment.getId());

            if (existing.isEmpty()) {
                response.put("message", "Appointment not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            Optional<Doctor> doctor = doctorRepository.findById(
                    appointment.getDoctor().getId()
            );

            if (doctor.isEmpty()) {
                response.put("message", "Doctor not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

// Get available slots for the appointment date
            LocalDate date = appointment.getAppointmentTime().toLocalDate();
            List<String> availableSlots = doctorService.getDoctorAvailability(
                    doctor.get().getId(), date
            );

// Check if requested time is available
            String requestedTime = appointment.getAppointmentTime()
                    .toLocalTime().toString();

            boolean isAvailable = availableSlots.stream()
                    .anyMatch(slot -> slot.contains(requestedTime));

            if (!isAvailable) {
                response.put("message", "Appointment time is unavailable.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            appointmentRepository.save(appointment);
            response.put("message", "Appointment updated successfully.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error updating appointment: " + e.getMessage());
            response.put("message", "An error occurred while updating the appointment.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> cancelAppointment(long id, String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            Patient patient = tokenService.getPatientFromToken(token);

            if (patient == null) {
                response.put("message", "Unauthorized. Invalid token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            Optional<Appointment> existing = appointmentRepository.findById(id);

            if (existing.isEmpty()) {
                response.put("message", "Appointment not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Appointment appointment = existing.get();

            if (!appointment.getPatient().getId().equals(patient.getId())) {
                response.put("message", "Unauthorized. You can only cancel your own appointments.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            appointmentRepository.delete(appointment);
            response.put("message", "Appointment cancelled successfully.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error cancelling appointment: " + e.getMessage());
            response.put("message", "An error occurred while cancelling the appointment.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            Doctor doctor = tokenService.getDoctorFromToken(token);

            if (doctor == null) {
                response.put("message", "Unauthorized. Invalid token.");
                return response;
            }
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end   = date.atTime(23, 59, 59);

            List<Appointment> appointments;

            if (pname != null && !pname.equals("null") && !pname.isEmpty()) {
                appointments = appointmentRepository
                        .getAppointmentsByDoctorAndPatientNameAndDate(
                                doctor.getId(), pname, start, end
                        );
            } else {
                appointments = appointmentRepository
                        .getAppointmentsByDoctorAndDate(
                                doctor.getId(), start, end
                        );
            }

            response.put("appointments", appointments);
            return response;

        } catch (Exception e) {
            System.out.println("Error fetching appointments: " + e.getMessage());
            response.put("message", "An error occurred while fetching appointments.");
            return response;
        }
    }
}
// 1. **Add @Service Annotation**:
//    - To indicate that this class is a service layer class for handling business logic.
//    - The `@Service` annotation should be added before the class declaration to mark it as a Spring service component.
//    - Instruction: Add `@Service` above the class definition.

// 2. **Constructor Injection for Dependencies**:
//    - The `AppointmentService` class requires several dependencies like `AppointmentRepository`, `Service`, `TokenService`, `PatientRepository`, and `DoctorRepository`.
//    - These dependencies should be injected through the constructor.
//    - Instruction: Ensure constructor injection is used for proper dependency management in Spring.

// 3. **Add @Transactional Annotation for Methods that Modify Database**:
//    - The methods that modify or update the database should be annotated with `@Transactional` to ensure atomicity and consistency of the operations.
//    - Instruction: Add the `@Transactional` annotation above methods that interact with the database, especially those modifying data.

// 4. **Book Appointment Method**:
//    - Responsible for saving the new appointment to the database.
//    - If the save operation fails, it returns `0`; otherwise, it returns `1`.
//    - Instruction: Ensure that the method handles any exceptions and returns an appropriate result code.

// 5. **Update Appointment Method**:
//    - This method is used to update an existing appointment based on its ID.
//    - It validates whether the patient ID matches, checks if the appointment is available for updating, and ensures that the doctor is available at the specified time.
//    - If the update is successful, it saves the appointment; otherwise, it returns an appropriate error message.
//    - Instruction: Ensure proper validation and error handling is included for appointment updates.

// 6. **Cancel Appointment Method**:
//    - This method cancels an appointment by deleting it from the database.
//    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
//    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment.

// 7. **Get Appointments Method**:
//    - This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by the patient's name.
//    - It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.
//    - Instruction: Ensure the correct use of transaction boundaries, especially when querying the database for appointments.

// 8. **Change Status Method**:
//    - This method updates the status of an appointment by changing its value in the database.
//    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
//    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.

