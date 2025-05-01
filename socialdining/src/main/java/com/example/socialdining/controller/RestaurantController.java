package com.example.socialdining.controller;

import com.example.socialdining.model.Restaurant;
import com.example.socialdining.model.User;
import com.example.socialdining.repository.RestaurantRepository;
import com.example.socialdining.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    // 申請餐廳 API（POST）
    @PostMapping("/apply")
    public ResponseEntity<?> applyRestaurant(@RequestBody Restaurant restaurantRequest) {
        // restaurantRequest 中必須包含 owner 的 email 資料
        // 這裡假設前端傳入的 JSON 內，owner 欄位只包一個 email
        if (restaurantRequest.getOwner() == null || restaurantRequest.getOwner().getEmail() == null) {
            return ResponseEntity.badRequest().body("必須包含擁有者電子郵件！");
        }
        Optional<User> userOpt = userRepository.findByEmail(restaurantRequest.getOwner().getEmail());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body("找不到該使用者！");
        }
        restaurantRequest.setOwner(userOpt.get());
        // 儲存餐廳，注意餐廳的 availableDates 字串格式例如："2023-10-10,2023-10-11"
        Restaurant saved = restaurantRepository.save(restaurantRequest);
        return ResponseEntity.ok(saved);
    }

    // 取得自己申請的餐廳 (GET /api/restaurant/my?email={email})
    @GetMapping("/my")
    public ResponseEntity<?> getMyRestaurants(@RequestParam("email") String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body("找不到該使用者！");
        }
        List<Restaurant> myRestaurants = restaurantRepository.findByOwner(userOpt.get());
        return ResponseEntity.ok(myRestaurants);
    }

    // 取得所有餐廳 (GET /api/restaurant/all)
    @GetMapping("/all")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return ResponseEntity.ok(restaurants);
    }
}
