// src/main/java/com/example/socialdining/repository/DinnerRepository.java
package com.example.socialdining.repository;

import com.example.socialdining.model.Dinner;
import com.example.socialdining.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DinnerRepository extends JpaRepository<Dinner, Long> {

    // 把 availableDates 一併抓回來，避免 N+1
    @EntityGraph(attributePaths = "availableDates")
    List<Dinner> findByOwner(User owner);
}
