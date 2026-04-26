package com.gem.controller;

import com.gem.dto.*;
import com.gem.model.AdminLog;
import com.gem.repository.AdminLogRepository;
import com.gem.service.AdminLogService;
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
    private final com.gem.repository.BookingRepository bookingRepo;
    private final AdminLogService logService;
    private final AdminLogRepository logRepo;

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
            HallResponse updated = adminService.updateHall(id, req);
            logService.log("UPDATE_HALL", "Updated hall ID=" + id + " → " + req);
            return ResponseEntity.ok(updated);
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
                    m.put("id",              u.getId());
                    m.put("fullName",        u.getFullName());
                    m.put("email",           u.getEmail());
                    m.put("phone",           u.getPhone());
                    m.put("nationality",     u.getNationality());
                    m.put("ssn",             u.getSsn());
                    m.put("passportNumber",  u.getPassportNumber());
                    m.put("role",            u.getRole().name());
                    m.put("createdAt",       u.getCreatedAt() != null ? u.getCreatedAt().toString() : null);
                    // count tickets booked by this user
                    long ticketCount = bookingRepo.findByUserOrderByOrderDateDesc(u).stream()
                            .filter(b -> "Confirmed".equalsIgnoreCase(b.getOrderStatus()))
                            .mapToLong(b -> b.getTickets() != null ? b.getTickets().size() : 0)
                            .sum();
                    m.put("ticketsBooked", ticketCount);
                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /** PUT /api/admin/users/{id}/block — toggle USER ↔ BLOCK */
    @PutMapping("/users/{userId}/block")
    public ResponseEntity<?> toggleBlock(
            @PathVariable Long userId,
            @RequestBody(required = false) Map<String, String> body) {
        return userRepo.findById(userId).map(u -> {
            Integer currentRoleId = u.getRoleId() != null ? u.getRoleId() : 1;
            Integer newRoleId     = currentRoleId.equals(3) ? 1 : 3;
            u.setRoleId(newRoleId);
            userRepo.save(u);
            String newRole = newRoleId.equals(3) ? "BLOCK" : "USER";
            String reason  = (body != null && body.get("reason") != null && !body.get("reason").isBlank())
                    ? body.get("reason") : null;
            String detail  = (newRoleId.equals(3) ? "Blocked" : "Unblocked") + " user: " + u.getEmail()
                    + (reason != null ? " | Reason: " + reason : "");
            logService.log(newRoleId.equals(3) ? "BLOCK_USER" : "UNBLOCK_USER", detail);
            return ResponseEntity.ok(Map.of(
                    "message", "Role updated to " + newRole,
                    "role",    newRole
            ));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** POST /api/admin/users — add new admin */
    @PostMapping("/users")
    public ResponseEntity<?> addAdmin(@RequestBody Map<String, String> body) {
        try {
            String email    = body.get("email");
            String password = body.get("password");
            String fullName = body.get("fullName");

            if (email == null || password == null || fullName == null)
                return ResponseEntity.badRequest().body(Map.of("message", "fullName, email and password are required"));

            if (userRepo.existsByEmail(email))
                return ResponseEntity.badRequest().body(Map.of("message", "Email already exists"));

            com.gem.model.User admin = new com.gem.model.User();
            admin.setFullName(fullName);
            admin.setEmail(email);
            admin.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(password));
            admin.setRoleId(2); // ADMIN
            if (body.get("ssn") != null && !body.get("ssn").isBlank())
                admin.setSsn(body.get("ssn"));
            userRepo.save(admin);
            logService.log("ADD_ADMIN", "Added new admin: " + email);
            return ResponseEntity.ok(Map.of("message", "Admin created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** DELETE /api/admin/users/{id} */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long userId,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            String reason = body != null ? body.getOrDefault("reason", "No reason provided") : "No reason provided";
            userRepo.findById(userId).ifPresent(u ->
                    logService.log("DELETE_USER", "Deleted user: " + u.getEmail() + " | Reason: " + reason)
            );
            userRepo.deleteById(userId);
            return ResponseEntity.ok(Map.of("message", "User deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** GET /api/admin/logs */
    @GetMapping("/logs")
    public ResponseEntity<?> getLogs() {
        List<Map<String, Object>> logs = logRepo.findAllByOrderByTimestampDesc()
                .stream()
                .map(l -> {
                    java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
                    m.put("id",         l.getId());
                    m.put("adminName",  l.getAdminName() != null ? l.getAdminName() : l.getAdminEmail());
                    m.put("adminEmail", l.getAdminEmail());
                    m.put("action",     l.getAction());
                    m.put("detail",     l.getDetail());
                    m.put("timestamp",  l.getTimestamp().toString());
                    return m;
                }).toList();
        return ResponseEntity.ok(logs);
    }
}