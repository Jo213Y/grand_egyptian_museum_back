package com.gem.dto;

import lombok.*;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminStatsResponse {
    private long totalUsers;
    private long totalBookings;
    private long confirmedBookings;
    private long cancelledBookings;
    private double totalRevenue;
    private long adultTickets;
    private long childTickets;
    private long seniorTickets;
    private long totalTickets;
    private Map<String, Long> bookingsPerHall;
}
