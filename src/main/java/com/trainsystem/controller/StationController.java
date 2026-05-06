package com.trainsystem.controller;

import com.trainsystem.service.RouteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/stations")
public class StationController {

    private final RouteService routeService;

    public StationController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public String listStations(Model model) {
        model.addAttribute("stations", routeService.getAllStations());
        return "stations";
    }

    @PostMapping("/add")
    public String addStation(@RequestParam String name, RedirectAttributes redirectAttributes) {
        try {
            routeService.createStation(name);
            redirectAttributes.addFlashAttribute("successMessage", "Station added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/stations";
    }

    @PostMapping("/delete/{id}")
    public String deleteStation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            routeService.deleteStation(id);
            redirectAttributes.addFlashAttribute("successMessage", "Station deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/stations";
    }
}
