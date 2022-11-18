package com.csctracker.csctrackerbalancer.service;

import com.csctracker.csctrackerbalancer.repository.RemoteRepository;
import org.springframework.stereotype.Service;

@Service
public class GenericService {
    private final RemoteRepository remoteRepository;
    private final RouteService routeService;

    public GenericService(RemoteRepository remoteRepository, RouteService routeService) {
        this.remoteRepository = remoteRepository;
        this.routeService = routeService;
    }

    public String get(String service, String object) {
        var route = routeService.getRoute(service);
        if (route == null) {
            throw new RuntimeException("No live pods");
        }
        routeService.lockPod(route.getPod());
        try {
            String s = remoteRepository.dispachGet(route.getDestination() + "/" + object);
            routeService.unlockPod(route.getPod());
            return s;
        } catch (Exception e) {
            routeService.unlockPod(route.getPod());
            throw e;
        }
    }

    public String save(String service, String object) {
        var route = routeService.getRoute(service);
        if (route == null) {
            throw new RuntimeException("No live pods");
        }
        routeService.lockPod(route.getPod());
        try {
            String s = remoteRepository.dispachPost(route.getDestination() + "/" + object);
            routeService.unlockPod(route.getPod());
            return s;
        } catch (Exception e) {
            routeService.unlockPod(route.getPod());
            throw e;
        }
    }

    public String delete(String service, String object) {
        var route = routeService.getRoute(service);
        if (route == null) {
            throw new RuntimeException("No live pods");
        }
        routeService.lockPod(route.getPod());
        try {
            String s = remoteRepository.dispachDelete(route.getDestination() + "/" + object);
            routeService.unlockPod(route.getPod());
            return s;
        } catch (Exception e) {
            routeService.unlockPod(route.getPod());
            throw e;
        }
    }
}
