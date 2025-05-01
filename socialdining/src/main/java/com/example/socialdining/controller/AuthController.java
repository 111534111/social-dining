package com.example.socialdining.controller;

import com.example.socialdining.model.LoginRequest;
import com.example.socialdining.model.UpdateProfileRequest;
import com.example.socialdining.model.User;
import com.example.socialdining.model.UserDTO;
import com.example.socialdining.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(loginRequest.getPassword())) {
                String displayName = user.getNickname() != null ? user.getNickname() : user.getUsername();
                return ResponseEntity.ok("登入成功！歡迎 " + displayName);
            } else {
                return ResponseEntity.status(401).body("密碼錯誤！");
            }
        }
        return ResponseEntity.status(401).body("找不到該電子郵件！");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody LoginRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("該電子郵件已被註冊！");
        }
        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setUsername(registerRequest.getEmail());
        newUser.setNickname(registerRequest.getUsername());
        newUser.setPassword(registerRequest.getPassword());
        userRepository.save(newUser);
        return ResponseEntity.ok("註冊成功，請重新登入！");
    }

    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody UpdateProfileRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 更新暱稱 (若有提供且不為空)
            if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
                user.setNickname(request.getNickname().trim());
            }
            // 處理密碼更新：如果前端提供新密碼，則必須檢查 oldPassword 是否正確
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                // 若未提供舊密碼或舊密碼不正確，則回傳錯誤
                if (request.getOldPassword() == null || !request.getOldPassword().equals(user.getPassword())) {
                    return ResponseEntity.status(401).body("舊密碼不正確！");
                }
                user.setPassword(request.getPassword());
            }
            userRepository.save(user);
            return ResponseEntity.ok("更新成功！");
        }
        return ResponseEntity.status(404).body("找不到使用者！");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam("email") String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            UserDTO dto = new UserDTO();
            dto.setEmail(user.getEmail());
            dto.setNickname(user.getNickname());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.status(404).body("使用者不存在！");
    }
}
