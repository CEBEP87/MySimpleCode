package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

/**
 * Created by samsonov_ky on 22.08.2017.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class UploadConfig {

    /**
     * Configuration bean download prices
     *
     * @return - factory
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("128000KB");
        factory.setMaxRequestSize("128000KB");
        return factory.createMultipartConfig();
    }
}
