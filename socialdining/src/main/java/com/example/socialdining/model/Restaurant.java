package com.example.socialdining.model;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 餐廳名稱
    @Column(nullable = false)
    private String name;

    // 餐廳地點
    @Column(nullable = false)
    private String location;

    // 料理類型
    @Column(nullable = false)
    private String cuisine;

    // 餐廳簡介
    @Column(length = 1000)
    private String description;

    // 餐廳圖片（這邊暫以圖片檔名作為示範，實際上可儲存 URL 或使用 Blob）
    private String image;

    // 可舉辦日期，這裡以逗號分隔的字串格式儲存，如 "2023-10-10,2023-10-11,2023-10-13"
    @Column(nullable = false)
    private String availableDates;

    // 申請該餐廳的使用者（餐廳擁有者）
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    // Getter 與 Setter
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

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getCuisine() {
        return cuisine;
    }
    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getAvailableDates() {
        return availableDates;
    }
    public void setAvailableDates(String availableDates) {
        this.availableDates = availableDates;
    }

    public User getOwner() {
        return owner;
    }
    public void setOwner(User owner) {
        this.owner = owner;
    }
}
