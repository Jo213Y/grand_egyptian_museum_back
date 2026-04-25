package com.gem.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Maps EXACTLY to: cards
 * Card_id | Card_number | Bank | Holder_name | user_id
 *
 * FIX: SQL schema uses user_id (FK → users), NOT Visitor_id.
 */
@Entity
@Table(name = "cards")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "cardId")
public class Card {

    @Id
    @Column(name = "Card_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cardId;

    @Column(name = "Card_number", length = 16)
    private String cardNumber;

    @Column(name = "Bank", length = 50)
    private String bank;

    @Column(name = "Holder_name", length = 100)
    private String holderName;

    /** FK → users(user_id) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
