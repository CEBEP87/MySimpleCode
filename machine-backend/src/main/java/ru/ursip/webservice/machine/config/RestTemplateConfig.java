package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * configuration for connection with RESTfull - services
 *
 * @author Samsonov
 * @since 30.10.2017
 */
@Configuration
@ConfigurationProperties(prefix = "machines.rest")
public class RestTemplateConfig {

    /**
     * URL service organizations
     */
    public static String organizations;

    /**
     * get URI service organization
     *
     * @return - URI service organization
     */
    public static String getOrganizations() {
        return organizations;
    }

    /**
     * set URI service organization
     *
     * @param organizations - URI service organization
     */
    public static void setOrganizations(String organizations) {
        RestTemplateConfig.organizations = organizations;
    }
}
