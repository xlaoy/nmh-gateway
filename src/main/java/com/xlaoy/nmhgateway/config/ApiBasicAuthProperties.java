package com.xlaoy.nmhgateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/2/23 0023.
 */
@ConfigurationProperties(prefix = "apibasicauth")
public class ApiBasicAuthProperties {

    private Map<String, BasicAuthInfo> services = new HashMap<>();

    public Map<String, BasicAuthInfo> getServices() {
        return services;
    }

    public void setServices(Map<String, BasicAuthInfo> services) {
        this.services = services;
    }

    public static class BasicAuthInfo {

        private String username;

        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

}
