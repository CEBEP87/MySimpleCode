package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger - configuration
 *
 * @author samsonov
 * @since 29.05.2017
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    /**
     * Documentation API
     *
     * @return - Documentation API
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.controller"))
                .paths(PathSelectors.regex("/v1/.*"))
                .build()
                .apiInfo(apiInfo());
    }

    /**
     * API information
     *
     * @return - API information
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX - API")
                .description("API сервиса \"Мониторинг цен\"")
                .version("1.0.0")
                .termsOfServiceUrl("http://www.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.ru")
                .license("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
                .licenseUrl("http://www.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.ru")
                .build();
    }
}