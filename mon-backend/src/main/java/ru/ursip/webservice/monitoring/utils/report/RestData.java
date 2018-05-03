package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report;

import org.springframework.web.client.RestTemplate;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.config.RestTemplateConfig;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.report.CompanyReport;

import java.util.Arrays;
import java.util.List;

/**
 * Получение данных из RESTfull - сервисов
 *
 * @author samsonov
 * @since 30.05.2017
 */
public class RestData {

    /**
     * Шаблон REST - запросов
     */
    private static RestTemplate restTemplate = new RestTemplate();

    /**
     * Получение списка организаций
     *
     * @return - список организаций
     */
    public static List<CompanyReport> getCompanys() {
        CompanyReport[] companysMass = restTemplate.getForObject(RestTemplateConfig.organizations + "organizacii/zapros", CompanyReport[].class);
        return Arrays.asList(companysMass);
    }
}
