package com.trainsystem.service;

import com.trainsystem.exception.EntityNotFoundException;
import com.trainsystem.exception.OverbookingException;
import com.trainsystem.model.Booking;
import com.trainsystem.model.Schedule;
import com.trainsystem.repository.BookingRepository;
import com.trainsystem.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

@Service
@Validated
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;
    private final NotificationService notificationService;

    public BookingService(BookingRepository bookingRepository,
                          ScheduleRepository scheduleRepository,
                          NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.scheduleRepository = scheduleRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Booking bookTickets(@NotNull(message = "Schedule ID is required") Long scheduleId, 
                               @NotBlank(message = "Passenger name is required") String passengerName, 
                               @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email, 
                               @Min(value = 1, message = "At least 1 seat must be booked") int seats) {
        Schedule schedule = scheduleRepository.findById(Objects.requireNonNull(scheduleId))
                .orElseThrow(() -> new EntityNotFoundException("Schedule", scheduleId));

        int bookedSeats = bookingRepository.sumSeatsBookedByScheduleId(scheduleId);
        int available = schedule.getTrain().getTotalSeats() - bookedSeats;

        if (seats > available) {
            throw new OverbookingException(seats, available);
        }

        Booking booking = new Booking(schedule, passengerName, email, seats);
        booking = bookingRepository.save(booking);

        notificationService.sendBookingConfirmation(booking);

        return booking;
    }

    public int getAvailableSeats(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(Objects.requireNonNull(scheduleId))
                .orElseThrow(() -> new EntityNotFoundException("Schedule", scheduleId));

        int bookedSeats = bookingRepository.sumSeatsBookedByScheduleId(scheduleId);
        return schedule.getTrain().getTotalSeats() - bookedSeats;
    }

    public List<Booking> getBookingsForSchedule(Long scheduleId) {
        return bookingRepository.findByScheduleId(scheduleId);
    }

    public List<Booking> getBookingsForTrain(Long trainId) {
        return bookingRepository.findByTrainId(trainId);
    }
}
