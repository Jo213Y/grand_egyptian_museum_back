package com.gem.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "etickets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(exclude = "booking")
@EqualsAndHashCode(of = "ticketId")
public class BookingTicket {

    @Id
    @Column(name = "Ticket_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ticketId;

    @Column(name = "Qr_code", length = 255)
    private String qrCode;

    @Column(name = "Sold_price")
    private Double soldPrice;

    @Column(name = "Date")
    private java.time.LocalDate visitingDate;

    @Column(name = "Validation_status")
    @Builder.Default
    private Boolean validationStatus = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Type_id")
    private TicketTypeEntity ticketType;

    @Column(name = "quantity")
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "user_id")
    private Long userId;

    @ManyToMany(mappedBy = "tickets", fetch = FetchType.LAZY)
    private java.util.List<Booking> booking;
}