package com.fitflow.gym_operations.repository;

import com.fitflow.gym_operations.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    // NOWE: Policz ile jest rezerwacji na konkretne zajęcia
    long countByGymClassId(Long gymClassId);
}