package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.impl;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.Period;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.report.ReportSettings;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.report.ExcelCurrentPeriodCreate;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.report.ExcelRiseIndexCreate;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.PeriodService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.ReportService;

import java.util.HashMap;
import java.util.Map;


/**
 * Report service
 *
 * @author Samsonov
 * @since 07.11.2017
 */
@Service
public class ReportServiceImpl implements ReportService {
    /**
     * Количество столбцов в таблице
     */
    private static int maxTableColumn = 0;

    /**
     * Настройки отчета
     */
    private static ReportSettings settings = null;

    /**
     * Результирующий Excel - документ
     */
    private static Workbook resultExcel = null;

    /**
     * Excel - лист
     */
    private static Sheet sheet;

    /**
     * Основной стиль ячеек в таблице(выравнивание по левой стороне)
     */
    private static CellStyle leftTableInfoStyle = null;

    /**
     * Дополнительный стиль ячеек в таблице(выравнивание по центру)
     */
    private static CellStyle centerTableInfoStyle = null;

    /**
     * Дополнительный стиль ячеек в таблице(выравнивание по правой стороне)
     */
    private static CellStyle rightTableInfoStyle = null;

    /**
     * Список организаций
     */
    private static Map<Integer, String> companys = new HashMap<>();

    /**
     * PeriodService
     */
    @Autowired
    private PeriodService periodService;

    /**
     * Create report Index Rise
     *
     * @param periodId - period ID
     * @return - excel template
     */
    @Override
    public Workbook createExcelReportIndexRise(String periodId) throws Exception {
        Period period = periodService.findOne(periodId);
        Period previousPeriod = periodService.previousPeriod(periodId);
        Workbook resultReport = null;
        resultReport = ExcelRiseIndexCreate.createNewExcelDocument(periodService.sortByTSN(period),periodService.sortByTSN(previousPeriod));
        return resultReport;
    }

    /**
     * Create report current index
     *
     * @param periodId - period ID
     * @return - excel template
     */
    @Override
    public Workbook createExcelReportCurrentIndex(String periodId) throws Exception {
        Period period = periodService.findOne(periodId);
        Workbook resultReport = null;
        resultReport = ExcelCurrentPeriodCreate.createNewExcelDocument(periodService.sortByTSN(period));
        return resultReport;
    }
}
