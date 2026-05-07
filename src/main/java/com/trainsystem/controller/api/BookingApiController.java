package com.trainsystem.controller.api;

import com.trainsystem.model.Booking;
import com.trainsystem.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingApiController {

    private final BookingService bookingService;

    public BookingApiController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/schedule/{scheduleId}")
    public List<Booking> getBookingsForSchedule(@PathVariable Long scheduleId) {
        return bookingService.getBookingsForSchedule(scheduleId);
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestParam Long scheduleId,
            @RequestParam String passengerName,
            @RequestParam String email,
            @RequestParam int seats) {
        Booking booking = bookingService.bookTickets(scheduleId, passengerName, email, seats);
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

}
