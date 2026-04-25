package com.gem.controller;

import com.gem.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin
public class TicketController {

    private final TicketService ticketService;

    /** GET /api/tickets/types — Adult $25, Child $12, Senior $20 */
    @GetMapping("/types")
    public ResponseEntity<?> getTypes() {
        return ResponseEntity.ok(ticketService.getTypes());
    }
}
