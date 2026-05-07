package com.trainsystem.controller;

import com.trainsystem.exception.NoRouteFoundException;
import com.trainsystem.service.RouteFinderService;
import com.trainsystem.service.RouteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.ConstraintViolationException;
import java.util.List;

@Controller
public class RouteFinderController {

    private final RouteFinderService routeFinderService;
    private final RouteService routeService;

    public RouteFinderController(RouteFinderService routeFinderService, RouteService routeService) {
        this.routeFinderService = routeFinderService;
        this.routeService = routeService;
    }

    @GetMapping("/find-routes")
    public String showFindRoutesForm(Model model) {
        model.addAttribute("stations", routeService.getAllStations());
        return "find-routes";
    }

    @PostMapping("/find-routes")
    public String findRoutes(@RequestParam String origin,
                             @RequestParam String destination,
                             Model model) {
        model.addAttribute("stations", routeService.getAllStations());
        model.addAttribute("origin", origin);
        model.addAttribute("destination", destination);

        try {
            List<String> connections = routeFinderService.findConnections(origin, destination);
            model.addAttribute("connections", connections);
        } catch (NoRouteFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (ConstraintViolationException e) {
            String msg = e.getConstraintViolations().iterator().next().getMessage();
            model.addAttribute("errorMessage", msg);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        return "find-routes";
    }
}
