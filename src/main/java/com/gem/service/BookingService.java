package com.gem.service;

import com.gem.dto.BookingRequest;
import com.gem.dto.BookingResponse;
import com.gem.model.*;
import com.gem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository    bookingRepo;
    private final UserRepository       userRepo;
    private final HallRepository       hallRepo;
    private final TicketTypeRepository ticketTypeRepo;

    @Transactional
    public BookingResponse create(BookingRequest req, String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate visitDate = parseDate(req.getVisitDate());

        // ── Build tickets ────────────────────────────────────────────────────
        List<BookingTicket> ticketEntities = new ArrayList<>();
        double total = 0.0;

        if (req.getTickets() != null) {
            for (BookingRequest.TicketItem item : req.getTickets()) {
                if (item.getQuantity() == null || item.getQuantity() <= 0) continue;

                TicketTypeEntity typeEntity = resolveTicketType(item.getType());
                double price = item.getPrice() != null ? item.getPrice()
                        : (typeEntity != null ? typeEntity.getPrice() : 120.0);

                for (int i = 0; i < item.getQuantity(); i++) {
                    BookingTicket ticket = new BookingTicket();
                    ticket.setQrCode("QR_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                    ticket.setSoldPrice(price);
                    ticket.setVisitingDate(visitDate);   // maps to column "Date"
                    ticket.setValidationStatus(false);
                    ticket.setUserId(user.getId());      // maps to column "user_id"
                    ticket.setTicketType(typeEntity);
                    ticket.setQuantity(1);
                    ticketEntities.add(ticket);
                }
                total += price * item.getQuantity();
            }
        }

        // ── Build Payment ────────────────────────────────────────────────────
        Payment payment = null;
        if (req.getPayment() != null) {
            String cardNum = req.getPayment().getCardNumber();
            String last4   = (cardNum != null && cardNum.length() >= 4)
                    ? cardNum.substring(cardNum.length() - 4) : "****";
            payment = new Payment();
            payment.setAmount(total);
            payment.setPaymentStatus("Completed");
            payment.setNameOnCard(req.getPayment().getNameOnCard());
            payment.setCardLastFour(last4);
            payment.setExpirationDate(req.getPayment().getExpirationDate());
        }

        // ── Build Booking (Order) ────────────────────────────────────────────
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setOrderDate(LocalDateTime.now());
        booking.setCreatedAt(LocalDateTime.now());
        booking.setOrderStatus("Confirmed");
        booking.setVisitDate(visitDate);
        booking.setTotalPrice(total);
        booking.setPayment(payment);
        booking.setTickets(ticketEntities);
        // Note: Hall is not persisted (no Hall_id column in orders table)
        // Populate hall on read via hallId from request if needed for response
        if (req.getHallId() != null) {
            hallRepo.findById(req.getHallId().intValue())
                    .ifPresent(booking::setHall);
        }

        bookingRepo.save(booking);
        return toDto(booking);
    }

    public List<BookingResponse> getMyBookings(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bookingRepo.findByUserOrderByOrderDateDesc(user)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public BookingResponse getById(Long id, String email) {
        Booking booking = bookingRepo.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (!booking.getUser().getEmail().equals(email))
            throw new RuntimeException("Access denied");
        return toDto(booking);
    }

    @Transactional
    public BookingResponse cancel(Long id, String email) {
        Booking booking = bookingRepo.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (!booking.getUser().getEmail().equals(email))
            throw new RuntimeException("Access denied");
        booking.setOrderStatus("Cancelled");
        return toDto(bookingRepo.save(booking));
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private TicketTypeEntity resolveTicketType(String type) {
        if (type == null) return ticketTypeRepo.findAll().stream().findFirst().orElse(null);
        // Try exact match first (case-insensitive) — works with "Egyptian Adult", "VIP Ticket" etc.
        return ticketTypeRepo.findByTicketTypeIgnoreCase(type)
                .orElseGet(() ->
                        // Fallback: partial match — if type="adult" match "Egyptian Adult"
                        ticketTypeRepo.findAll().stream()
                                .filter(t -> t.getTicketType() != null &&
                                        t.getTicketType().toLowerCase().contains(type.toLowerCase()))
                                .findFirst()
                                .orElse(null)
                );
    }

    private LocalDate parseDate(String s) {
        if (s == null) return LocalDate.now();
        try { return LocalDate.parse(s, DateTimeFormatter.ofPattern("d/M/yyyy")); }
        catch (Exception e1) {
            try { return LocalDate.parse(s); }
            catch (Exception e2) { return LocalDate.now(); }
        }
    }

    private BookingResponse toDto(Booking b) {
        List<BookingResponse.TicketDetail> details = new ArrayList<>();
        if (b.getTickets() != null) {
            for (BookingTicket t : b.getTickets()) {
                BookingResponse.TicketDetail d = new BookingResponse.TicketDetail();
                d.setTicketType(t.getTicketType() != null ? t.getTicketType().getTicketType() : "Adult");
                d.setQuantity(t.getQuantity() != null ? t.getQuantity() : 1);
                d.setUnitPrice(t.getSoldPrice() != null ? t.getSoldPrice() : 0.0);
                d.setSubtotal(d.getUnitPrice() * d.getQuantity());
                details.add(d);
            }
        }
        BookingResponse res = new BookingResponse();
        res.setId(b.getOrderId() != null ? b.getOrderId().longValue() : null);
        res.setVisitDate(b.getVisitDate() != null ? b.getVisitDate().toString() : "");
        res.setTotalPrice(b.getTotalPrice() != null ? b.getTotalPrice() : 0.0);
        res.setStatus(b.getOrderStatus() != null ? b.getOrderStatus() : "Confirmed");
        res.setHallName(b.getHall() != null ? b.getHall().getHallName() : null);
        res.setTickets(details);
        return res;
    }
}