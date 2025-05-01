package com.example.socialdining.controller;

import com.example.socialdining.dto.DinnerApplyRequest;
import com.example.socialdining.model.Dinner;
import com.example.socialdining.model.User;
import com.example.socialdining.repository.DinnerRepository;
import com.example.socialdining.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class DinnerController {

    private final DinnerRepository dinnerRepo;
    private final UserRepository userRepo;

    public DinnerController(DinnerRepository dinnerRepo, UserRepository userRepo) {
        this.dinnerRepo = dinnerRepo;
        this.userRepo = userRepo;
    }

    @Value("${dinner.image.upload-dir:uploads/dinner-images}")
    private String uploadDir;

    @PostMapping(value = "/api/dinner/apply", consumes = "multipart/form-data")
    public ResponseEntity<?> applyDinner(
            @RequestPart("data") DinnerApplyRequest req,
            @RequestPart("cover") MultipartFile coverFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {

        // 1. 找使用者
        Optional<User> userOpt = userRepo.findByEmail(req.getOwnerEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("找不到使用者！");
        }

        // 2. 儲存封面照
        String coverName = System.currentTimeMillis() + "_" +
                StringUtils.cleanPath(coverFile.getOriginalFilename());
        Path coverTarget = Path.of(uploadDir).resolve(coverName);
        Files.createDirectories(coverTarget.getParent());
        Files.copy(coverFile.getInputStream(), coverTarget);

        // 3. 儲存其他圖片
        List<String> imagePaths = new ArrayList<>();
        if (images != null) {
            for (MultipartFile img : images) {
                String fn = System.currentTimeMillis() + "_" +
                        StringUtils.cleanPath(img.getOriginalFilename());
                Path p = Path.of(uploadDir).resolve(fn);
                Files.copy(img.getInputStream(), p);
                imagePaths.add(fn);
            }
        }

        // 4. 組裝 Dinner
        Dinner dinner = new Dinner();
        dinner.setTitle(req.getTitle());
        dinner.setPublicAddress(req.getPublicAddress());
        dinner.setFullAddress(req.getFullAddress());
        dinner.setCuisine(req.getCuisine());
        dinner.setCapacity(req.getCapacity());
        dinner.setDescription(req.getDescription());
        dinner.setCoverImagePath(coverName);
        dinner.setImagePaths(imagePaths);
        dinner.setOwner(userOpt.get());

        // 5. 處理日期
        Set<LocalDate> dates = req.getAvailableDates().stream()
                .map(LocalDate::parse)
                .collect(Collectors.toSet());
        dinner.setAvailableDates(dates);

        Dinner saved = dinnerRepo.save(dinner);
        return ResponseEntity.ok(saved);
    }
}
