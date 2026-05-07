package com.trainsystem.controller.api;

import com.trainsystem.model.Booking;
import com.trainsystem.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Validated
public class BookingApiController {

    private final BookingService bookingService;

    public BookingApiController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/schedule/{scheduleId}")
    public List<Booking> getBookingsForSchedule(@PathVariable Long scheduleId) {
        return bookingService.getBookingsForSchedule(scheduleId);
    }

    public static class BookingRequest {
        @NotNull(message = "Schedule ID is required")
        private Long scheduleId;
        
        @NotBlank(message = "Passenger name is required")
        private String passengerName;
        
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;
        
        @Min(value = 1, message = "At least 1 seat must be booked")
        private int seats;

        public Long getScheduleId() { return scheduleId; }
        public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
        public String getPassengerName() { return passengerName; }
        public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public int getSeats() { return seats; }
        public void setSeats(int seats) { this.seats = seats; }
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody @Valid BookingRequest req) {
        Booking booking = bookingService.bookTickets(req.getScheduleId(), req.getPassengerName(), req.getEmail(), req.getSeats());
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

}
