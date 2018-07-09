package com.xlaoy.nmhgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@EnableEurekaClient
@EnableCircuitBreaker
@SpringBootApplication(scanBasePackages = "com.xlaoy")
public class NmhGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(NmhGatewayApplication.class, args);
    }
}
