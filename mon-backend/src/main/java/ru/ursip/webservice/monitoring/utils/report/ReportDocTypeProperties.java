package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report;

/**
 * Настройки для типа документа
 *
 * @author samsonov
 * @since 15.03.2017
 */
public enum ReportDocTypeProperties {
    /**
     * Список отпускных цен поставщиков материалов за 4 периода для москомэкспертизы
     */
    RESULT_MOSCOMEXP_MON_MATERIALS_MON("report/moscomexp_materials_monitoring_properties.xml"),

    /**
     * Список отпускных цен поставщиков оборудования за 4 периода  для москомэкспертизы
     */
    RESULT_MOSCOMEXP_MON_EQUIPMENT_MON("report/moscomexp_equipment_monitoring_properties.xml"),


    /**
     * Список отпускных цен поставщиков материалов за 4 периода
     */
    RESULT_FOUR_PERIODS_MATERIALS_MON("report/four_periods_materials_monitoring_properties.xml"),

    /**
     * Список отпускных цен поставщиков оборудования за 4 периода
     */
    RESULT_FOUR_PERIODS_EQUIPMENT_MON("report/four_periods_equipment_monitoring_properties.xml"),

    /**
     * Список отпускных цен поставщиков строительных материалов по минимальной цене за период
     */
    RESULT_PERIOD_MINPRICE_MATERIALS_MON("report/current_period_minprice_materials_monitoring_properties.xml"),

    /**
     * Список отпускных цен поставщиков оборудования по минимальной цене за период
     */
    RESULT_PERIOD_MINPRICE_EQUIPMENT_MON("report/current_period_minprice_equipment_monitoring_properties.xml"),

    /**
     * Список отпускных цен материалов по средней цене за период
     */
    RESULT_PERIOD_AVGRICE_MATERIALS_MON("report/current_period_avgprice_materials_monitoring_properties.xml"),

    /**
     * Список отпускных цен оборудования по средней цене за период
     */
    RESULT_PERIOD_AVGPRICE_EQUIPMENT_MON("report/current_period_avgprice_equipment_monitoring_properties.xml"),

    /**
     * Список отпускных цен поставщиков материалов за период
     */
    RESULT_PERIOD_MATERIALS_MON("report/current_period_materials_monitoring_properties.xml"),

    /**
     * Список отпускных цен поставщиков оборудования за период
     */
    RESULT_PERIOD_EQUIPMENT_MON("report/current_period_equipment_monitoring_properties.xml"),

    /**
     * Список по материалам, которые не попали в установленный коридор роста/снижения цен
     */
    RESULT_NOT_FALL_CORRIDOR_MATERIALS("report/not_fall_corridor_materials_properties.xml"),

    /**
     * Список по оборудованию, которое не попало в установленный коридор роста/снижения цен
     */
    RESULT_NOT_FALL_CORRIDOR_EQUIPMENT("report/not_fall_corridor_equipment_properties.xml");




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
