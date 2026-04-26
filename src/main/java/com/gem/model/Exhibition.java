package com.gem.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "exhibitions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Exhibition {

    @Id
    @Column(name = "Exhibition_id")
    private Integer exhibitionId;

    @Column(name = "Exhibition_name", length = 100)
    private String exhibitionName;

    @Column(name = "Exhibition_description", columnDefinition = "TEXT")
    private String exhibitionDescription;

    @Column(name = "Hall_id")
    private Integer hallId;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "Exhibition_id", insertable = false, updatable = false)
    private List<Artifact> artifacts;
}