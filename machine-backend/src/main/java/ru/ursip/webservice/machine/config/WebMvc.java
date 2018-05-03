package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Web Mvc
 *
 * @author samsonov
 * @since 05.11.2017
 */
@Configuration
@EnableWebMvc
public class WebMvc extends WebMvcConfigurerAdapter {

    /**
     * ViewControllers
     *
     * @param registry - registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/documentation/v2/api-docs", "/v2/api-docs").setKeepQueryParams(true);
        registry.addRedirectViewController("/documentation/swagger-resources/configuration/ui", "/swagger-resources/configuration/ui");
        registry.addRedirectViewController("/documentation/swagger-resources/configuration/security", "/swagger-resources/configuration/security");
        registry.addRedirectViewController("/documentation/swagger-resources", "/swagger-resources");
        registry.addRedirectViewController("/documentation", "/documentation/swagger-ui.html");
        registry.addRedirectViewController("/documentation/", "/documentation/swagger-ui.html");
        registry.addRedirectViewController("/v1/", "/documentation/swagger-ui.html").setKeepQueryParams(true);
        registry.addRedirectViewController("/v1", "/documentation/swagger-ui.html").setKeepQueryParams(true);
    }

    /**
     * ResourceHandlers
     *
     * @param registry registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/documentation/**").addResourceLocations("classpath:/META-INF/resources/");
    }

}
