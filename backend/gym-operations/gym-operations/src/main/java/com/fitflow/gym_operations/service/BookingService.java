package com.fitflow.gym_operations.service;

import com.fitflow.gym_operations.dto.BookingRequestDTO;
import com.fitflow.gym_operations.model.Booking;
import com.fitflow.gym_operations.model.GymClass;
import com.fitflow.gym_operations.repository.BookingRepository;
import com.fitflow.gym_operations.repository.GymClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final GymClassRepository gymClassRepository;

    public Booking createBooking(BookingRequestDTO request) {
        // 1. Znajdź zajęcia w bazie
        GymClass gymClass = gymClassRepository.findById(request.getGymClassId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono takich zajęć!"));

        // 2. Sprawdź, czy są wolne miejsca
        long currentBookings = bookingRepository.countByGymClassId(gymClass.getId());
        if (currentBookings >= gymClass.getCapacity()) {
            throw new RuntimeException("Brak wolnych miejsc na te zajęcia!");
        }

        // 3. Stwórz rezerwację
        Booking booking = Booking.builder()
                .userId(request.getUserId())
                .gymClass(gymClass)
                .build();

        return bookingRepository.save(booking);
    }
}