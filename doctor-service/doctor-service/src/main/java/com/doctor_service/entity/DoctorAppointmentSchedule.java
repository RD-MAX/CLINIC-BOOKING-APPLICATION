package com.doctor_service.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "doctor_appointment_schedule")
public class DoctorAppointmentSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    //    @JsonBackReference
    @JsonIgnore
    private Doctor doctor;

    @OneToMany(mappedBy = "doctorAppointmentSchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonManagedReference
    @Valid
    @NotEmpty(message = "Time slots cannot be empty")
    private List<TimeSlots> timeSlots;

    @FutureOrPresent(message = "Date must be today or future")
    @NotNull(message = "Date is required")
    @Column(name = "date")
    private LocalDate date;

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<TimeSlots> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlots> timeSlots) {
        this.timeSlots = timeSlots;
    }
}