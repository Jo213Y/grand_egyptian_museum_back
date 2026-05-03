package com.gem.controller;

import com.gem.dto.*;
import com.gem.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody BookingRequest req,
            @AuthenticationPrincipal UserDetails user) {
        try {
            return ResponseEntity.ok(bookingService.create(req, user.getUsername()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> myBookings(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(bookingService.getMyBookings(user.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        try {
            return ResponseEntity.ok(bookingService.getById(id, user.getUsername()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        try {
            return ResponseEntity.ok(bookingService.cancel(id, user.getUsername()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        try {
            bookingService.delete(id, user.getUsername());
            return ResponseEntity.ok(Map.of("message", "Booking deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}