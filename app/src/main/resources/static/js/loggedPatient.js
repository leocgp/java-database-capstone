// loggedPatient.js 
import { getDoctors } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';
import { filterDoctors } from './services/doctorServices.js';
import { bookAppointment } from './services/appointmentRecordService.js';


document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
});

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

export function showBookingOverlay(e, doctor, patient) {
  const button = e.target;
  const rect = button.getBoundingClientRect();
  console.log(patient.name)
  console.log(patient)
  const ripple = document.createElement("div");
  ripple.classList.add("ripple-overlay");
  ripple.style.left = `${e.clientX}px`;
  ripple.style.top = `${e.clientY}px`;
  document.body.appendChild(ripple);

  setTimeout(() => ripple.classList.add("active"), 50);

  const modalApp = document.createElement("div");
  modalApp.classList.add("modalApp");

  modalApp.innerHTML = `
    <h2>Book Appointment</h2>
    <input class="input-field" type="text" value="${patient.fullName}" disabled />
    <input class="input-field" type="text" value="${doctor.fullName}" disabled />
    <input class="input-field" type="text" value="${doctor.specialization}" disabled/>
    <input class="input-field" type="email" value="${doctor.email}" disabled/>
    <input class="input-field" type="date" id="appointment-date" />
    <select class="input-field" id="appointment-time">
      <option value="">Select time</option>
      ${doctor.availableTimes.map(t => `<option value="${t}">${t}</option>`).join('')}
    </select>
    <button class="confirm-booking">Confirm Booking</button>
  `;

  document.body.appendChild(modalApp);

  setTimeout(() => modalApp.classList.add("active"), 600);

  modalApp.querySelector(".confirm-booking").addEventListener("click", async () => {
    const date = modalApp.querySelector("#appointment-date").value;
    const time = modalApp.querySelector("#appointment-time").value;
    const token = localStorage.getItem("token");
    const startTime = time.split('-')[0];
    const appointment = {
      doctor: { id: doctor.id },
      patient: { id: patient.id },
      appointmentTime: `${date}T${startTime}:00`,
      status: 0
    };


    const { success, message } = await bookAppointment(appointment, token);

    if (success) {
      alert("Appointment Booked successfully");
      ripple.remove();
      modalApp.remove();
    } else {
      alert("❌ Failed to book an appointment :: " + message);
    }
  });
}

document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();

    const searchBar     = document.getElementById("searchBar");
    const filterTime    = document.getElementById("filterTime");
    const filterSpecialization = document.getElementById("filterSpecialization");

    if (searchBar)        searchBar.addEventListener("input", filterDoctorsOnChange);
    if (filterTime)       filterTime.addEventListener("change", filterDoctorsOnChange);
    if (filterSpecialization)  filterSpecialization.addEventListener("change", filterDoctorsOnChange);
});

async function filterDoctorsOnChange() {
    const name           = document.getElementById("searchBar")?.value.trim() || null;
    const time           = document.getElementById("filterTime")?.value || null;
    const specialization = document.getElementById("filterSpecialization")?.value || null;

    try {
        const data = await filterDoctors(name, time, specialization);
        const doctors = Array.isArray(data) ? data : data?.doctors ?? [];

        const contentDiv = document.getElementById("content");
        contentDiv.innerHTML = "";

        if (doctors.length === 0) {
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

export function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });

}
