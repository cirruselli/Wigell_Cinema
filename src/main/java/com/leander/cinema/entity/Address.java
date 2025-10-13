package com.leander.cinema.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, name = "street", length = 100)
    private String street;

    @Column(nullable = false, name = "postal_code", length = 5)
    private String postalCode;

    @Column(nullable = false, name = "city", length = 100)
    private String city;

    @ManyToMany(mappedBy = "addresses", fetch = FetchType.LAZY)
    private List<Customer> customers = new ArrayList<>();


    public Address() {
    }

    public Address(String street, String postalCode, String city, List<Customer> customers) {
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
        this.customers = customers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
}
