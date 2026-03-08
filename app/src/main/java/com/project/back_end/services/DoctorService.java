package com.project.back_end.services;
import com.project.back_end.models.*;
import com.project.back_end.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.project.back_end.DTO.Login;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    // ── Repositories & Services ───────────────────────────────────────────────

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;


    // ── 1. Get Doctor Availability ────────────────────────────────────────────

    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        try {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end   = date.atTime(23, 59, 59);

            List<Appointment> booked = appointmentRepository
                    .getAppointmentsByDoctorAndDate(doctorId, start, end);

            Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
            if (doctor == null) return new ArrayList<>();

            List<String> bookedSlots = booked.stream()
                    .map(a -> a.getAppointmentTime().toLocalTime().toString())
                    .collect(Collectors.toList());

            List<String> availableSlots = doctor.getAvailableTimes().stream()
                    .filter(slot -> !bookedSlots.contains(slot))
                    .collect(Collectors.toList());

            return availableSlots;

        } catch (Exception e) {
            System.out.println("Error fetching availability: " + e.getMessage());
            return new ArrayList<>();
        }
    }


    // ── 2. Save Doctor ────────────────────────────────────────────────────────

    public int saveDoctor(Doctor doctor) {
        try {
            Doctor existing = doctorRepository.findByEmail(doctor.getEmail());
            if (existing != null) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;

        } catch (Exception e) {
            System.out.println("Error saving doctor: " + e.getMessage());
            return 0;
        }
    }


    // ── 3. Update Doctor ──────────────────────────────────────────────────────

    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existing = doctorRepository.findById(doctor.getId());
            if (existing.isEmpty()) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;

        } catch (Exception e) {
            System.out.println("Error updating doctor: " + e.getMessage());
            return 0;
        }
    }


    // ── 4. Get All Doctors ────────────────────────────────────────────────────

    public List<Doctor> getDoctors() {
        try {
            return doctorRepository.findAll();
        } catch (Exception e) {
            System.out.println("Error fetching doctors: " + e.getMessage());
            return new ArrayList<>();
        }
    }


    // ── 5. Delete Doctor ──────────────────────────────────────────────────────

    public int deleteDoctor(long id) {
        try {
            Optional<Doctor> existing = doctorRepository.findById(id);
            if (existing.isEmpty()) {
                return -1;
            }
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;

        } catch (Exception e) {
            System.out.println("Error deleting doctor: " + e.getMessage());
            return 0;
        }
    }


    // ── 6. Validate Doctor Login ──────────────────────────────────────────────

    public ResponseEntity<Map<String, Object>> validateDoctor(Login login) {
        Map<String, Object> response = new HashMap<>();
        try {
            Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());

            if (doctor == null) {
                response.put("message", "Doctor not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (!doctor.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid credentials.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(doctor.getEmail());
            response.put("token", token);
            response.put("message", "Login successful.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error validating doctor: " + e.getMessage());
            response.put("message", "An error occurred during login.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // ── 7. Find Doctor By Name ────────────────────────────────────────────────

    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Doctor> doctors = doctorRepository.searchByName(name);
            response.put("doctors", doctors);
            return response;
        } catch (Exception e) {
            System.out.println("Error finding doctor by name: " + e.getMessage());
            response.put("doctors", new ArrayList<>());
            return response;
        }
    }


    // ── 8. Filter By Name, Specialization & Time ─────────────────────────────

    public Map<String, Object> filterDoctorsByNameSpecializationAndTime(
            String name, String specialization, String amOrPm) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<Doctor> doctors = doctorRepository
                    .searchByNameAndSpecialization(name, specialization);

            List<Doctor> filtered = filterDoctorByTime(doctors, amOrPm);
            response.put("doctors", filtered);
            return response;

        } catch (Exception e) {
            System.out.println("Error filtering doctors: " + e.getMessage());
            response.put("doctors", new ArrayList<>());
            return response;
        }
    }


    // ── 9. Filter By Name & Time ──────────────────────────────────────────────

    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Doctor> doctors = doctorRepository.searchByName(name);

            List<Doctor> filtered = filterDoctorByTime(doctors, amOrPm);
            response.put("doctors", filtered);
            return response;

        } catch (Exception e) {
            System.out.println("Error filtering doctors by name and time: " + e.getMessage());
            response.put("doctors", new ArrayList<>());
            return response;
        }
    }

    public Map<String, Object> filterDoctorByNameAndSpecialization(
            String name, String specialization) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<Doctor> doctors = doctorRepository
                    .searchByNameAndSpecialization(name, specialization);
            response.put("doctors", doctors);
            return response;

        } catch (Exception e) {
            System.out.println("Error filtering doctors by name and specialization: " + e.getMessage());
            response.put("doctors", new ArrayList<>());
            return response;
        }
    }

    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Doctor> doctors  = doctorRepository.findAll();
            List<Doctor> filtered = filterDoctorByTime(doctors, amOrPm);
            response.put("doctors", filtered);
            return response;

        } catch (Exception e) {
            System.out.println("Error filtering doctors by time: " + e.getMessage());
            response.put("doctors", new ArrayList<>());
            return response;
        }
    }

    public Map<String, Object> filterDoctorByTimeAndSpecialization(String specialization, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Filter by specialization first
            List<Doctor> doctors = doctorRepository.findBySpecializationIgnoreCase(specialization);

            // Then filter by AM/PM availability
            List<Doctor> filtered = filterDoctorByTime(doctors, amOrPm);
            response.put("doctors", filtered);
            return response;

        } catch (Exception e) {
            System.out.println("Error filtering doctors by time and specialization: " + e.getMessage());
            response.put("doctors", new ArrayList<>());
            return response;
        }
    }


    // ── 12. Filter By Specialization ───────────────────────────────────────────────

    public Map<String, Object> filterDoctorBySpecialization(String specialization) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Doctor> doctors = doctorRepository.findBySpecializationIgnoreCase(specialization);
            response.put("doctors", doctors);
            return response;

        } catch (Exception e) {
            System.out.println("Error filtering doctors by specialization: " + e.getMessage());
            response.put("doctors", new ArrayList<>());
            return response;
        }
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        return doctors.stream()
                .filter(doctor -> doctor.getAvailableTimes().stream()
                        .anyMatch(slot -> slot.toUpperCase().contains(amOrPm.toUpperCase()))
                )
                .collect(Collectors.toList());
    }
}

