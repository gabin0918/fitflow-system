package com.fitflow.gym_operations.service;

import com.fitflow.gym_operations.model.Booking;
import com.fitflow.gym_operations.model.GymClass;
import com.fitflow.gym_operations.repository.BookingRepository;
import com.fitflow.gym_operations.repository.GymClassRepository;
import com.fitflow.gym_operations.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final GymClassRepository gymClassRepository;
    private final JwtUtils jwtUtils;

    public Booking createBookingWithToken(String jwt, Long gymClassId) {
        Long userId = jwtUtils.extractUserId(jwt);

        // 1. Sprawdź, czy użytkownik już jest zapisany
        if (bookingRepository.existsByUserIdAndGymClassId(userId, gymClassId)) {
            throw new RuntimeException("Jesteś już zapisany/a na te zajęcia!");
        }

        // 2. Szukaj zajęć
        GymClass gymClass = gymClassRepository.findById(gymClassId)
                .orElseThrow(() -> new RuntimeException("Zajęcia nie istnieją"));

        // 3. Sprawdź pojemność
        long currentBookings = bookingRepository.countByGymClassId(gymClassId);
        if (currentBookings >= gymClass.getCapacity()) {
            throw new RuntimeException("Brak wolnych miejsc!");
        }

        Booking booking = Booking.builder()
                .userId(userId)
                .gymClass(gymClass)
                .build();

        return bookingRepository.save(booking);
    }

    public List<Booking> getMyBookings(String jwt) {
        Long userId = jwtUtils.extractUserId(jwt);
        return bookingRepository.findByUserId(userId);
    }

    // NOWE: Usuwanie rezerwacji
    public void cancelBooking(Long bookingId, String jwt) {
        Long userId = jwtUtils.extractUserId(jwt);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono rezerwacji"));

        if (!booking.getUserId().equals(userId)) {
            throw new RuntimeException("Nie możesz odwołać cudzej rezerwacji!");
        }

        bookingRepository.delete(booking);
    }
}