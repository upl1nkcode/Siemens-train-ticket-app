package com.trainsystem.service;

import com.trainsystem.TestEntityFactory;
import com.trainsystem.exception.EntityNotFoundException;
import com.trainsystem.exception.OverbookingException;
import com.trainsystem.model.*;
import com.trainsystem.repository.BookingRepository;
import com.trainsystem.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepo;
    @Mock private ScheduleRepository scheduleRepo;
    @Mock private NotificationService notificationService;

    @InjectMocks private BookingService bookingService;

    private Train train;
    private Route route;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        train = TestEntityFactory.train(1L, "IR 1581", 100);
        route = TestEntityFactory.route(1L, "Bucharest - Brasov");
        schedule = TestEntityFactory.schedule(1L, train, route, LocalDateTime.of(2026, 6, 15, 8, 30));
    }

    @Test
    void BookTickets_ValidRequest_ReturnsBookingAndSendsEmail() {
        when(scheduleRepo.findById(1L)).thenReturn(Optional.of(schedule));
        when(bookingRepo.sumSeatsBookedByScheduleId(1L)).thenReturn(50);
        when(bookingRepo.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking saved = invocation.getArgument(0);
            return TestEntityFactory.booking(10L, saved.getSchedule(),
                    saved.getPassengerName(), saved.getPassengerEmail(), saved.getSeatsBooked());
        });

        Booking result = bookingService.bookTickets(1L, "Andrei", "andrei@test.com", 2);

        assertThat(result.getPassengerName()).isEqualTo("Andrei");
        assertThat(result.getSeatsBooked()).isEqualTo(2);

        verify(notificationService).sendBookingConfirmation(any(Booking.class));
    }

    @Test
    void BookTickets_InsufficientSeats_ThrowsOverbookingException() {
        when(scheduleRepo.findById(1L)).thenReturn(Optional.of(schedule));
        when(bookingRepo.sumSeatsBookedByScheduleId(1L)).thenReturn(99);

        assertThatThrownBy(() -> bookingService.bookTickets(1L, "Andrei", "andrei@test.com", 5))
                .isInstanceOf(OverbookingException.class);

        verify(bookingRepo, never()).save(any());
        verify(notificationService, never()).sendBookingConfirmation(any());
    }

    @Test
    void BookTickets_ExactlyFillsCapacity_Succeeds() {
        when(scheduleRepo.findById(1L)).thenReturn(Optional.of(schedule));
        when(bookingRepo.sumSeatsBookedByScheduleId(1L)).thenReturn(98);
        when(bookingRepo.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.bookTickets(1L, "Andrei", "andrei@test.com", 2);

        assertThat(result.getSeatsBooked()).isEqualTo(2);
    }

    @Test
    void BookTickets_ScheduleNotFound_ThrowsEntityNotFoundException() {
        when(scheduleRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.bookTickets(999L, "Andrei", "andrei@test.com", 1))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void GetAvailableSeats_PartiallyBooked_ReturnsRemainder() {
        when(scheduleRepo.findById(1L)).thenReturn(Optional.of(schedule));
        when(bookingRepo.sumSeatsBookedByScheduleId(1L)).thenReturn(30);

        int available = bookingService.getAvailableSeats(1L);

        assertThat(available).isEqualTo(70);
    }

    @Test
    void GetAvailableSeats_ScheduleNotFound_ThrowsEntityNotFoundException() {
        when(scheduleRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getAvailableSeats(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void GetBookingsForSchedule_ReturnsListFromRepository() {
        Booking booking = TestEntityFactory.booking(1L, schedule, "Andrei", "a@test.com", 2);
        when(bookingRepo.findByScheduleId(1L)).thenReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingsForSchedule(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPassengerName()).isEqualTo("Andrei");
    }

    @Test
    void GetBookingsForTrain_ReturnsListFromRepository() {
        Booking booking = TestEntityFactory.booking(1L, schedule, "Maria", "m@test.com", 1);
        when(bookingRepo.findByTrainId(1L)).thenReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingsForTrain(1L);

        assertThat(result).hasSize(1);
    }
}