// 1. **Add @Service Annotation**:
//    - This class should be annotated with `@Service` to indicate that it is a service layer class.
//    - The `@Service` annotation marks this class as a Spring-managed bean for business logic.
//    - Instruction: Add `@Service` above the class declaration.

// 2. **Constructor Injection for Dependencies**:
//    - The `DoctorService` class depends on `DoctorRepository`, `AppointmentRepository`, and `TokenService`.
//    - These dependencies should be injected via the constructor for proper dependency management.
//    - Instruction: Ensure constructor injection is used for injecting dependencies into the service.

// 3. **Add @Transactional Annotation for Methods that Modify or Fetch Database Data**:
//    - Methods like `getDoctorAvailability`, `getDoctors`, `findDoctorByName`, `filterDoctorsBy*` should be annotated with `@Transactional`.
//    - The `@Transactional` annotation ensures that database operations are consistent and wrapped in a single transaction.
//    - Instruction: Add the `@Transactional` annotation above the methods that perform database operations or queries.

// 4. **getDoctorAvailability Method**:
//    - Retrieves the available time slots for a specific doctor on a particular date and filters out already booked slots.
//    - The method fetches all appointments for the doctor on the given date and calculates the availability by comparing against booked slots.
//    - Instruction: Ensure that the time slots are properly formatted and the available slots are correctly filtered.

