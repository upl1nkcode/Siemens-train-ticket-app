package com.trainsystem.controller;

import com.trainsystem.service.BookingService;
import com.trainsystem.service.RouteService;
import com.trainsystem.service.ScheduleService;
import com.trainsystem.service.TrainService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/trains")
public class TrainController {

    private final TrainService trainService;
    private final ScheduleService scheduleService;
    private final RouteService routeService;
    private final BookingService bookingService;

    public TrainController(TrainService trainService,
                           ScheduleService scheduleService,
                           RouteService routeService,
                           BookingService bookingService) {
        this.trainService = trainService;
        this.scheduleService = scheduleService;
        this.routeService = routeService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public String listTrains(Model model) {
        model.addAttribute("trains", trainService.getAllTrains());
        model.addAttribute("schedules", scheduleService.getAllSchedules());
        model.addAttribute("routes", routeService.getAllRoutes());
        return "trains";
    }

    @PostMapping("/add")
    public String addTrain(@RequestParam String name,
                           @RequestParam int totalSeats,
                           RedirectAttributes redirectAttributes) {
        try {
            trainService.createTrain(name, totalSeats);
            redirectAttributes.addFlashAttribute("successMessage", "Train added successfully.");
        } catch (ConstraintViolationException e) {
            String msg = e.getConstraintViolations().iterator().next().getMessage();
            redirectAttributes.addFlashAttribute("errorMessage", msg);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/trains";
    }

    @PostMapping("/delete/{id}")
    public String deleteTrain(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            trainService.deleteTrain(id);
            redirectAttributes.addFlashAttribute("successMessage", "Train deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/trains";
    }

    @PostMapping("/schedules/add")
    public String addSchedule(@RequestParam Long trainId,
                              @RequestParam Long routeId,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTime,
                              RedirectAttributes redirectAttributes) {
        try {
            scheduleService.createSchedule(trainId, routeId, departureTime);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/trains";
    }

    @PostMapping("/schedules/delete/{id}")
    public String deleteSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            scheduleService.deleteSchedule(id);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/trains";
    }

    @PostMapping("/delay/{id}")
    public String reportDelay(@PathVariable Long id,
                              @RequestParam int delayMinutes,
                              RedirectAttributes redirectAttributes) {
        try {
            trainService.reportDelay(id, delayMinutes);
            redirectAttributes.addFlashAttribute("successMessage", "Delay recorded. Passengers have been notified.");
        } catch (ConstraintViolationException e) {
            String msg = e.getConstraintViolations().iterator().next().getMessage();
            redirectAttributes.addFlashAttribute("errorMessage", msg);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/trains";
    }

    @GetMapping("/{id}/bookings")
    public String viewBookings(@PathVariable Long id, Model model) {
        model.addAttribute("train", trainService.getTrainById(id));
        model.addAttribute("bookings", bookingService.getBookingsForTrain(id));
        return "train-bookings"; // Fragment or separate page
    }
}
