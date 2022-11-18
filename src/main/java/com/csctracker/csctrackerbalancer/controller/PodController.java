package com.csctracker.csctrackerbalancer.controller;

import com.csctracker.configs.RequestInterceptor;
import com.csctracker.csctrackerbalancer.dto.PodDTO;
import com.csctracker.csctrackerbalancer.service.RouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PodController {

    private final RouteService routeService;

    public PodController(RouteService routeService) {
        this.routeService = routeService;
        RequestInterceptor.SECURE_REQUEST = true;
    }

    @PostMapping(value = "/pod", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody PodDTO podDTO) {
        try {
            routeService.registrerPod(podDTO);
        } catch (Exception e) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping(value = "/pod", produces = "application/json")
    public void delete(@RequestBody PodDTO podDTO) {
        try {
            routeService.removePod(podDTO);
        } catch (Exception e) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(value = "/lock-unlock", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public void lockUnlock(@RequestBody PodDTO podDTO) {
        try {
            routeService.lockUnlock(podDTO);
        } catch (Exception e) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
