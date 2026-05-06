package com.trainsystem.controller;

import com.trainsystem.service.RouteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public String listRoutes(Model model) {
        model.addAttribute("routes", routeService.getAllRoutes());
        model.addAttribute("stations", routeService.getAllStations());
        return "routes";
    }

    @PostMapping("/add")
    public String addRoute(@RequestParam String name, 
                           @RequestParam List<Long> stationIds, 
                           RedirectAttributes redirectAttributes) {
        if (stationIds == null || stationIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "At least one station is required.");
            return "redirect:/admin/routes";
        }
        
        try {
            routeService.createRoute(name, stationIds);
            redirectAttributes.addFlashAttribute("successMessage", "Route added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/routes";
    }

    @PostMapping("/delete/{id}")
    public String deleteRoute(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            routeService.deleteRoute(id);
            redirectAttributes.addFlashAttribute("successMessage", "Route deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/routes";
    }
}
