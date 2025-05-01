package com.example.socialdining.repository;

import com.example.socialdining.model.Restaurant;
import com.example.socialdining.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    // 根據擁有者取得該使用者申請的餐廳
    List<Restaurant> findByOwner(User owner);
}
