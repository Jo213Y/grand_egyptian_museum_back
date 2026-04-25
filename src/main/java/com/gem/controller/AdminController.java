package com.gem.controller;

import com.gem.dto.*;
import com.gem.model.User;
import com.gem.repository.UserRepository;
import com.gem.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService   adminService;
    private final UserRepository userRepo;

    /** GET /api/admin/statistics */
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        try {
            return ResponseEntity.ok(adminService.getStatistics());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** GET /api/admin/halls */
    @GetMapping("/halls")
    public ResponseEntity<?> getAllHalls() {
        return ResponseEntity.ok(adminService.getAllHalls());
    }

    /** PUT /api/admin/halls/{id} */
    @PutMapping("/halls/{id}")
    public ResponseEntity<?> updateHall(
            @PathVariable Long id,
            @RequestBody HallUpdateRequest req) {
        try {
            return ResponseEntity.ok(adminService.updateHall(id, req));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** GET /api/admin/users */
    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        List<Map<String, Object>> users = userRepo.findAll().stream()
            .map(u -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id",       u.getId());
                m.put("fullName", u.getFullName());
                m.put("email",    u.getEmail());
                m.put("phone",    u.getPhone());
                m.put("role",     u.getRole().name());
                return m;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /** DELETE /api/admin/users/{id} */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long userId,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            userRepo.deleteById(userId);
            return ResponseEntity.ok(Map.of("message", "User deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** GET /api/admin/logs — simple activity log */
    @GetMapping("/logs")
    public ResponseEntity<?> getLogs() {
        // سجلات مبسطة — يمكن توسيعها لاحقاً
        List<Map<String, Object>> logs = new ArrayList<>();
        logs.add(Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "action",    "System",
            "detail",    "Admin panel accessed"
        ));
        return ResponseEntity.ok(logs);
    }
}
