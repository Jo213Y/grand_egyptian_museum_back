package com.gem.controller;

import com.gem.dto.HallResponse;
import com.gem.service.HallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gem.repository.ExhibitionRepository;
import java.util.*;

@RestController
@RequestMapping("/api/halls")
@RequiredArgsConstructor
public class HallController {

    private final HallService          hallService;
    private final ExhibitionRepository exhibitionRepo;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(hallService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Integer id) {
        return ResponseEntity.ok(hallService.getById(id));
    }

    /** GET /api/halls/{id}/exhibitions — returns exhibitions + artifacts for a hall */
    @GetMapping("/{id}/exhibitions")
    public ResponseEntity<?> getExhibitions(@PathVariable Integer id,
                                            @RequestParam(required = false, defaultValue = "false") boolean showHidden) {
        List<Map<String, Object>> result = new ArrayList<>();

        exhibitionRepo.findByHallId(id).forEach(ex -> {
            Map<String, Object> exMap = new LinkedHashMap<>();
            exMap.put("Exhibition_id",          ex.getExhibitionId());
            exMap.put("Exhibition_name",         ex.getExhibitionName());
            exMap.put("Exhibition_description",  ex.getExhibitionDescription());
            exMap.put("image_url",               ex.getImageUrl());

            List<Map<String, Object>> arts = new ArrayList<>();
            if (ex.getArtifacts() != null) {
                ex.getArtifacts().stream()
                        .filter(a -> showHidden || !Boolean.TRUE.equals(a.getIsHidden()))
                        .forEach(a -> {
                            Map<String, Object> aMap = new LinkedHashMap<>();
                            aMap.put("Artifact_id",       a.getArtifactId());
                            aMap.put("Art_name",           a.getArtName());
                            aMap.put("Historical_period",  a.getHistoricalPeriod());
                            aMap.put("Art_description",    a.getArtDescription());
                            aMap.put("image_url",          a.getImageUrl());
                            aMap.put("isHidden",           Boolean.TRUE.equals(a.getIsHidden()));
                            arts.add(aMap);
                        });
            }
            exMap.put("artifacts", arts);
            result.add(exMap);
        });

        return ResponseEntity.ok(result);
    }
}