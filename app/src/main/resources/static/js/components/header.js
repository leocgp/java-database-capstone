function renderHeader(){
    const headerDiv = document.getElementById("header");
    if (!headerDiv) return;
    if (window.location.pathname.endsWith("/")) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
        headerDiv.innerHTML = `
        <header class="header">
        <div class="logo-section">
        <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
        <span class="logo-title">Hospital CMS</span>
        </div>
        </header>`;
        return;
    }
    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    let headerContent = `<header class="header">
                        <div class="logo-section">
                        <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
                        <span class="logo-title">Hospital CMS</span>
                        </div>
                        <nav>`;
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
          localStorage.removeItem("userRole");
          alert("Session expired or invalid login. Please log in again.");
          window.location.href = "/";
          return;
        }else if (role === "admin") {
            headerContent += `
            <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
            <a href="#" onclick="logout()">Logout</a>`;
        }else if (role === "doctor") {
            headerContent += `
            <button class="adminBtn"  onclick="selectRole('doctor')">Home</button>
            <a href="#" onclick="logout()">Logout</a>`;
        }else if (role === "patient") {
            headerContent += `
            <button id="patientLogin" class="adminBtn">Login</button>
            <button id="patientSignup" class="adminBtn">Sign Up</button>`;
        }else if (role === "loggedPatient") {
            headerContent += `
            <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
            <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
            <a href="#" onclick="logoutPatient()">Logout</a>`;
        }
    headerContent +=    `</nav>
                        </header>`;
    headerDiv.innerHTML = headerContent;
    attachHeaderButtonListeners();
    }

function attachHeaderButtonListeners() {

    const patientLogin = document.getElementById("patientLogin");
    if (patientLogin) {
        patientLogin.addEventListener("click", () => {
            openModal("patientLogin");
        });
    }

    const patientSignup = document.getElementById("patientSignup");
    if (patientSignup) {
        patientSignup.addEventListener("click", () => {
            openModal("patientSignup");
        });
    }
}
function logout(){
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
    window.location.href = "/";
}
function logoutPatient(){
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/pages/patientDashboard.html";
}
renderHeader();
/*


  ### Helper Functions

  13. **attachHeaderButtonListeners**: Adds event listeners to login buttons for "Doctor" and "Admin" roles. If clicked, it opens the respective login modal.

  14. **logout**: Removes user session data and redirects the user to the root page.

  15. **logoutPatient**: Removes the patient's session token and redirects to the patient dashboard.

  16. **Render the Header**: Finally, the `renderHeader()` function is called to initialize the header rendering process when the page loads.
*/
   
