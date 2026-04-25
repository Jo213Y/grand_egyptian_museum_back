package com.gem.dto;

import lombok.Data;
import java.util.List;

@Data
public class HallUpdateRequest {
    private String       name;
    private String       shortDescription;
    private String       description;
    private String       imageUrl;
    private Integer      maxCapacity;
    private List<String> artifacts;
}
