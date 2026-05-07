package com.trainsystem.controller.api;

import com.trainsystem.model.Train;
import com.trainsystem.service.TrainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trains")
public class TrainApiController {

    private final TrainService trainService;

    public TrainApiController(TrainService trainService) {
        this.trainService = trainService;
    }

    @GetMapping
    public List<Train> getAllTrains() {
        return trainService.getAllTrains();
    }

    @GetMapping("/{id}")
    public Train getTrain(@PathVariable Long id) {
        return trainService.getTrainById(id);
    }

    @PostMapping
    public ResponseEntity<Train> createTrain(@RequestParam String name, @RequestParam int totalSeats) {
        Train train = trainService.createTrain(name, totalSeats);
        return new ResponseEntity<>(train, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Train updateTrain(@PathVariable Long id, @RequestParam String name, @RequestParam int totalSeats) {
        return trainService.updateTrain(id, name, totalSeats);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrain(@PathVariable Long id) {
        trainService.deleteTrain(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/delay")
    public ResponseEntity<Void> reportDelay(@PathVariable Long id, @RequestParam int delayMinutes) {
        trainService.reportDelay(id, delayMinutes);
        return ResponseEntity.ok().build();
    }
}
