package com.gem.repository;

import com.gem.model.TicketTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketTypeEntity, Integer> {
    Optional<TicketTypeEntity> findByTicketTypeIgnoreCase(String ticketType);
}
