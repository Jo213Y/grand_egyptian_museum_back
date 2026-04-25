package com.gem.service;

import com.gem.dto.*;
import com.gem.model.*;
import com.gem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository          userRepo;
    private final BookingRepository       bookingRepo;
    private final BookingTicketRepository ticketRepo;
    private final HallRepository          hallRepo;

    public StatisticsResponse getStatistics() {
        long totalUsers    = userRepo.count();
        long totalBookings = bookingRepo.count();

        List<Booking> allBookings = bookingRepo.findAll();
        long confirmed = allBookings.stream()
                .filter(b -> "Confirmed".equalsIgnoreCase(b.getOrderStatus())).count();
        long cancelled = allBookings.stream()
                .filter(b -> "Cancelled".equalsIgnoreCase(b.getOrderStatus())).count();

        double totalRevenue = allBookings.stream()
                .filter(b -> "Confirmed".equalsIgnoreCase(b.getOrderStatus()))
                .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice() : 0)
                .sum();

        Map<String, Long>   ticketsByType = new LinkedHashMap<>();
        Map<String, Double> revenueByType = new LinkedHashMap<>();

        for (Object[] row : ticketRepo.countByType())
            if (row[0] != null) ticketsByType.put(row[0].toString(), ((Number) row[1]).longValue());
        for (Object[] row : ticketRepo.revenueByType())
            if (row[0] != null) revenueByType.put(row[0].toString(), ((Number) row[1]).doubleValue());

        // Hall is @Transient in Booking — bookingsByHall will always be empty
        // To support this properly, hall_id needs to be added to orders table in DB
        Map<String, Long> bookingsByHall = new LinkedHashMap<>();

        StatisticsResponse res = new StatisticsResponse();
        res.setTotalUsers(totalUsers);
        res.setTotalBookings(totalBookings);
        res.setConfirmedBookings(confirmed);
        res.setCancelledBookings(cancelled);
        res.setTotalRevenue(totalRevenue);
        res.setTicketsByType(ticketsByType);
        res.setRevenueByType(revenueByType);
        res.setBookingsByHall(bookingsByHall);
        return res;
    }

    @Transactional
    public HallResponse updateHall(Long id, HallUpdateRequest req) {
        Hall hall = hallRepo.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Hall not found: " + id));
        if (req.getName()        != null) hall.setHallName(req.getName());
        if (req.getDescription() != null) hall.setHallDescription(req.getDescription());
        if (req.getImageUrl()    != null) hall.setImageUrl(req.getImageUrl());
        if (req.getMaxCapacity() != null) hall.setArea(req.getMaxCapacity());
        // artifacts removed — not stored in halls table
        return toHallDto(hallRepo.save(hall));
    }

    public List<HallResponse> getAllHalls() {
        return hallRepo.findAll().stream().map(this::toHallDto).collect(Collectors.toList());
    }

    private HallResponse toHallDto(Hall h) {
        HallResponse res = new HallResponse();
        res.setId(h.getHallId() != null ? h.getHallId().longValue() : null);
        res.setName(h.getHallName());
        res.setShortDescription(h.getHallDescription());
        res.setDescription(h.getHallDescription());
        res.setImageUrl(h.getImageUrl());
        res.setMaxCapacity(h.getArea());
        res.setArtifacts(new ArrayList<>()); // artifacts field removed from Hall entity
        return res;
    }
}