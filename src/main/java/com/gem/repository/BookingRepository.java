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
}
