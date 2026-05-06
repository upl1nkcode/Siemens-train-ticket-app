package com.trainsystem.controller;

import com.trainsystem.service.RouteService;
import com.trainsystem.service.ScheduleService;
import com.trainsystem.service.TrainService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final RouteService routeService;
    private final TrainService trainService;
    private final ScheduleService scheduleService;

    public DashboardController(RouteService routeService, TrainService trainService, ScheduleService scheduleService) {
        this.routeService = routeService;
        this.trainService = trainService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("stationCount", routeService.getAllStations().size());
        model.addAttribute("routeCount", routeService.getAllRoutes().size());
        model.addAttribute("trainCount", trainService.getAllTrains().size());
        model.addAttribute("scheduleCount", scheduleService.getAllSchedules().size());
        return "dashboard";
    }
}
