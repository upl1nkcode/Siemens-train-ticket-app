package com.trainsystem.service;

import com.trainsystem.TestEntityFactory;
import com.trainsystem.exception.EntityNotFoundException;
import com.trainsystem.model.Route;
import com.trainsystem.model.Schedule;
import com.trainsystem.model.Train;
import com.trainsystem.repository.RouteRepository;
import com.trainsystem.repository.ScheduleRepository;
import com.trainsystem.repository.TrainRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class ScheduleServiceTest {

    @Mock private ScheduleRepository scheduleRepo;
    @Mock private TrainRepository trainRepo;
    @Mock private RouteRepository routeRepo;

    @InjectMocks private ScheduleService scheduleService;

    @Test
    void GetAllSchedules_ReturnsList() {
        Train train = TestEntityFactory.train(1L, "IR 1581", 200);
        Route route = TestEntityFactory.route(1L, "Bucharest - Brasov");
        Schedule schedule = TestEntityFactory.schedule(1L, train, route,
                LocalDateTime.of(2026, 6, 15, 8, 30));
        when(scheduleRepo.findAll()).thenReturn(List.of(schedule));

        assertThat(scheduleService.getAllSchedules()).hasSize(1);
    }

    @Test
    void GetScheduleById_Exists_ReturnsSchedule() {
        Train train = TestEntityFactory.train(1L, "IR 1581", 200);
        Route route = TestEntityFactory.route(1L, "Bucharest - Brasov");
        Schedule schedule = TestEntityFactory.schedule(1L, train, route,
                LocalDateTime.of(2026, 6, 15, 8, 30));
        when(scheduleRepo.findById(1L)).thenReturn(Optional.of(schedule));

        assertThat(scheduleService.getScheduleById(1L).getTrain().getName())
                .isEqualTo("IR 1581");
    }

    @Test
    void GetScheduleById_NotFound_Throws() {
        when(scheduleRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> scheduleService.getScheduleById(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void CreateSchedule_ValidIds_SavesSchedule() {
        Train train = TestEntityFactory.train(1L, "IR 1581", 200);
        Route route = TestEntityFactory.route(1L, "Bucharest - Brasov");
        when(trainRepo.findById(1L)).thenReturn(Optional.of(train));
        when(routeRepo.findById(1L)).thenReturn(Optional.of(route));
        when(scheduleRepo.save(any(Schedule.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime dep = LocalDateTime.of(2026, 6, 15, 8, 30);
        Schedule result = scheduleService.createSchedule(1L, 1L, dep);

        assertThat(result.getTrain()).isEqualTo(train);
        assertThat(result.getRoute()).isEqualTo(route);
        assertThat(result.getDepartureTime()).isEqualTo(dep);
    }

    @Test
    void CreateSchedule_TrainNotFound_Throws() {
        when(trainRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                scheduleService.createSchedule(999L, 1L, LocalDateTime.now()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void CreateSchedule_RouteNotFound_Throws() {
        Train train = TestEntityFactory.train(1L, "IR 1581", 200);
        when(trainRepo.findById(1L)).thenReturn(Optional.of(train));
        when(routeRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                scheduleService.createSchedule(1L, 999L, LocalDateTime.now()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void DeleteSchedule_Exists_Deletes() {
        Train train = TestEntityFactory.train(1L, "IR 1581", 200);
        Route route = TestEntityFactory.route(1L, "Bucharest - Brasov");
        Schedule schedule = TestEntityFactory.schedule(1L, train, route,
                LocalDateTime.of(2026, 6, 15, 8, 30));
        when(scheduleRepo.findById(1L)).thenReturn(Optional.of(schedule));

        scheduleService.deleteSchedule(1L);

        verify(scheduleRepo).delete(schedule);
    }

    @Test
    void GetSchedulesForTrain_ReturnsList() {
        Train train = TestEntityFactory.train(1L, "IR 1581", 200);
        Route route = TestEntityFactory.route(1L, "Bucharest - Brasov");
        Schedule schedule = TestEntityFactory.schedule(1L, train, route,
                LocalDateTime.of(2026, 6, 15, 8, 30));
        when(scheduleRepo.findByTrainId(1L)).thenReturn(List.of(schedule));

        assertThat(scheduleService.getSchedulesForTrain(1L)).hasSize(1);
    }
}
