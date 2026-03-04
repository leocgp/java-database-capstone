import { openModal, closeModal } from "../components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

document.getElementById('addDocBtn').addEventListener('click', () => {
  openModal('addDoctor');
});

window.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
  setupFilterListeners();
});

async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();

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
  document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
  document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
  document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);
}

async function filterDoctorsOnChange() {
  const name      = document.getElementById("searchBar").value.trim() || null;
  const time      = document.getElementById("filterTime").value || null;
  const specialty = document.getElementById("filterSpecialty").value || null;

  try {
    const doctors = await filterDoctors(name, time, specialty);

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
  const specialty = document.getElementById("doctorSpecialty").value.trim();
  const email     = document.getElementById("doctorEmail").value.trim();
  const password  = document.getElementById("doctorPassword").value.trim();
  const phone    = document.getElementById("doctorPhone").value.trim();

  const availabilityCheckboxes = document.querySelectorAll(
    "input[name='availability']:checked"
  );
  const availability = Array.from(availabilityCheckboxes).map((cb) => cb.value);

  const doctor = { name, specialty, email, password, phone, availability };

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
  - Filters doctors by name, time, or specialty
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
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

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
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/
