package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.report;

import org.springframework.core.io.ClassPathResource;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.report.ReportSettings;

import java.io.IOException;
import java.io.InputStream;

/**
 * Конфигуратор отчета
 *
 * @author samsonov
 * @since 15.03.2017
 */
public class ReportConfiguration {


    /**
     * Получение настроек для Excel документа в зависимости от типа необходимого документа
     *
     * @param reportDocTypeProperties - тип необходимого документа
     *                     report_current_period -  Список отпускных цен поставщиков материалов за 4 периода
     *
     *
     * @return - настройки для отчета
     */
    public static ReportSettings getReportSettings(ReportDocTypeProperties reportDocTypeProperties) throws IOException {
        InputStream configFile = new ClassPathResource(reportDocTypeProperties.getProperties()).getInputStream();
        return new ReportSettingsParser().parseExcelSettings(configFile);
    }


}
