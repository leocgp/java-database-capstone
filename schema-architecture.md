# Smart Clinic Management System – Architecture Design

## Section 1: Architecture summary

The Smart Clinic Management System follows a three-tier web architecture: presentation, application, and data layers.
The presentation layer supports both server-rendered web dashboards (Spring MVC + Thymeleaf) for Admin and Doctor views and REST API consumers for other modules that communicate using HTTP and JSON.
The application layer is implemented in Spring Boot, where controllers receive requests, services apply business rules and repositories provide a clean interface for persistence.
The data layer uses two databases: MySQL for structured relational entities such as patients, doctors, admins and appointments and MongoDB for flexible prescription documents.
This separation keeps responsibilities clear and helps the system remain maintainable and scalable.

## Section 2: Numbered flow of data and control

1. A user accesses the web dashboard or sends a REST API request.
2. The request is received by the appropriate controller.
3. The controller delegates the request to the service layer.
4. The service layer applies business logic and determines the required data operations.
5. The service calls the appropriate repository.
6. The repository interacts with the corresponding database to retrieve or persist data.
7. The result is returned through the service and controller back to the user as either a rendered HTML page or a JSON response.