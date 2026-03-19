package com.doctor_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

@Entity
@Table(name = "doctors")
public class Doctor {

    // ================== PRIMARY KEY ==================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================== BASIC DETAILS ==================
    @Column(nullable = false)
    @NotBlank(message = "Doctor name is required")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Specialization is required")
    private String specialization;

    @Column(nullable = false)
    @NotBlank(message = "Qualification is required")
    private String qualification;

    @Column(nullable = false)
    @NotBlank(message = "Experience is required")
    private String experience;

    // ================== CONTACT ==================
    @Column(nullable = false)
    @NotBlank(message = "Contact is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String contact;

    // ================== MEDIA ==================
    @Column(nullable = false, length = 2000)
    @NotBlank(message = "Image URL is required")
    @Size(max = 2000, message = "URL too long")
    private String url;

    // ================== LOCATION ==================
    @ManyToOne
    @JoinColumn(name = "state_id")
    @NotNull(message = "State is required")
    private State state;

    @ManyToOne
    @JoinColumn(name = "city_id")
    @NotNull(message = "City is required")
    private City city;

    @ManyToOne
    @JoinColumn(name = "area_id")
    @NotNull(message = "Area is required")
    private Area area;

    // ================== CLINIC ==================
    @Column(nullable = false)
    @NotBlank(message = "Clinic name is required")
    private String clinicName;

    @Column(nullable = false, length = 1000)
    @NotBlank(message = "Address is required")
    @Size(max = 1000, message = "Address too long")
    private String address;

    // ================== RELATION ==================
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Valid
//    @JsonManagedReference

    private List<DoctorAppointmentSchedule> appointmentSchedules;

    // ================== GETTERS & SETTERS ==================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public State getState() { return state; }
    public void setState(State state) { this.state = state; }

    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    public Area getArea() { return area; }
    public void setArea(Area area) { this.area = area; }

    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<DoctorAppointmentSchedule> getAppointmentSchedules() {
        return appointmentSchedules;
    }

    public void setAppointmentSchedules(List<DoctorAppointmentSchedule> appointmentSchedules) {
        this.appointmentSchedules = appointmentSchedules;
    }
}