package com.csctracker.csctrackerbalancer.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PodDTO {
    private String service;
    private String port;
    private String host;
    private Integer podId;
    private boolean locked;
    private Date lastHeartbeat;
}
