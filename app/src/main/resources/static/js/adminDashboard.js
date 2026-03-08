import { openModal, closeModal } from "./components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

window.openModal = openModal;
window.closeModal = closeModal;

window.addEventListener("DOMContentLoaded", () => {
    const addDocBtn = document.getElementById('addDocBtn');
    if (addDocBtn) addDocBtn.addEventListener('click', () => openModal('addDoctor'));

  loadDoctorCards();
  setupFilterListeners();
});

async function loadDoctorCards() {
    try {
        const data = await getDoctors();
        const doctors = Array.isArray(data) ? data : data.doctors ?? [];

        const contentDiv = document.getElementById("content");
        contentDiv.innerHTML = "";

        if (doctors.length === 0) {
            contentDiv.innerHTML = "<p>No doctors found.</p>";
            return;
        }
        renderDoctorCards(doctors);

    } catch (error) {
        console.error("Error loading doctor cards:", error);
        alert("Failed to load doctors. Please try again.");
    }
}
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p>No doctors found.</p>";
    return;
  }
  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}
function setupFilterListeners() {
  const searchBar       = document.getElementById("searchBar");
  const filterTime      = document.getElementById("filterTime");
  const filterSpecialization = document.getElementById("filterSpecialization");

  if (searchBar)        searchBar.addEventListener("input", filterDoctorsOnChange);
  if (filterTime)       filterTime.addEventListener("change", filterDoctorsOnChange);
  if (filterSpecialization)  filterSpecialization.addEventListener("change", filterDoctorsOnChange);
}

async function filterDoctorsOnChange() {
    const name           = document.getElementById("searchBar")?.value.trim() || null;
    const time           = document.getElementById("filterTime")?.value || null;
    const specialization = document.getElementById("filterSpecialization")?.value || null;

    try {
        const doctors = await filterDoctors(name, time, specialization);

        const contentDiv = document.getElementById("content");
        contentDiv.innerHTML = "";

        if (!doctors || doctors.length === 0) {
            contentDiv.innerHTML = "<p>No doctors found.</p>";
            return;
        }

        renderDoctorCards(doctors);

    } catch (error) {
        console.error("Error filtering doctors:", error);
        alert("Failed to filter doctors: " + error.message);
    }
}

window.adminAddDoctor = async function () {
  const name      = document.getElementById("doctorName").value.trim();
  const specialization = document.getElementById("doctorSpecialization").value.trim();
  const email     = document.getElementById("doctorEmail").value.trim();
  const password  = document.getElementById("doctorPassword").value.trim();
  const phone    = document.getElementById("doctorPhone").value.trim();

  const availabilityCheckboxes = document.querySelectorAll(
    "input[name='availability']:checked"
  );
  const availability = Array.from(availabilityCheckboxes).map((cb) => cb.value);

  const doctor = { fullName: name, specialization, email, password, phone, availableTimes: availability };

  const token = localStorage.getItem("token");
  if (!token) {
    alert("Unauthorized! Please log in as admin.");
    return;
  }

  try {
    const result = await saveDoctor(doctor, token);

    if (result.success) {
      closeModal("addDoctor");
      alert(result.message || "Doctor added successfully!");
      loadDoctorCards();
    } else {
      alert("Failed to add doctor: " + result.message);
    }

  } catch (error) {
    console.error("Error adding doctor:", error);
    alert("An unexpected error occurred: " + error.message);
  }
};
/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialization
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialization

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialization) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialization, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/
