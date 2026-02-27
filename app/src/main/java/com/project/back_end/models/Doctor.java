package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;

@Entity
@Table(name = "doctor")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotNull(message = "email cannot be null")
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull(message = "password cannot be null")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    @Size(min = 6)
    private String password;

    @NotNull(message = "fullName cannot be null")
    @Column(name = "full_name", nullable = false)
    @Size(max = 30)
    private String fullName;

    @NotNull(message = "specialization cannot be null")
    @Column(nullable = false)
    private String specialization;

    @Pattern(regexp = "\\d{11}", message = "Phone number must be 11 digits")
    @Column
    private String phone;

    @ElementCollection
    @Column
    private List<String> availableTimes;

    @NotNull(message = "isActive cannot be null")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public Doctor() {}

    public Doctor(String email, String password, String fullName, String specialization, String phone) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.specialization = specialization;
        this.phone = phone;
        this.isActive = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
}