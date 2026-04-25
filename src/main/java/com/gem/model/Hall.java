package com.gem.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Maps EXACTLY to: halls
 * Hall_id | Hall_name | Hall_description | Area | image_url
 *
 * FIX: removed @ElementCollection on hall_artifacts — that table doesn't exist in SQL.
 *      image_url already exists in the SQL schema.
 */
@Entity
@Table(name = "halls")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "hallId")
public class Hall {

    @Id
    @Column(name = "Hall_id")
    private Integer hallId;

    @Column(name = "Hall_name", length = 100)
    private String hallName;

    @Column(name = "Hall_description", columnDefinition = "TEXT")
    private String hallDescription;

    @Column(name = "Area")
    private Integer area;

    @Column(name = "image_url", length = 500)
    private String imageUrl;
}
