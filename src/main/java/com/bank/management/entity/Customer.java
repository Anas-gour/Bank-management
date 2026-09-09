package com.bank.management.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq_gen")
    @SequenceGenerator(
            name = "customer_seq_gen",
            sequenceName = "customer_seq",
            allocationSize = 1
    )
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String address;

    public Customer() {
    }

    public Customer(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}