# Admin User Stories
## User Story 1
**Title:**
_As an Admin, I want to log into the portal using my email and password, so that I can securely manage the platform._

**Acceptance Criteria:**
1. The system requires a valid email and password.
2. If the credentials are correct, the admin is redirected to the Admin Dashboard.
3. If the credentials are invalid, an error message is displayed.
4. The session is created only after successful authentication.

**Priority:** High
**Story Points:** 3

**Notes:**
- Passwords must be stored securely (hashed).
## User Story 2

**Title:**
_As an Admin, I want to log out of the portal, so that I can protect system access when I finish using it._

**Acceptance Criteria:**
1. The admin can click a logout button from the dashboard.
2. The system invalidates the active session.
3. After logout, the admin is redirected to the login page.
4. Attempting to access protected pages after logout requires reauthentication.

**Priority:** High
**Story Points:** 2

## User Story 3

**Title:**
_As an Admin, I want to add doctors to the portal, so that they can access the system and manage appointments._

**Acceptance Criteria:**
1. The admin can access a form to enter doctor details (name, specialty, contact information, credentials).
2. The system validates required fields before saving.
3. A new doctor record is stored in the MySQL database.
4. A confirmation message is displayed after successful creation.

**Priority:** High
**Story Points:** 5

**Notes:**
- Required fields must not be empty.
## User Story 4

**Title:**
_As an Admin, I want to delete a doctor’s profile from the portal, so that inactive or incorrect accounts can be removed._

**Acceptance Criteria:**
1. The admin can view a list of registered doctors.
2. The admin can select a doctor profile to delete.
3. The system asks for confirmation before deletion.
4. The doctor record is removed from the MySQL database after confirmation.

**Priority:** Medium
**Story Points:** 3

**Notes:**
- The system should prevent deletion if the doctor has active appointments.
## User Story 5

**Title:**
_As an Admin, I want to run a stored procedure to get the number of appointments per month, so that I can track platform usage statistics._

**Acceptance Criteria:**
1. The stored procedure can be executed from the MySQL CLI.
2. The procedure returns the total number of appointments grouped by month.
3. The results display month and appointment count.
4. The procedure runs without errors when valid data exists.

**Priority:** Medium
**Story Points:** 5

# Patient User Stories
## User Story 6

**Title:**
_As a Patient, I want to view a list of doctors without logging in, so that I can explore available options before registering._

**Acceptance Criteria:**
1. The system displays a list of doctors with basic information (name, specialty).
2. The list is accessible without authentication.
3. Sensitive doctor data is not displayed.
4. The list loads successfully without requiring a session.

**Priority:** Medium
**Story Points:** 3
## User Story 7

**Title:**
_As a Patient, I want to sign up using my email and password, so that I can book appointments._

**Acceptance Criteria:**
1. The patient can access a registration form.
2. The system validates required fields (email, password).
3. The email must be unique in the system.
4. The password is securely stored (hashed).
5. After successful registration, the patient can log in.

**Priority:** High
**Story Points:** 5

**Notes:**
- Password strength validation should be applied.
## User Story 8

**Title:**
_As a Patient, I want to log into the portal, so that I can manage my bookings._

**Acceptance Criteria:**
1. The patient enters valid email and password credentials.
2. The system authenticates the patient successfully.
3. Upon success, the patient is redirected to their dashboard.
4. Invalid credentials display an error message.
5. A session is created upon successful login.

**Priority:** High
**Story Points:** 3

**Notes:**
- Authentication must be secure.
## User Story 9

**Title:**
_As a Patient, I want to log out of the portal, so that I can secure my account._

**Acceptance Criteria:**
1. The patient can click a logout button from the dashboard.
2. The system invalidates the active session.
3. The patient is redirected to the login page.
4. Protected pages cannot be accessed after logout without reauthentication.

**Priority:** High
**Story Points:** 2
## User Story 10

**Title:**
_As a Patient, I want to log in and book an hour-long appointment with a doctor, so that I can receive medical consultation._

**Acceptance Criteria:**
1. Only logged-in patients can book appointments.
2. The patient selects a doctor and an available time slot.
3. The appointment duration is fixed at one hour.
4. The system prevents double-booking of the same doctor and time slot.
5. A confirmation message is displayed after successful booking.

**Priority:** High
**Story Points:** 8

**Notes:**
- Appointment details must be saved in the MySQL database.
## User Story 11

**Title:**
_As a Patient, I want to view my upcoming appointments, so that I can prepare accordingly._

**Acceptance Criteria:**
1. Only logged-in patients can access their appointments.
2. The system displays future appointments sorted by date.
3. Each appointment shows doctor name, date, and time.
4. Past appointments are not included in this view.

**Priority:** Medium
**Story Points:** 3

**Notes:**
- The system should filter appointments using the current date.

# Doctor User Stories
## User Story 12

**Title:**
_As a Doctor, I want to log into the portal, so that I can manage my appointments._

**Acceptance Criteria:**
1. The doctor enters valid email and password credentials.
2. The system authenticates the doctor successfully.
3. Upon successful login the doctor is redirected to the Doctor Dashboard.
4. Invalid credentials display an appropriate error message.
5. A secure session is created after authentication.

**Priority:** High  
**Story Points:** 3

**Notes:**
- Passwords must be stored securely (hashed).
- Unauthorized users must not access doctor features.
## User Story 13

**Title:**
_As a Doctor, I want to log out of the portal, so that I can protect my data._

**Acceptance Criteria:**
1. The doctor can access a logout option from the dashboard.
2. The system invalidates the current session.
3. The doctor is redirected to the login page.
4. Protected pages cannot be accessed after logout without reauthentication.

**Priority:** High  
**Story Points:** 2
## User Story 14

**Title:**
_As a Doctor, I want to view my appointment calendar, so that I can stay organized._

**Acceptance Criteria:**
1. Only logged doctors can access their calendar.
2. The calendar displays appointments assigned to the doctor.
3. Appointments show patient name, date, and time.
4. Appointments are sorted chronologically.

**Priority:** High  
**Story Points:** 5

**Notes:**
- Only the doctor’s own appointments should be visible.
## User Story 15

**Title:**
_As a Doctor, I want to mark my unavailability, so that patients can only book available time slots._

**Acceptance Criteria:**
1. The doctor can select dates or time ranges as unavailable.
2. The system prevents patients from booking appointments during those times.
3. Unavailability entries are stored in the database.
4. The doctor can modify or remove unavailable slots.

**Priority:** Medium  
**Story Points:** 8

**Notes:**
- The system must validate that unavailable slots do not conflict with confirmed appointments.
## User Story 16

**Title:**
_As a Doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._

**Acceptance Criteria:**
1. Only logged-in doctors can update their profile.
2. The system allows editing specialization and contact details.
3. Required fields must be validated.
4. Updated information is saved in the MySQL database.
5. A confirmation message is displayed after successful update.

**Priority:** Medium  
**Story Points:** 3
## User Story 17

**Title:**
_As a Doctor, I want to view patient details for upcoming appointments, so that I can be prepared._

**Acceptance Criteria:**
1. Only logged-in doctors can access patient details.
2. The system displays patient information for appointments assigned to the doctor.
3. Sensitive patient data is shown only for relevant appointments.
4. The system prevents access to patients not associated with the doctor.

**Priority:** High  
**Story Points:** 5

**Notes:**
- Access control must enforce doctor-to-patient relationship.