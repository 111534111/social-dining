package com.example.socialdining.model;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String country;
    private String city;
    private String district;
    private String fullAddress;

    @PrePersist
    private void buildFullAddress() {
        this.fullAddress = String.join(" ", country, city, district);
    }

    // getters & setters
    public Long getId() { return id; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getFullAddress() { return fullAddress; }
}
