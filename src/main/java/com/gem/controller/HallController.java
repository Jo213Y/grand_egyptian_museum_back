package com.gem.controller;

import com.gem.service.HallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/halls")
@RequiredArgsConstructor
@CrossOrigin
public class HallController {

    private final HallService hallService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(hallService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(hallService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
