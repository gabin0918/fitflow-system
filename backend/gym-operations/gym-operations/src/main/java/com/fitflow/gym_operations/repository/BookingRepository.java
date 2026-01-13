package com.fitflow.gym_operations.repository;

import com.fitflow.gym_operations.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    long countByGymClassId(Long gymClassId);

    // To sprawia, że nie zapiszesz się 2 razy na to samo
    boolean existsByUserIdAndGymClassId(Long userId, Long gymClassId);
}