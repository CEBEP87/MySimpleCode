package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report;

/**
 * Тип документа
 *
 * @author samsonov
 * @since 25.05.2017
 */
public enum ReportType {

    /**
     * Отчет по списку отпускных цен исходя из метода вычислений за период
     */
    RESULT_PERIOD_CALC_METHOD_MON,

    /**
     * Отчет по списку отпускных цен, которые не попали в установленный коридор
     */
    RESULT_NOT_FALL_CORRIDOR,

    /**
     * Отчет по списку отпускных цен за период
     */
    RESULT_PERIOD_MON,

    /**
     * Отчет по списку отпускных цен поставщиков материалов за 4 периода
     */
    FOUR_PERIODS_MON,

    /**
     * Отчет по списку отпускных цен поставщиков материалов за 4 периода для МосКомЭксперизы
     */
    RESULT_MOSCOMEXP_MON,
    /**
     * Отчет по списку организаций, включенных в период
     */
    PERIOD_ORGS;
}
