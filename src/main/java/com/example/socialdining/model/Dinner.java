package com.example.socialdining.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "dinners")
@Data
public class Dinner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String location;
    private String cuisine;
    private Integer capacity;

    @Column(length = 1000)
    private String description;

    private String imagePath;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "dinner_dates", joinColumns = @JoinColumn(name = "dinner_id"))
    @Column(name = "date")
    private Set<LocalDate> availableDates;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "address_id")
    private Address address;
}
