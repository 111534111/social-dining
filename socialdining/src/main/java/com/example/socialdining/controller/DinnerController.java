package com.example.socialdining.controller;

import com.example.socialdining.dto.DinnerApplyRequest;
import com.example.socialdining.model.Address;
import com.example.socialdining.model.Dinner;
import com.example.socialdining.model.User;
import com.example.socialdining.repository.AddressRepository;
import com.example.socialdining.repository.DinnerRepository;
import com.example.socialdining.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/dinner")
public class DinnerController {

    private final DinnerRepository dinnerRepo;
    private final UserRepository userRepo;
    private final AddressRepository addressRepo;

    public DinnerController(DinnerRepository dinnerRepo,
                            UserRepository userRepo,
                            AddressRepository addressRepo) {
        this.dinnerRepo = dinnerRepo;
        this.userRepo = userRepo;
        this.addressRepo = addressRepo;
    }

    @Value("${dinner.image.upload-dir:uploads/dinner-images}")
    private String uploadDir;

    @PostMapping(value = "/apply", consumes = "multipart/form-data")
    public ResponseEntity<?> applyDinner(
            @RequestPart("data") DinnerApplyRequest req,
            @RequestPart("image") MultipartFile imageFile) throws IOException {

        // 1. 取得擁有者
        Optional<User> userOpt = userRepo.findByEmail(req.getOwnerEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("找不到使用者！");
        }

        // 2. 儲存圖片
        String filename = System.currentTimeMillis() + "_" +
                StringUtils.cleanPath(imageFile.getOriginalFilename());
        Path target = Path.of(uploadDir).resolve(filename);
        Files.createDirectories(target.getParent());
        Files.copy(imageFile.getInputStream(), target);

        // 3. 儲存 Address
        Address addr = new Address();
        addr.setCountry(req.getCountry());
        addr.setCity(req.getCity());
        addr.setDistrict(req.getDistrict());
        addressRepo.save(addr);

        // 4. 組裝 Dinner
        Dinner dinner = new Dinner();
        dinner.setTitle(req.getTitle());
        dinner.setCuisine(req.getCuisine());
        dinner.setCapacity(req.getCapacity());
        dinner.setDescription(req.getDescription());
        dinner.setImagePath(filename);
        dinner.setOwner(userOpt.get());
        dinner.setAddress(addr);

        // 5. 日期轉換
        Set<LocalDate> dates = new HashSet<>();
        for (String d : req.getAvailableDates()) {
            dates.add(LocalDate.parse(d));
        }
        dinner.setAvailableDates(dates);

        Dinner saved = dinnerRepo.save(dinner);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/my")
    public ResponseEntity<?> myDinners(@RequestParam String email) {
        return userRepo.findByEmail(email)
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(dinnerRepo.findByOwner(u)))
                .orElseGet(() -> ResponseEntity.badRequest().body("找不到使用者！"));
    }

    @GetMapping("/all")
    public List<Dinner> all() {
        return dinnerRepo.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDinner(@PathVariable Long id,
                                          @RequestParam String email) {
        Optional<Dinner> dinnerOpt = dinnerRepo.findById(id);
        if (dinnerOpt.isEmpty()) {
            return ResponseEntity.status(404).body("找不到餐會！");
        }
        Dinner dinner = dinnerOpt.get();
        if (!dinner.getOwner().getEmail().equals(email)) {
            return ResponseEntity.status(403).body("無權刪除此餐會！");
        }
        dinnerRepo.delete(dinner);
        return ResponseEntity.ok("刪除成功");
    }

    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) throws IOException {
        Path file = Path.of(uploadDir).resolve(filename);
        Resource resource = new UrlResource(file.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }
        String contentType = Files.probeContentType(file);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
