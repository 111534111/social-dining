package com.example.socialdining.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "dinners")
@Data
public class Dinner {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // 公開顯示到區的地址
    private String publicAddress;

    // 完整地址，只在訂購完成後顯示
    private String fullAddress;

    private String cuisine;
    private Integer capacity;

    @Column(length = 1000)
    private String description;

    // 封面照檔名
    private String coverImagePath;

    // 其餘照片檔名
    @ElementCollection
    @CollectionTable(name = "dinner_images", joinColumns = @JoinColumn(name = "dinner_id"))
    @Column(name = "path")
    private List<String> imagePaths;

    @ElementCollection
    @CollectionTable(name = "dinner_dates", joinColumns = @JoinColumn(name = "dinner_id"))
    @Column(name = "date")
    private Set<LocalDate> availableDates;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private User owner;
}