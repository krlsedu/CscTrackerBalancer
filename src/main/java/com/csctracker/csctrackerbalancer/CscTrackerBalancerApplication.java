package com.csctracker.csctrackerbalancer;

import kong.unirest.Unirest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"com.csctracker"})
@EnableScheduling
public class CscTrackerBalancerApplication {

    public static void main(String[] args) {
        Unirest.config().socketTimeout(60 * 10 * 1000);
        SpringApplication.run(CscTrackerBalancerApplication.class, args);
    }

}
