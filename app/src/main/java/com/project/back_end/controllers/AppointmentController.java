package com.project.back_end.controllers;
import com.project.back_end.models.*;
import com.project.back_end.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// 1. Annotate as @RestController — returns JSON responses
@RestController
@RequestMapping("${api.path}" + "appointments")
public class AppointmentController {

    // 2. Autowire necessary services
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private MainService service;


    // 3. Get Appointments
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();

        // Validate token for doctor role
        Map<String, Object> tokenValidation = service.validateToken(token, "doctor");
        if (!tokenValidation.isEmpty()) {
            response.put("message", tokenValidation.get("message"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Parse date string to LocalDate
        LocalDate localDate = LocalDate.parse(date);

        // Fetch and return appointments
        Map<String, Object> appointments = appointmentService.getAppointment(
                patientName, localDate, token
        );

        return ResponseEntity.ok(appointments);
    }


    // 4. Book Appointment
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> bookAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();

        // Validate token for patient role
        Map<String, Object> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.isEmpty()) {
            response.put("message", tokenValidation.get("message"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Validate the appointment time/availability
        Map<String, Object> appointmentValidation = service.validateAppointment(appointment);
        if (!appointmentValidation.isEmpty()) {
            response.put("message", (String) appointmentValidation.get("message"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Book the appointment
        int result = appointmentService.bookAppointment(appointment);

        if (result == 1) {
            response.put("message", "Appointment booked successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201
        } else {
            response.put("message", "Failed to book appointment.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // 5. Update Appointment
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, Object>> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();

        // Validate token for patient role
        Map<String, Object> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.isEmpty()) {
            response.put("message", tokenValidation.get("message"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Update and return the result
        return appointmentService.updateAppointment(appointment);
    }


    // 6. Cancel Appointment
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> cancelAppointment(
            @PathVariable long id,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();

        // Validate token for patient role
        Map<String, Object> tokenValidation = service.validateToken(token, "patient");
        if (!tokenValidation.isEmpty()) {
            response.put("message", tokenValidation.get("message"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Cancel and return the result
        return appointmentService.cancelAppointment(id, token);
    }
}
// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller.
//    - Use `@RequestMapping("/appointments")` to set a base path for all appointment-related endpoints.
//    - This centralizes all routes that deal with booking, updating, retrieving, and canceling appointments.


// 2. Autowire Dependencies:
//    - Inject `AppointmentService` for handling the business logic specific to appointments.
//    - Inject the general `Service` class, which provides shared functionality like token validation and appointment checks.


// 3. Define the `getAppointments` Method:
//    - Handles HTTP GET requests to fetch appointments based on date and patient name.
//    - Takes the appointment date, patient name, and token as path variables.
//    - First validates the token for role `"doctor"` using the `Service`.
//    - If the token is valid, returns appointments for the given patient on the specified date.
//    - If the token is invalid or expired, responds with the appropriate message and status code.


// 4. Define the `bookAppointment` Method:
//    - Handles HTTP POST requests to create a new appointment.
//    - Accepts a validated `Appointment` object in the request body and a token as a path variable.
//    - Validates the token for the `"patient"` role.
//    - Uses service logic to validate the appointment data (e.g., check for doctor availability and time conflicts).
//    - Returns success if booked, or appropriate error messages if the doctor ID is invalid or the slot is already taken.


// 5. Define the `updateAppointment` Method:
//    - Handles HTTP PUT requests to modify an existing appointment.
//    - Accepts a validated `Appointment` object and a token as input.
//    - Validates the token for `"patient"` role.
//    - Delegates the update logic to the `AppointmentService`.
//    - Returns an appropriate success or failure response based on the update result.


// 6. Define the `cancelAppointment` Method:
//    - Handles HTTP DELETE requests to cancel a specific appointment.
//    - Accepts the appointment ID and a token as path variables.
//    - Validates the token for `"patient"` role to ensure the user is authorized to cancel the appointment.
//    - Calls `AppointmentService` to handle the cancellation process and returns the result.