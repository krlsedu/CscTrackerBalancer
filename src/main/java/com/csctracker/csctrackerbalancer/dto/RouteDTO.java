package com.csctracker.csctrackerbalancer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteDTO {
    private Long id;
    private String path;
    private String destination;
    private String method;
    private String port;
    private String host;
    private PodDTO pod;

}
