package com.fitflow.gym_operations.controller;

import com.fitflow.gym_operations.model.Booking;
import com.fitflow.gym_operations.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gym/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // Pobieranie moich rezerwacji
    @GetMapping("/my")
    public ResponseEntity<List<Booking>> getMyBookings(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        return ResponseEntity.ok(bookingService.getMyBookings(jwt));
    }

    // Zapisywanie na zajęcia
    @PostMapping
    public ResponseEntity<?> bookClass(@RequestHeader("Authorization") String token, @RequestBody Map<String, Long> payload) {
        try {
            String jwt = token.substring(7);
            Booking booking = bookingService.createBookingWithToken(jwt, payload.get("gymClassId"));
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // USUWANIE REZERWACJI - poprawiona linia z @PathVariable("id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable("id") Long id, @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            bookingService.cancelBooking(id, jwt);
            return ResponseEntity.ok("Odwołano rezerwację pomyślnie");
        } catch (Exception e) {
            // Jeśli dostajemy 404, sprawdźmy czy logi Javy coś wypisują
            return ResponseEntity.status(404).body("Nie znaleziono rezerwacji lub brak uprawnień: " + e.getMessage());
        }
    }
}