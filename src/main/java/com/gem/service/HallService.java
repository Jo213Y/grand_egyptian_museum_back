package com.gem.service;

import com.gem.dto.HallResponse;
import com.gem.model.Hall;
import com.gem.repository.HallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HallService {

    private final HallRepository hallRepo;

    public List<HallResponse> getAll() {
        List<HallResponse> result = new ArrayList<>();
        for (Hall h : hallRepo.findAll()) result.add(toDto(h));
        return result;
    }

    public HallResponse getById(Integer id) {
        Hall h = hallRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Hall not found: " + id));
        return toDto(h);
    }

    private HallResponse toDto(Hall h) {
        HallResponse res = new HallResponse();
        res.setId(h.getHallId() != null ? h.getHallId().longValue() : null);
        res.setName(h.getHallName());
        res.setShortDescription(h.getHallDescription() != null && h.getHallDescription().length() > 80
                ? h.getHallDescription().substring(0, 80) + "..."
                : h.getHallDescription());
        res.setDescription(h.getHallDescription());
        res.setImageUrl(h.getImageUrl() != null ? h.getImageUrl()
                : "https://upload.wikimedia.org/wikipedia/commons/thumb/a/af/All_Gizah_Pyramids.jpg/640px-All_Gizah_Pyramids.jpg");
        res.setMaxCapacity(h.getArea());
        res.setArtifacts(new ArrayList<>()); // artifacts field removed from Hall entity
        return res;
    }
}