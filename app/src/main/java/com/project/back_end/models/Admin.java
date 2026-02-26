package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotNull(message = "email cannot be null")
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull(message = "password cannot be null")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false)
    @Size(min = 6)
    private String password;

    @NotNull(message = "fullName cannot be null")
    @Column(name = "full_name", nullable = false)
    @Size(max = 30)
    private String fullName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Admin() {}
    public Admin(String email, String password, String fullName) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    public Long getId() {return id;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public String getFullName() {return fullName;}
    public void setFullName(String fullName) {this.fullName = fullName;}

    public LocalDateTime getCreatedAt() {return createdAt;}
}