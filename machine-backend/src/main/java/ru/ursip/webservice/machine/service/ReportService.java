package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service;
import org.apache.poi.ss.usermodel.Workbook;
/**
 * Report Service
 *
 * @author Samsonov
 * @since 07.11.2017
 */


public interface ReportService {

    /**
     * Create Excel Report Index rise
     *
     * @param periodId   - идентификатор периода
     * @return - excel документ
     */
    Workbook createExcelReportIndexRise(String periodId) throws Exception;

    /**
     * Create Excel Report current index
     *
     * @param periodId   - идентификатор периода
     * @return - excel документ
     */
    Workbook createExcelReportCurrentIndex(String periodId) throws Exception;

}
