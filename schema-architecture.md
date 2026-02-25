# Smart Clinic Management System – Architecture Design

## Section 1: Architecture summary

The Smart Clinic Management System follows a three-tier web architecture: presentation, application, and data layers. The presentation layer supports both server-rendered web dashboards (Spring MVC + Thymeleaf) for Admin and Doctor views, and REST API consumers for other modules that communicate using HTTP and JSON. The application layer is implemented in Spring Boot, where controllers receive requests, services apply business rules, and repositories provide a clean interface for persistence. The data layer uses two databases: MySQL for structured relational entities such as patients, doctors, admins, and appointments (via Spring Data JPA), and MongoDB for flexible prescription documents (via Spring Data MongoDB). This separation keeps responsibilities clear and helps the system remain maintainable and scalable.

## Section 2: Numbered flow of data and control

1. A user accesses a web dashboard (Admin/Doctor via Thymeleaf) or an API client sends a REST request (JSON over HTTP).
2. The request is routed to the appropriate controller: a Thymeleaf (MVC) controller for server-rendered pages or a REST controller for API endpoints.
3. The controller validates input and delegates the action to the service layer.
4. The service layer executes business logic (e.g., checking availability before creating an appointment).
5. The service calls the repository layer to read/write data.
6. For structured clinic data, Spring Data JPA repositories interact with MySQL using JPA entities.
7. For prescription records, Spring Data MongoDB repositories interact with MongoDB using document models.
8. The repository returns results to the service, then the controller prepares the response.
9. The response is returned either as a rendered HTML page (Thymeleaf) or as JSON (REST API).