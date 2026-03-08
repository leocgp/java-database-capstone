export function createPatientRow(patient, appointmentId, doctorId) {
  const tr = document.createElement("tr");
  console.log("CreatePatientRow :: ", doctorId)
  tr.innerHTML = `
      <td class="patient-id">${patient.id}</td>
      <td>${patient.fullName}</td>
      <td>${patient.phone}</td>
      <td>${patient.email}</td>
      <td><img src="../assets/images/addPrescriptionIcon/addPrescription.png" alt="addPrescriptionIcon" class="prescription-btn" data-id="${patient.id}"></img></td>
    `;

  tr.querySelector(".patient-id").addEventListener("click", () => {
    window.location.href = `/pages/patientRecord.html?id=${patient.id}&doctorId=${doctorId}`;
  });

  tr.querySelector(".prescription-btn").addEventListener("click", () => {
    window.location.href = `/pages/addPrescription.html?appointmentId=${appointmentId}&patientName=${patient.fullName}`;
  });
    console.log("URL:", window.location.href);
    console.log("params:", window.location.search);
  return tr;
}
