package com.xlaoy.nmhgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistration;

/**
 * Created by Administrator on 2018/3/1 0001.
 */
@Configuration
public class WebFilterConfig {

    @Bean
    public CorsFilter corsFilter() {
        WebCorsRegistration corsRegistration = new WebCorsRegistration("/**");
        corsRegistration.allowedOrigins(CorsConfiguration.ALL)
                        .allowedMethods(
                                HttpMethod.PATCH.name(),
                                HttpMethod.HEAD.name(),
                                HttpMethod.OPTIONS.name(),
                                HttpMethod.GET.name(),
                                HttpMethod.HEAD.name(),
                                HttpMethod.POST.name(),
                                HttpMethod.PUT.name(),
                                HttpMethod.DELETE.name())
                        .allowedHeaders(CorsConfiguration.ALL);
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**", corsRegistration.getCorsConfiguration());
        return new CorsFilter(configurationSource);
    }

    private static class WebCorsRegistration extends CorsRegistration {

        public WebCorsRegistration(String pathPattern) {
            super(pathPattern);
        }

        @Override
        protected CorsConfiguration getCorsConfiguration() {
            return super.getCorsConfiguration();
        }
    }

}