// 5. **saveDoctor Method**:
//    - Used to save a new doctor record in the database after checking if a doctor with the same email already exists.
//    - If a doctor with the same email is found, it returns `-1` to indicate conflict; `1` for success, and `0` for internal errors.
//    - Instruction: Ensure that the method correctly handles conflicts and exceptions when saving a doctor.

// 6. **updateDoctor Method**:
//    - Updates an existing doctor's details in the database. If the doctor doesn't exist, it returns `-1`.
//    - Instruction: Make sure that the doctor exists before attempting to save the updated record and handle any errors properly.

// 7. **getDoctors Method**:
//    - Fetches all doctors from the database. It is marked with `@Transactional` to ensure that the collection is properly loaded.
//    - Instruction: Ensure that the collection is eagerly loaded, especially if dealing with lazy-loaded relationships (e.g., available times). 

// 8. **deleteDoctor Method**:
//    - Deletes a doctor from the system along with all appointments associated with that doctor.
//    - It first checks if the doctor exists. If not, it returns `-1`; otherwise, it deletes the doctor and their appointments.
//    - Instruction: Ensure the doctor and their appointments are deleted properly, with error handling for internal issues.

// 9. **validateDoctor Method**:
//    - Validates a doctor's login by checking if the email and password match an existing doctor record.
//    - It generates a token for the doctor if the login is successful, otherwise returns an error message.
//    - Instruction: Make sure to handle invalid login attempts and password mismatches properly with error responses.

// 10. **findDoctorByName Method**:
//    - Finds doctors based on partial name matching and returns the list of doctors with their available times.
//    - This method is annotated with `@Transactional` to ensure that the database query and data retrieval are properly managed within a transaction.
//    - Instruction: Ensure that available times are eagerly loaded for the doctors.


// 11. **filterDoctorsByNameSpecializationandTime Method**:
//    - Filters doctors based on their name, specialization, and availability during a specific time (AM/PM).
//    - The method fetches doctors matching the name and specialization criteria, then filters them based on their availability during the specified time period.
//    - Instruction: Ensure proper filtering based on both the name and specialization as well as the specified time period.

// 12. **filterDoctorByTime Method**:
//    - Filters a list of doctors based on whether their available times match the specified time period (AM/PM).
//    - This method processes a list of doctors and their available times to return those that fit the time criteria.
//    - Instruction: Ensure that the time filtering logic correctly handles both AM and PM time slots and edge cases.


// 13. **filterDoctorByNameAndTime Method**:
//    - Filters doctors based on their name and the specified time period (AM/PM).
//    - Fetches doctors based on partial name matching and filters the results to include only those available during the specified time period.
//    - Instruction: Ensure that the method correctly filters doctors based on the given name and time of day (AM/PM).

// 14. **filterDoctorByNameAndSpecialization Method**:
//    - Filters doctors by name and specialization.
//    - It ensures that the resulting list of doctors matches both the name (case-insensitive) and the specified specialization.
//    - Instruction: Ensure that both name and specialization are considered when filtering doctors.


// 15. **filterDoctorByTimeAndSpecialization Method**:
//    - Filters doctors based on their specialization and availability during a specific time period (AM/PM).
//    - Fetches doctors based on the specified specialization and filters them based on their available time slots for AM/PM.
//    - Instruction: Ensure the time filtering is accurately applied based on the given specialization and time period (AM/PM).

// 16. **filterDoctorBySpecialization Method**:
//    - Filters doctors based on their specialization.
//    - This method fetches all doctors matching the specified specialization and returns them.
//    - Instruction: Make sure the filtering logic works for case-insensitive specialization matching.

// 17. **filterDoctorsByTime Method**:
//    - Filters all doctors based on their availability during a specific time period (AM/PM).
//    - The method checks all doctors' available times and returns those available during the specified time period.
//    - Instruction: Ensure proper filtering logic to handle AM/PM time periods.
