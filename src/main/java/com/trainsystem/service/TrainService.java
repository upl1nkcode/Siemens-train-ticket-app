package com.trainsystem.service;

import com.trainsystem.exception.EntityNotFoundException;
import com.trainsystem.model.Booking;
import com.trainsystem.model.Train;
import com.trainsystem.model.enums.TrainStatus;
import com.trainsystem.repository.BookingRepository;
import com.trainsystem.repository.TrainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class TrainService {

    private final TrainRepository trainRepository;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

    public TrainService(TrainRepository trainRepository,
                        BookingRepository bookingRepository,
                        NotificationService notificationService) {
        this.trainRepository = trainRepository;
        this.bookingRepository = bookingRepository;
        this.notificationService = notificationService;
    }

    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }

    public Train getTrainById(Long id) {
        return trainRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new EntityNotFoundException("Train", id));
    }

    @Transactional
    public Train createTrain(String name, int totalSeats) {
        Train train = new Train(name, totalSeats);
        return trainRepository.save(train);
    }

    @Transactional
    public Train updateTrain(Long id, String name, int totalSeats) {
        Train train = getTrainById(id);
        train.setName(name);
        train.setTotalSeats(totalSeats);
        return trainRepository.save(train);
    }

    @Transactional
    public void deleteTrain(Long id) {
        Train train = getTrainById(id);
        trainRepository.delete(Objects.requireNonNull(train));
    }

    @Transactional
    public void reportDelay(Long trainId, int delayMinutes) {
        Train train = getTrainById(trainId);
        train.setStatus(TrainStatus.DELAYED);
        train.setDelayMinutes(delayMinutes);
        trainRepository.save(train);

        List<Booking> affectedBookings = bookingRepository.findByTrainId(trainId);
        for (Booking booking : affectedBookings) {
            notificationService.sendDelayNotification(booking, delayMinutes);
        }
    }
}
