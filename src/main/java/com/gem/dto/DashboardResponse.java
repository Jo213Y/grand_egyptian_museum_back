package com.gem.dto;

import lombok.*;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardResponse {
    private long totalUsers;
    private long totalBookings;
    private long confirmedBookings;
    private long cancelledBookings;
    private long totalTickets;
    private Map<String, Long> ticketsByType;   // ADULT, CHILD, SENIOR → count
    private double totalRevenue;
}
