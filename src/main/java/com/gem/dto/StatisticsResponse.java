package com.gem.dto;

import lombok.Data;
import java.util.Map;

@Data
public class StatisticsResponse {
    private long   totalUsers;
    private long   totalBookings;
    private long   confirmedBookings;
    private long   cancelledBookings;
    private double totalRevenue;
    private long   totalTickets;
    private Map<String, Long>   ticketsByType;
    private Map<String, Long>   bookingsByHall;
    private Map<String, Double> revenueByType;
}