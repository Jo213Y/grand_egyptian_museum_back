package com.gem.repository;

import com.gem.model.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Integer> {
    List<Exhibition> findByHallId(Integer hallId);
}