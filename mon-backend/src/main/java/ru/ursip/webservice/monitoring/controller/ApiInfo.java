package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * info API
 *
 * @author aneichikes
 * @since 21.02.2017
 */
@Controller
public class ApiInfo {

    /**
     * redirect to documentation page
     *
     * @return redirect to documentation page
     */
    @GetMapping(value = "/v1")
    public String getInfo() throws IOException {
        try {
            return "forward:/swagger-docapi.html";
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
}

