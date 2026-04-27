package com.gem.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "artifacts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Artifact {

    @Id
    @Column(name = "Artifact_id")
    private Integer artifactId;

    @Column(name = "Art_name", length = 100)
    private String artName;

    @Column(name = "Historical_period", length = 100)
    private String historicalPeriod;

    @Column(name = "Art_description", columnDefinition = "TEXT")
    private String artDescription;

    @Column(name = "Exhibition_id")
    private Integer exhibitionId;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_hidden")
    private Boolean isHidden = false;
}