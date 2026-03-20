package com.doctor_service.service;

import com.doctor_service.dto.SearchResultDto;
import com.doctor_service.entity.*;
import com.doctor_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DoctorService {

    @Autowired
    private DoctorsRepository doctorsRepository;

    @Autowired
    private TimeSlotsRepository timeSlotsRepository;

    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final AreaRepository areaRepository;

    public DoctorService(StateRepository stateRepository,
                         CityRepository cityRepository,
                         AreaRepository areaRepository) {
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.areaRepository = areaRepository;
    }


    public Object searchDoctors(String specialization,
                                String areaName,
                                int pageNo,
                                int pageSize,
                                String sortDir,
                                String sortBy) {

        LocalDate currentDate = LocalDate.now();

        //sorting
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        //pagenation
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        // ✅ NEW (handle all cases)
        Page<Doctor> page;

        if (specialization != null && areaName != null) {
            page = doctorsRepository.findBySpecializationAndArea_Name(
                    specialization, areaName, pageable
            );
        } else if (specialization != null) {
            page = doctorsRepository.findBySpecialization(specialization, pageable);
        } else if (areaName != null) {
            page = doctorsRepository.findByArea_Name(areaName, pageable);
        } else {
            page = doctorsRepository.findAll(pageable);
        }


        // ✅ CORRECT (use paginated data)
        List<Doctor> doctors = page.getContent();

        System.out.println("Doctors found: " + doctors.size());
        System.out.println("Specialization: " + specialization);
        System.out.println("AreaName: " + areaName);


        List<SearchResultDto> result = new ArrayList<>();


        for (Doctor doctor : doctors) {

            SearchResultDto dto = new SearchResultDto();

            List<LocalDate> validDates = new ArrayList<>();
            List<LocalTime> allTimeSlots = new ArrayList<>();

            //fetch appointment schedules
            List<DoctorAppointmentSchedule> schedules = doctor.getAppointmentSchedules();


            if (schedules != null) {

                for (DoctorAppointmentSchedule schedule : schedules) {

                    LocalDate scheduleDate = schedule.getDate();
                    LocalTime now = LocalTime.now();

                    validDates.add(scheduleDate);

//fetch timeslots

                    List<TimeSlots> timeSlots =
                            timeSlotsRepository.getAllTimeSlots(schedule.getId());


                    for (TimeSlots ts : timeSlots) {

                        LocalTime slotTime = ts.getTime();

                        // future time   same date
                        if (scheduleDate.isEqual(currentDate)) {
                            if (slotTime.isAfter(now)) {
                                allTimeSlots.add(slotTime);
                            }
                        }
                        // past date-invalid
                        else if (scheduleDate.isBefore(currentDate)) {
                            System.out.println("invalid date");
                        }
                        //future date-all time
                        else if (scheduleDate.isAfter(currentDate)) {
                            allTimeSlots.add(slotTime);
                        }
                    }
                }
            }

            dto.setId(doctor.getId());
            dto.setUrl(doctor.getUrl());
            dto.setName(doctor.getName());
            dto.setArea(doctor.getArea().getName());
            dto.setCity(doctor.getCity().getName());
            dto.setQualification(doctor.getQualification());
            dto.setSpecialization(doctor.getSpecialization());
            dto.setDates(validDates);
            dto.setTimeSlots(allTimeSlots);

            result.add(dto);
        }

        return Map.of(
                "content", result,
                "page", page.getNumber(),
                "size", page.getSize(),
                "totalElements", page.getTotalElements(),
                "totalPages", page.getTotalPages(),
                "last", page.isLast()
        );
    }


    // ✅ Get all states
    public List<State> getAllStates() {
        return stateRepository.findAll();
    }

    // ✅ Get cities by stateId
    public List<City> getCitiesByState(Long stateId) {
        return cityRepository.findByStateId(stateId);
    }

    // ✅ Get areas by cityId
    public List<Area> getAreasByCity(Long cityId) {
        return areaRepository.findByCityId(cityId);
    }


}