package com.gem.repository;

import com.gem.model.BookingTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingTicketRepository extends JpaRepository<BookingTicket, Integer> {

    @Query("SELECT t.ticketType.ticketType, SUM(t.quantity) FROM BookingTicket t GROUP BY t.ticketType.ticketType")
    List<Object[]> countByType();

    @Query("SELECT t.ticketType.ticketType, SUM(t.soldPrice * t.quantity) FROM BookingTicket t GROUP BY t.ticketType.ticketType")
    List<Object[]> revenueByType();
}
