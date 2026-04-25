package com.gem.service;

import com.gem.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketTypeRepository ticketTypeRepo;

    public List<Map<String, Object>> getTypes() {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            ticketTypeRepo.findAll().forEach(tt -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id",       tt.getTypeId());
                m.put("type",     mapTypeKey(tt.getTicketType()));
                m.put("label",    tt.getTicketType());
                m.put("ageRange", getAgeRange(tt.getTicketType()));
                m.put("price",    tt.getPrice() != null ? tt.getPrice() : 0.0);
                result.add(m);
            });
            if (!result.isEmpty()) return result;
        } catch (Exception ignored) {}

        // Fallback - أسعار من ticket_types في museum_project
        return List.of(
            Map.of("type","adult",  "label","Adult",          "ageRange","Age (+18)",            "price", 120.0),
            Map.of("type","child",  "label","Child",          "ageRange","Age (3-17)",            "price", 40.0),
            Map.of("type","senior", "label","Senior",         "ageRange","Age (+65)",             "price", 80.0),
            Map.of("type","student","label","Student",        "ageRange","With Student ID",       "price", 60.0),
            Map.of("type","family", "label","Family (2A+2C)", "ageRange","2 Adults + 2 Children", "price", 300.0),
            Map.of("type","vip",    "label","VIP",            "ageRange","Full Access",           "price", 500.0)
        );
    }

    private String mapTypeKey(String type) {
        if (type == null) return "adult";
        return switch (type.toLowerCase()) {
            case "child"            -> "child";
            case "senior"           -> "senior";
            case "student"          -> "student";
            case "family (2a+2c)"   -> "family";
            case "group (10+)"      -> "group";
            case "special tour"     -> "special";
            case "educational"      -> "educational";
            case "vip"              -> "vip";
            case "member"           -> "member";
            default                 -> "adult";
        };
    }

    private String getAgeRange(String type) {
        if (type == null) return "";
        return switch (type.toLowerCase()) {
            case "adult"            -> "Age (+18)";
            case "child"            -> "Age (3-17)";
            case "student"          -> "With Student ID";
            case "senior"           -> "Age (+65)";
            case "family (2a+2c)"   -> "2 Adults + 2 Children";
            case "member"           -> "Annual Membership";
            case "group (10+)"      -> "Groups of 10+";
            case "vip"              -> "Full VIP Access";
            case "special tour"     -> "Guided Tour";
            case "educational"      -> "School Groups";
            default                 -> "";
        };
    }
}
