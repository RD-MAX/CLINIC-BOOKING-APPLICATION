package com.doctor_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SearchResultDto {

    private long id;//doctor id
   @NotBlank(message  ="name required")
    private String name;
    @NotBlank(message  ="specialization required")
    private String specialization;
    @NotBlank(message  ="qualification required")
    private String qualification;
    @NotBlank(message  ="area name required")
    private String area;
    @NotBlank(message  ="city name required")
    private String city;
    @NotEmpty(message = "At least one date is required")
    private List<LocalDate> dates;//appointment date
    @NotEmpty(message = "Time slots cannot be empty")
    private List<LocalTime> timeSlots;
    @NotBlank(message  ="image url required")
    @Size(max = 2000, message = "URL too long")
    private String url;// image url--photo of doctor


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<LocalDate> getDates() {
        return dates;
    }

    public void setDates(List<LocalDate> dates) {
        this.dates = dates;
    }

    public List<LocalTime> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<LocalTime> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
