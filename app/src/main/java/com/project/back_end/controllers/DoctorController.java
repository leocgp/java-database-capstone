package com.project.back_end.controllers;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.*;
import com.project.back_end.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private MainService service;

    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> tokenValidation = service.validateToken(token, user);
        if (!tokenValidation.isEmpty()) {
            response.put("message", tokenValidation.get("message"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        LocalDate localDate = LocalDate.parse(date);
        List<String> availability = doctorService.getDoctorAvailability(doctorId, localDate);

        response.put("availability", availability);
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        Map<String, Object> response = new HashMap<>();

        List<Doctor> doctors = doctorService.getDoctors();
        response.put("doctors", doctors);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> addDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();

        Map<String, Object> tokenValidation = service.validateToken(token, "admin");
        if (!tokenValidation.isEmpty()) {
            response.put("message", tokenValidation.get("message"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        int result = doctorService.saveDoctor(doctor);

        if (result == 1) {
            response.put("message", "Doctor added to db.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else if (result == -1) {
            response.put("message", "Doctor already exists.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            response.put("message", "Some internal error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, Object>> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> tokenValidation = service.validateToken(token, "admin");
        if (!tokenValidation.isEmpty()) {
            response.put("message", tokenValidation.get("message"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        int result = doctorService.updateDoctor(doctor);

        if (result == 1) {
            response.put("message", "Doctor updated.");
            return ResponseEntity.ok(response);
        } else if (result == -1) {
            response.put("message", "Doctor not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            response.put("message", "Some internal error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> deleteDoctor(
            @PathVariable long id,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();

        Map<String, Object> tokenValidation = service.validateToken(token, "admin");
        if (!tokenValidation.isEmpty()) {
            response.put("message", tokenValidation.get("message"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        int result = doctorService.deleteDoctor(id);

        if (result == 1) {
            response.put("message", "Doctor deleted successfully.");
            return ResponseEntity.ok(response);
        } else if (result == -1) {
            response.put("message", "Doctor not found with id: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            response.put("message", "Some internal error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/filter/{name}/{time}/{specialization}")
    public ResponseEntity<Map<String, Object>> filterDoctors(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String specialization) {
        System.out.println("Filter received - name: " + name + ", time: " + time + ", specialization: " + specialization);

        Map<String, Object> result = service.filterDoctor(name, time, specialization);
        System.out.println("Filter result: " + result);
        return ResponseEntity.ok(result);
    }
}
// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST controller that serves JSON responses.
//    - Use `@RequestMapping("${api.path}doctor")` to prefix all endpoints with a configurable API path followed by "doctor".
//    - This class manages doctor-related functionalities such as registration, login, updates, and availability.


// 2. Autowire Dependencies:
//    - Inject `DoctorService` for handling the core logic related to doctors (e.g., CRUD operations, authentication).
//    - Inject the shared `Service` class for general-purpose features like token validation and filtering.


// 3. Define the `getDoctorAvailability` Method:
//    - Handles HTTP GET requests to check a specific doctor’s availability on a given date.
//    - Requires `user` type, `doctorId`, `date`, and `token` as path variables.
//    - First validates the token against the user type.
//    - If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.


// 4. Define the `getDoctor` Method:
//    - Handles HTTP GET requests to retrieve a list of all doctors.
//    - Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.


// 5. Define the `saveDoctor` Method:
//    - Handles HTTP POST requests to register a new doctor.
//    - Accepts a validated `Doctor` object in the request body and a token for authorization.
//    - Validates the token for the `"admin"` role before proceeding.
//    - If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.


// 6. Define the `doctorLogin` Method:
//    - Handles HTTP POST requests for doctor login.
//    - Accepts a validated `Login` DTO containing credentials.
//    - Delegates authentication to the `DoctorService` and returns login status and token information.


// 7. Define the `updateDoctor` Method:
//    - Handles HTTP PUT requests to update an existing doctor's information.
//    - Accepts a validated `Doctor` object and a token for authorization.
//    - Token must belong to an `"admin"`.
//    - If the doctor exists, updates the record and returns success; otherwise, returns not found or error messages.


// 8. Define the `deleteDoctor` Method:
//    - Handles HTTP DELETE requests to remove a doctor by ID.
//    - Requires both doctor ID and an admin token as path variables.
//    - If the doctor exists, deletes the record and returns a success message; otherwise, responds with a not found or error message.


// 9. Define the `filter` Method:
//    - Handles HTTP GET requests to filter doctors based on name, time, and specialty.
//    - Accepts `name`, `time`, and `specialization` as path variables.
//    - Calls the shared `Service` to perform filtering logic and returns matching doctors in the response.
