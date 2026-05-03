package com.gem.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(exclude = {"user", "tickets", "payment"})
@EqualsAndHashCode(of = "orderId")
public class Booking {

    @Id
    @Column(name = "Order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @Column(name = "Order_date")
    @Builder.Default
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "Order_status", length = 20)
    @Builder.Default
    private String orderStatus = "Confirmed";

    /** FK → users(user_id) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /** FK → payments(Transaction_id) */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "Transaction_id")
    private Payment payment;

    @Column(name = "visit_date")
    private java.time.LocalDate visitDate;

    @Column(name = "total_price")
    private Double totalPrice;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
        name = "orders_tickets",
        joinColumns        = @JoinColumn(name = "Order_id"),
        inverseJoinColumns = @JoinColumn(name = "Ticket_id")
    )
    @Builder.Default
    private List<BookingTicket> tickets = new ArrayList<>();

    /**
     * Hall is NOT a column in orders — kept transient for UI convenience.
     * Populate manually in service layer when needed.
     */
    @Transient
    private Hall hall;

    // ── helpers ───────────────────────────────────────────────────────────────

    public Long getId()               { return orderId != null ? orderId.longValue() : null; }
    public LocalDateTime getCreatedAt(){ return createdAt; }

    public enum BookingStatus { PENDING, CONFIRMED, CANCELLED }
}
