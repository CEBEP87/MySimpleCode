package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * configuration use RESTfull - services
 *
 * @author samsonov
 * @since 30.05.2017
 */
@Configuration
@ConfigurationProperties(prefix = "monitoring.rest")
public class RestTemplateConfig {

    /**
     * organization service URL
     */
    public static String organizations;

    /**
     * get organization service URL
     * @return - organization service URL
     */
    public static String getOrganizations() {
        return organizations;
    }

    /**
     * set organization service URL
     * @param organizations - organization service URL
     */
    public static void setOrganizations(String organizations) {
        RestTemplateConfig.organizations = organizations;
    }
}
