package com.gem.repository;

import com.gem.model.Booking;
import com.gem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByUserOrderByOrderDateDesc(User user);

    @Query("SELECT b FROM Booking b WHERE b.orderStatus = :status")
    List<Booking> findByStatus(@Param("status") String status);

    /** Returns userId → total ticket quantity for all confirmed/completed bookings */
    @Query(value = """
        SELECT e.user_id, COALESCE(SUM(e.quantity), 0)
        FROM orders o
        JOIN orders_tickets ot ON ot.Order_id = o.Order_id
        JOIN etickets e        ON e.Ticket_id  = ot.Ticket_id
        WHERE o.Order_status IN ('Confirmed', 'COMPLETED', 'confirmed', 'completed')
          AND e.user_id IS NOT NULL
        GROUP BY e.user_id
    """, nativeQuery = true)
    List<Object[]> sumTicketQuantityPerUser();
}