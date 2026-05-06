package com.trainsystem.service;

import com.trainsystem.exception.EntityNotFoundException;
import com.trainsystem.exception.OverbookingException;
import com.trainsystem.model.Booking;
import com.trainsystem.model.Schedule;
import com.trainsystem.repository.BookingRepository;
import com.trainsystem.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
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
    public Booking bookTickets(Long scheduleId, String passengerName, String email, int seats) {
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
