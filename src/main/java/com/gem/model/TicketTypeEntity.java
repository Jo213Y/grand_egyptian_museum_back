package com.gem.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ticket_types")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "typeId")
public class TicketTypeEntity {

    @Id
    @Column(name = "Type_id")
    private Integer typeId;

    @Column(name = "Ticket_type", length = 50)
    private String ticketType;

    @Column(name = "Price")
    private Double price;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;
}