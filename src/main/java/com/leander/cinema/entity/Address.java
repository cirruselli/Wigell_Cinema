package com.leander.cinema.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, name = "street")
    private String street;

    @Column(nullable = false, name = "postal_code", length = 5)
    private String postalCode;

    @Column(nullable = false, name = "city", length = 100)
    private String city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}
