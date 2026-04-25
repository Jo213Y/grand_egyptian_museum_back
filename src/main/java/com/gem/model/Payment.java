package com.gem.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "transactionId")
public class Payment {

    @Id
    @Column(name = "Transaction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    @Column(name = "Amount")
    private Double amount;

    @Column(name = "Payment_status", length = 20)
    @Builder.Default
    private String paymentStatus = "Completed";

    @Column(name = "Card_id")
    private Integer cardId;

    @Column(name = "name_on_card", length = 100)
    private String nameOnCard;

    @Column(name = "card_last_four", length = 4)
    private String cardLastFour;

    @Column(name = "expiration_date", length = 7)
    private String expirationDate;

    public Long getId() { return transactionId != null ? transactionId.longValue() : null; }
}