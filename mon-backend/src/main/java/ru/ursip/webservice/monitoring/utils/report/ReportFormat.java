package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report;

/**
 * Перечисление форматов отчета
 *
 * @author samsonov
 * @since 26.06.2017
 */
public enum ReportFormat {

    /**
     * EXCEL
     */
    EXCEL("EXCEL"),

    /**
     * PDF
     */
    PDF("PDF");

//    /**
//     * WORD
//     */
//    WORD("WORD");

    /**
     * Строка с форматом отчета
     */
    private String reportFormat;

    /**
     * Конструктор
     *
     * @param reportFormat - формат отчета
     */
    ReportFormat(String reportFormat) {
        this.reportFormat = reportFormat;
    }

    /**
     * Получение формата отчета
     *
     * @return - формат отчета
     */
    public String getReportFormat() {
        return reportFormat;
    }
}
