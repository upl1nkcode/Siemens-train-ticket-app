package com.trainsystem.controller.api;

import com.trainsystem.model.Train;
import com.trainsystem.service.TrainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/api/trains")
@Validated
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

    public static class TrainRequest {
        @NotBlank(message = "Train name is required")
        private String name;
        
        @Min(value = 1, message = "Train must have at least 1 seat")
        private int totalSeats;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getTotalSeats() { return totalSeats; }
        public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    }

    public static class DelayRequest {
        @Min(value = 1, message = "Delay must be at least 1 minute")
        private int delayMinutes;

        public int getDelayMinutes() { return delayMinutes; }
        public void setDelayMinutes(int delayMinutes) { this.delayMinutes = delayMinutes; }
    }

    @PostMapping
    public ResponseEntity<Train> createTrain(@RequestBody @Valid TrainRequest req) {
        Train train = trainService.createTrain(req.getName(), req.getTotalSeats());
        return new ResponseEntity<>(train, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Train updateTrain(@PathVariable Long id, @RequestBody @Valid TrainRequest req) {
        return trainService.updateTrain(id, req.getName(), req.getTotalSeats());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrain(@PathVariable Long id) {
        trainService.deleteTrain(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/delay")
    public ResponseEntity<Void> reportDelay(@PathVariable Long id, @RequestBody @Valid DelayRequest req) {
        trainService.reportDelay(id, req.getDelayMinutes());
        return ResponseEntity.ok().build();
    }
}
