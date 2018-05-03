package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.report;

/**
 * Настройки для типа документа
 *
 * @author samsonov
 * @since 15.03.2017
 */
public enum ReportDocTypeProperties {
    /**
     * XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
     */
    report_current_period("report/report_current_period.xml"),


    /**
     *XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
     */
    report_index_rise("report/report_index_rise.xml");

    /**
     * Файл настроек
     */
    private String properties;

    /**
     * Конструктор
     *
     * @param str - строка с файлом настроек
     */
    ReportDocTypeProperties(String str) {
        properties = str;
    }

    /**
     * Получение файла настроек
     *
     * @return - файл настроек
     */
    public String getProperties() {
        return properties;
    }

}
