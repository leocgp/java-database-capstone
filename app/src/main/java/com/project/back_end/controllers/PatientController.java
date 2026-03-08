package com.project.back_end.controllers;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.*;
import com.project.back_end.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private MainService service;


    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatientDetails(
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();

        Map<String, Object> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.isEmpty()) {
            response.put("message", tokenValidation.get("message"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return patientService.getPatientDetails(token);
    }


    @PostMapping()
    public ResponseEntity<Map<String, Object>> createPatient(
            @RequestBody Patient patient) {

        Map<String, Object> response = new HashMap<>();

        boolean isValid = service.validatePatient(patient);

        if (!isValid) {
            response.put("message", "Patient with email id or phone no already exist.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409
        }

        int result = patientService.createPatient(patient);

        if (result == 1) {
            response.put("message", "Signup successful.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201
        } else {
            response.put("message", "Internal server error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 500
        }
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> patientLogin(
            @RequestBody Login login) {

        return service.validatePatientLogin(login);
    }

    @GetMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointments(
            @PathVariable Long id,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();

        Map<String, Object> doctorValidation  = service.validateToken(token, "doctor");
        Map<String, Object> patientValidation = service.validateToken(token, "patient");

        if (!doctorValidation.isEmpty() && !patientValidation.isEmpty()) {
            response.put("message", "Unauthorized.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return patientService.getPatientAppointment(id, token);
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointments(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();

        Map<String, Object> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.isEmpty()) {
            response.put("message", tokenValidation.get("message"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return service.filterPatient(condition, name, token);
    }
}
// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller for patient-related operations.
//    - Use `@RequestMapping("/patient")` to prefix all endpoints with `/patient`, grouping all patient functionalities under a common route.


// 2. Autowire Dependencies:
//    - Inject `PatientService` to handle patient-specific logic such as creation, retrieval, and appointments.
//    - Inject the shared `Service` class for tasks like token validation and login authentication.


// 3. Define the `getPatient` Method:
//    - Handles HTTP GET requests to retrieve patient details using a token.
//    - Validates the token for the `"patient"` role using the shared service.
//    - If the token is valid, returns patient information; otherwise, returns an appropriate error message.


// 4. Define the `createPatient` Method:
//    - Handles HTTP POST requests for patient registration.
//    - Accepts a validated `Patient` object in the request body.
//    - First checks if the patient already exists using the shared service.
//    - If validation passes, attempts to create the patient and returns success or error messages based on the outcome.


// 5. Define the `login` Method:
//    - Handles HTTP POST requests for patient login.
//    - Accepts a `Login` DTO containing email/username and password.
//    - Delegates authentication to the `validatePatientLogin` method in the shared service.
//    - Returns a response with a token or an error message depending on login success.


// 6. Define the `getPatientAppointment` Method:
//    - Handles HTTP GET requests to fetch appointment details for a specific patient.
//    - Requires the patient ID, token, and user role as path variables.
//    - Validates the token using the shared service.
//    - If valid, retrieves the patient's appointment data from `PatientService`; otherwise, returns a validation error.


// 7. Define the `filterPatientAppointment` Method:
//    - Handles HTTP GET requests to filter a patient's appointments based on specific conditions.
//    - Accepts filtering parameters: `condition`, `name`, and a token.
//    - Token must be valid for a `"patient"` role.
//    - If valid, delegates filtering logic to the shared service and returns the filtered result.