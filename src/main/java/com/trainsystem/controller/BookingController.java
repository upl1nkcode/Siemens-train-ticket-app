package com.trainsystem.controller;

import com.trainsystem.exception.OverbookingException;
import com.trainsystem.model.Booking;
import com.trainsystem.service.BookingService;
import com.trainsystem.service.ScheduleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.ConstraintViolationException;

@Controller
public class BookingController {

    private final BookingService bookingService;
    private final ScheduleService scheduleService;

    public BookingController(BookingService bookingService, ScheduleService scheduleService) {
        this.bookingService = bookingService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/book")
    public String showBookForm(Model model) {
        model.addAttribute("schedules", scheduleService.getAllSchedules());
        return "book";
    }

    @PostMapping("/book")
    public String bookTickets(@RequestParam Long scheduleId,
                              @RequestParam String passengerName,
                              @RequestParam String email,
                              @RequestParam int seats,
                              RedirectAttributes redirectAttributes) {
        try {
            Booking booking = bookingService.bookTickets(scheduleId, passengerName, email, seats);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Booking #" + booking.getId() + " confirmed! A confirmation email has been sent to " + email);
        } catch (OverbookingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (ConstraintViolationException e) {
            String msg = e.getConstraintViolations().iterator().next().getMessage();
            redirectAttributes.addFlashAttribute("errorMessage", msg);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/book";
    }
}
