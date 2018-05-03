package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report;

import org.springframework.core.io.ClassPathResource;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.report.ReportSettings;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.parser.ReportSettingsParser;

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
     *                      RESULT_FOUR_PERIODS_MATERIALS_MON -  Список отпускных цен поставщиков материалов за 4 периода
     *                      RESULT_FOUR_PERIODS_EQUIPMENT_MON - Список отпускных цен поставщиков оборудования за 4 периода
     *                      RESULT_PERIOD_MATERIALS_MON - Список текущих отпускных цен поставщиков строительных материалов за период
     *                      RESULT_PERIOD_EQUIPMENT_MON - Список текущих отпускных цен поставщиков оборудования за период
     *                      RESULT_NOT_FALL_CORRIDOR_MATERIALS - Список по материалам, которые не попали в установленный коридор роста/снижения цен
     *                      RESULT_NOT_FALL_CORRIDOR_EQUIPMENT - Список по оборудованию, которое не попало в установленный коридор роста/снижения цен
     *                      RESULT_PERIOD_MINPRICE_EQUIPMENT_MON - Список по минимальной цене для оборудования за период
     *                      RESULT_PERIOD_MINPRICE_MATERIALS_MON - Список по минимальной цене для материалов за период
     *                      RESULT_MOSCOMEXP_EQUIPMENT_MONITORING_PROPERTIES - список для москомэкспертизы для оборудования
     *                      RESULT_MOSCOMEXP_MATERIALS_MONITORING_PROPERTIES - список для москомэкспертизы для материалов
     *
     * @return - настройки для отчета
     */
    public static ReportSettings getReportSettings(ReportDocTypeProperties reportDocTypeProperties) throws IOException {
        InputStream configFile = new ClassPathResource(reportDocTypeProperties.getProperties()).getInputStream();
        return new ReportSettingsParser().parseExcelSettings(configFile);
    }


}
