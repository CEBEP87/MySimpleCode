package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * info API
 *
 * @author samsonov
 * @since 16.10.2017
 */
@Controller
public class ApiInfo {

    /**
     * Method for relocated to documentation page
     *
     * @return relocation to documentation page
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
