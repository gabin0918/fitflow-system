package com.fitflow.gym_operations.repository;

import com.fitflow.gym_operations.model.GymClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GymClassRepository extends JpaRepository<GymClass, Long> {
    // Tu na razie nic nie musimy pisać - Spring sam wygeneruje kod do SQL
}