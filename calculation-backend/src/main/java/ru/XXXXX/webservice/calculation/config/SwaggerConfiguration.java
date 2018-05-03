package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.config;

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
 * @author Samsonov
 * @since 29.10.2017
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    /**
     * Settings spring fox
     *
     * @return настройка spring fox
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("/v1/.*"))
                .build()
                .apiInfo(apiInfo());
    }

    /**
     * Get information about API swagger
     *
     * @return - information about API swagger
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
                .description("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
                .version("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
                .termsOfServiceUrl("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
                .license("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
                .licenseUrl("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
                .build();
    }
}