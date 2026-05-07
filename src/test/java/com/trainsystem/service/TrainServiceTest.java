package com.trainsystem.service;

import com.trainsystem.TestEntityFactory;
import com.trainsystem.exception.EntityNotFoundException;
import com.trainsystem.model.Booking;
import com.trainsystem.model.Route;
import com.trainsystem.model.Schedule;
import com.trainsystem.model.Train;
import com.trainsystem.model.enums.TrainStatus;
import com.trainsystem.repository.BookingRepository;
import com.trainsystem.repository.TrainRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainServiceTest {

    @Mock private TrainRepository trainRepo;
    @Mock private BookingRepository bookingRepo;
    @Mock private NotificationService notificationService;

    @InjectMocks private TrainService trainService;

    @Test
    void GetAllTrains_ReturnsList() {
        Train train = TestEntityFactory.train(1L, "IR 1581", 200);
        when(trainRepo.findAll()).thenReturn(List.of(train));

        List<Train> result = trainService.getAllTrains();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("IR 1581");
    }

    @Test
    void GetTrainById_Exists_ReturnsTrain() {
        Train train = TestEntityFactory.train(1L, "IR 1581", 200);
        when(trainRepo.findById(1L)).thenReturn(Optional.of(train));

        Train result = trainService.getTrainById(1L);

        assertThat(result.getName()).isEqualTo("IR 1581");
    }

    @Test
    void GetTrainById_NotFound_ThrowsEntityNotFoundException() {
        when(trainRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainService.getTrainById(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void CreateTrain_SavesAndReturns() {
        when(trainRepo.save(any(Train.class))).thenAnswer(invocation -> {
            Train saved = invocation.getArgument(0);
            return TestEntityFactory.train(1L, saved.getName(), saved.getTotalSeats());
        });

        Train result = trainService.createTrain("IR 2000", 180);

        assertThat(result.getName()).isEqualTo("IR 2000");
        assertThat(result.getTotalSeats()).isEqualTo(180);
    }

    @Test
    void UpdateTrain_ExistingTrain_UpdatesFields() {
        Train existing = TestEntityFactory.train(1L, "IR 1581", 200);
        when(trainRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(trainRepo.save(any(Train.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Train result = trainService.updateTrain(1L, "IR 1581 Updated", 250);

        assertThat(result.getName()).isEqualTo("IR 1581 Updated");
        assertThat(result.getTotalSeats()).isEqualTo(250);
    }

    @Test
    void DeleteTrain_ExistingTrain_Deletes() {
        Train train = TestEntityFactory.train(1L, "IR 1581", 200);
        when(trainRepo.findById(1L)).thenReturn(Optional.of(train));

        trainService.deleteTrain(1L);

        verify(trainRepo).delete(train);
    }

    @Test
    void DeleteTrain_NotFound_ThrowsEntityNotFoundException() {
        when(trainRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainService.deleteTrain(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void ReportDelay_UpdatesStatusAndNotifiesPassengers() {
        Train train = TestEntityFactory.train(1L, "IR 1581", 200);
        Route route = TestEntityFactory.route(1L, "Bucharest - Brasov");
        Schedule schedule = TestEntityFactory.schedule(1L, train, route, LocalDateTime.of(2026, 6, 15, 8, 30));
        Booking booking = TestEntityFactory.booking(1L, schedule, "Andrei", "andrei@test.com", 2);

        when(trainRepo.findById(1L)).thenReturn(Optional.of(train));
        when(bookingRepo.findByTrainId(1L)).thenReturn(List.of(booking));
        when(trainRepo.save(any(Train.class))).thenAnswer(invocation -> invocation.getArgument(0));

        trainService.reportDelay(1L, 30);

        assertThat(train.getStatus()).isEqualTo(TrainStatus.DELAYED);
        assertThat(train.getDelayMinutes()).isEqualTo(30);
        verify(notificationService).sendDelayNotification(booking, 30);
    }

    @Test
    void ReportDelay_NoBookings_UpdatesStatusOnly() {
        Train train = TestEntityFactory.train(1L, "IR 1581", 200);
        when(trainRepo.findById(1L)).thenReturn(Optional.of(train));
        when(bookingRepo.findByTrainId(1L)).thenReturn(Collections.emptyList());
        when(trainRepo.save(any(Train.class))).thenAnswer(invocation -> invocation.getArgument(0));

        trainService.reportDelay(1L, 15);

        assertThat(train.getStatus()).isEqualTo(TrainStatus.DELAYED);
        verify(notificationService, never()).sendDelayNotification(any(), anyInt());
    }
}
