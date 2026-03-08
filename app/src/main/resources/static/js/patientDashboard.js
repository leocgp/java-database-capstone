// patientDashboard.js
import { getDoctors } from './services/doctorServices.js';
import { openModal } from './components/modals.js';
import { createDoctorCard } from './components/doctorCard.js';
import { filterDoctors } from './services/doctorServices.js';
import { patientSignup, patientLogin } from './services/patientServices.js';

window.openModal = openModal;
window.closeModal = closeModal;

document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
});

document.addEventListener("DOMContentLoaded", () => {
  const btn = document.getElementById("patientSignup");
  if (btn) {
    btn.addEventListener("click", () => openModal("patientSignup"));
  }
});

document.addEventListener("DOMContentLoaded", () => {
  const loginBtn = document.getElementById("patientLogin")
  if (loginBtn) {
    loginBtn.addEventListener("click", () => {
      openModal("patientLogin")
    })
  }
})

function loadDoctorCards() {
  getDoctors()
    .then(doctors => {
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = "";

      doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
      });
    })
    .catch(error => {
      console.error("Failed to load doctors:", error);
    });
}
const searchBar           = document.getElementById("searchBar");
const filterTime          = document.getElementById("filterTime");
const filterSpecialization = document.getElementById("filterSpecialization");

if (searchBar)            searchBar.addEventListener("input", filterDoctorsOnChange);
if (filterTime)           filterTime.addEventListener("change", filterDoctorsOnChange);
if (filterSpecialization) filterSpecialization.addEventListener("change", filterDoctorsOnChange);

async function filterDoctorsOnChange() {
    const name           = document.getElementById("searchBar")?.value.trim() || null;
    const time           = document.getElementById("filterTime")?.value || null;
    const specialization = document.getElementById("filterSpecialization")?.value || null;

    try {
        const data = await filterDoctors(name, time, specialization);
        const doctors = Array.isArray(data) ? data : data?.doctors ?? [];

        const contentDiv = document.getElementById("content");
        contentDiv.innerHTML = "";

        if (!doctors || doctors.length === 0) {
            contentDiv.innerHTML = "<p>No doctors found.</p>";
            return;
        }
        doctors.forEach(doctor => {
            const card = createDoctorCard(doctor);
            contentDiv.appendChild(card);
        });

    } catch (error) {
        console.error("Error filtering doctors:", error);
    }
}

window.signupPatient = async function () {
  try {
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const phone = document.getElementById("phone").value;
    const address = document.getElementById("address").value;

    const data = { fullName: name, email, password, phone, address };
    const { success, message } = await patientSignup(data);
    if (success) {
      alert(message);
      document.getElementById("modal").style.display = "none";
      window.location.reload();
    }
    else alert(message);
  } catch (error) {
    console.error("Signup failed:", error);
    alert("❌ An error occurred while signing up.");
  }
};

window.loginPatient = async function () {
  try {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const data = {
      identifier: email,
      password
    }
    console.log("loginPatient :: ", data)
    const response = await patientLogin(data);
    console.log("Status Code:", response.status);
    console.log("Response OK:", response.ok);
    if (response.ok) {
      const result = await response.json();
      console.log(result);
      selectRole('loggedPatient');
      localStorage.setItem('token', result.token)
      window.location.href = '/pages/loggedPatientDashboard.html';
    } else {
      alert('❌ Invalid credentials!');
    }
  }
  catch (error) {
    alert("❌ Failed to Login : ", error);
    console.log("Error :: loginPatient :: ", error)
  }
}
