package com.patient_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name="patients")
public class Patient {

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name="name" ,nullable=false)
     private String name;


    @Column(name="email" ,nullable=false)
    private String email;

    @Column(name="contact" ,nullable=false)
    private String contact;
    @Column(name="age" ,nullable=false)
    private int age;
    @Column(name="gender" ,nullable=false)
    private String gender;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
