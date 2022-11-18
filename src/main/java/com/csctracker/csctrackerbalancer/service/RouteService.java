package com.csctracker.csctrackerbalancer.service;

import com.csctracker.csctrackerbalancer.dto.PodDTO;
import com.csctracker.csctrackerbalancer.dto.RouteDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RouteService {
    private Map<String, Integer> nextPod;
    private Map<String, List<PodDTO>> pods;
    private Map<String, List<PodDTO>> lockedPods;

    public RouteService() {
        init();
    }

    public void init() {
        pods = new ConcurrentHashMap<>();
        lockedPods = new ConcurrentHashMap<>();
        nextPod = new ConcurrentHashMap<>();
    }

    public void registrerPod(PodDTO podDTO) {
        podDTO.setLastHeartbeat(new Date());
        var pods = this.pods.computeIfAbsent(podDTO.getService(), k -> new ArrayList<>());
        for (var pod : pods) {
            if (pod.getHost().equals(podDTO.getHost())) {
                pod.setLastHeartbeat(podDTO.getLastHeartbeat());
                return;
            }
        }
        log.info("Registrando pod: " + podDTO);
        podDTO.setPodId(pods.size());
        pods.add(podDTO);
    }

    public void lockUnlock(PodDTO podDTO) {
        if (podDTO.isLocked()) {
            unlockPod(podDTO);
        } else {
            lockPod(podDTO);
        }
    }

    public boolean isLocked(PodDTO podDTO) {
        var pods = this.lockedPods.computeIfAbsent(podDTO.getService(), k -> new ArrayList<>());
        for (var pod : pods) {
            if (pod.getHost().equals(podDTO.getHost())) {
                return true;
            }
        }
        return false;
    }

    public void removePod(PodDTO podDTO) {
        var pods = this.pods.computeIfAbsent(podDTO.getService(), k -> new ArrayList<>());
        for (var pod : pods) {
            if (pod.getHost().equals(podDTO.getHost())) {
                pods.remove(pod);
                return;
            }
        }
    }

    public void lockPod(PodDTO pod) {
        var pods = lockedPods.computeIfAbsent(pod.getService(), k -> new ArrayList<>());
        for (var podLocked : pods) {
            if (Objects.equals(podLocked.getPodId(), pod.getPodId())) {
                return;
            }
        }
        pods.add(pod);
    }

    public void unlockPod(PodDTO pod) {
        var pods = lockedPods.computeIfAbsent(pod.getService(), k -> new ArrayList<>());
        for (var podLocked : pods) {
            if (Objects.equals(podLocked.getPodId(), pod.getPodId())) {
                pods.remove(podLocked);
                return;
            }
        }
    }

    public PodDTO getUlockedPod(String service) {
        var podNum = nextPod.getOrDefault(service, 0);
        if (podNum >= pods.get(service).size()) {
            podNum = 0;
        }
        while (isLocked(pods.get(service).get(podNum))) {
            log.info("Pod locked: " + pods.get(service).get(podNum));
            podNum++;
            if (podNum >= pods.get(service).size()) {
                podNum = 0;
            }
        }
        nextPod.put(service, podNum + 1);
        var podDTO = pods.get(service).get(podNum);
        podDTO.setPodId(podNum);
        return podDTO;
    }

    public RouteDTO getRoute(String service) {
        return getPod(service);
    }

    private RouteDTO getPod(String service) {
        if (pods.get(service) != null && !pods.get(service).isEmpty()) {
            var pod = getUlockedPod(service);
            var route = new RouteDTO();
            route.setDestination("http://" + pod.getHost() + ":" + pod.getPort());
            route.setPod(pod);
            log.info("Pod: " + pod.getPodId() + " -> " + pod.getHost() + ":" + pod.getPort());
            return route;
        }
        return null;
    }

    @Scheduled(fixedRate = 10000)
    public void checkPods() {
        for (var service : pods.keySet()) {
            var pods = this.pods.get(service);
            var podsToRemove = new ArrayList<PodDTO>();
            for (var pod : pods) {
                if (pod.getLastHeartbeat().getTime() + 10000 < new Date().getTime()) {
                    log.info("Pod is dead -> " + pod);
                    podsToRemove.add(pod);
                }
            }
            pods.removeAll(podsToRemove);
        }
    }
}
