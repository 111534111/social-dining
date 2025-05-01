package com.example.socialdining.controller;

import com.example.socialdining.dto.DinnerApplyRequest;
import com.example.socialdining.model.Dinner;
import com.example.socialdining.model.User;
import com.example.socialdining.repository.DinnerRepository;
import com.example.socialdining.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    /**
     * 取回任意上傳圖片（封面或其他照片）
     */
    @GetMapping("/api/dinner/image/{filename:.+}")
    public ResponseEntity<Resource> serveDinnerImage(@PathVariable String filename) throws IOException {
        Path file = Path.of(uploadDir).resolve(filename);
        Resource resource = new UrlResource(file.toUri());
        String contentType = Files.probeContentType(file);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    /**
     * 申請餐會：接收 JSON data + 封面照 + 多張其他照片
     */
    @PostMapping(value = "/api/dinner/apply", consumes = "multipart/form-data")
    public ResponseEntity<?> applyDinner(
            @RequestPart("data") DinnerApplyRequest req,
            @RequestPart("cover") MultipartFile coverFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {

        // 找使用者
        Optional<User> userOpt = userRepo.findByEmail(req.getOwnerEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("找不到使用者！");
        }

        // 儲存封面照
        String coverName = System.currentTimeMillis() + "_" +
                StringUtils.cleanPath(coverFile.getOriginalFilename());
        Path coverTarget = Path.of(uploadDir).resolve(coverName);
        Files.createDirectories(coverTarget.getParent());
        Files.copy(coverFile.getInputStream(), coverTarget);

        // 儲存其他照片
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

        // 組裝 Dinner 物件
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

        // 處理日期
        Set<LocalDate> dates = req.getAvailableDates().stream()
                .map(LocalDate::parse)
                .collect(Collectors.toSet());
        dinner.setAvailableDates(dates);

        // 存檔並回傳
        Dinner saved = dinnerRepo.save(dinner);
        return ResponseEntity.ok(saved);
    }
}
