package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.report;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Period;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Material;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.report.ReportSettings;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.report.ReportTableHeadColumn;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Create excel report
 *
 * @author Samsonov
 * @since 07.11.2017
 */
public class ExcelRiseIndexCreate {


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
     * Создание отчета
     *
     * @param period - period object
     * @param previousPeriod - previousPeriod object
     * @return - excel отчет
     */
    public static Workbook createNewExcelDocument(Period period,Period previousPeriod) throws IOException {
        resultExcel = new SXSSFWorkbook();

        settings = ReportConfiguration.getReportSettings(ReportDocTypeProperties.report_index_rise);
        List<ReportTableHeadColumn> reportTableHeadColumn = settings.getTableHeadColumns();
        maxTableColumn = reportTableHeadColumn.size();
        // Формируем строку шапки таблицы с информацией о периодах
        Calendar currentPeriodCalendar = Calendar.getInstance();
    //    currentPeriodCalendar.setTime(period.getFields().getDateFrom());
        ReportTableHeadColumn currentPeriodColumn = reportTableHeadColumn.get(6);
        String currentPeriodStringName = currentPeriodCalendar.get(Calendar.YEAR) + " год";
        reportTableHeadColumn.set(6, currentPeriodColumn);

        List<String> titleList = settings.getTitleList();
        titleList.set(titleList.size() - 1, titleList.get(titleList.size() - 1) + " " + currentPeriodStringName);

        settings.setTableHeadColumns(reportTableHeadColumn);

        // создание листа с названием
        sheet = resultExcel.createSheet(settings.getDocName());
        // счетчик для строк
        int rowNum = 0;
        //создание шапки
        if (settings.getServiceList() != null) {
            rowNum = createService(rowNum);
        }
        //Создание заголовка таблицы
        if (settings.getTitleList() != null) {
            rowNum = createTitle(rowNum);
        }
        //Создание заголовка таблицы
        if (settings.getTableHeadColumns() != null) {
            // создаем стиль для ячейки
            leftTableInfoStyle = resultExcel.createCellStyle();
            setUpLeftTableInfoStyle(resultExcel);
            centerTableInfoStyle = resultExcel.createCellStyle();
            setUpCenterTableInfoStyle(resultExcel);
            rightTableInfoStyle = resultExcel.createCellStyle();
            setUpRightTableInfoStyle(resultExcel);
            List<Row> tableHeadRows = createHeadTableRows(rowNum);
            rowNum = tableHeadRows.get(tableHeadRows.size() - 1).getRowNum();

            //Установка стилей для заголовка таблицы
            for (Row currentRow : tableHeadRows) {
                setUpHeaderTableStyle(resultExcel, currentRow);
            }

            //Создание строки с номерами
            rowNum = createNumericTableRow(rowNum);

            //Установка стилей для строки нумерации
            Row numericRow = sheet.getRow(rowNum);
            setUpNumericTableStyle(resultExcel, numericRow);

            //Счетчик записи организации
            int countResourcePeriod = 0;
            //Заполнение данными
            //Установка стилей для строки информации о ресурсы в периоде
           for (Material resourcePeriod : period.getResource()) {
                Material previousResource=null;
                for(Material res:previousPeriod.getResource())
                {
                    if (ObjectUtils.notEqual(resourcePeriod.getActiveResourceIdentificator(), null))
                        if(resourcePeriod.getActiveResourceIdentificator().equals(res.getId())) {
                        previousResource = res;
                    }
                }

                Row resourcePeriodRow = createTableRow(++rowNum, resourcePeriod, ++countResourcePeriod,previousResource);
                //Установка стилей для строки информации о ресурсы в периоде
                setUpResourcePeriodInfoStyle(resourcePeriodRow);
                rowNum = resourcePeriodRow.getRowNum();

            }
        }

        if (settings.getFooterList() != null) {
            createFooterRow(++rowNum);
        }

        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 3500);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(4, 10000);
        sheet.setColumnWidth(5, 2000);
        sheet.setColumnWidth(6, 3000);

        sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
        sheet.getPrintSetup().setLandscape(true);
        sheet.getPrintSetup().setScale((short) 70);

        return resultExcel;
    }

    /**
     * Создание строк со служебной информацией
     *
     * @param rowNum - номер последней заполненной строки
     * @return - номер последней заполненной строки
     */
    private static int createService(int rowNum) {
        int endRow = rowNum;
        for (String strRow : settings.getServiceList()) {
            Row currentRow = sheet.createRow(++endRow);
            currentRow.createCell(0).setCellValue(strRow);
            sheet.addMergedRegion(new CellRangeAddress(endRow, endRow, 0, settings.getTableHeadColumns().size() - 1));
            setUpServiceStyle(resultExcel, currentRow);

        }
        //Делаем отступ от служебной информации если она была задана в настройках
        if (settings.getServiceList().size() > 0) ++endRow;
        return endRow;
    }

    /**
     * Создание строк заголовка для таблицы
     *
     * @param rowNum - номер последней заполненной строки
     * @return - номер последней заполненной строки
     */
    private static int createTitle(int rowNum) {
        int endRow = rowNum;
        for (String strRow : settings.getTitleList()) {
            Row currentRow = sheet.createRow(++endRow);
            currentRow.createCell(0).setCellValue(strRow);
            sheet.addMergedRegion(new CellRangeAddress(endRow, endRow, 0, settings.getTableHeadColumns().size() - 1));
            setUpTitleStyle(resultExcel, currentRow);
        }
        //Делаем отступ от заголовка таблицы если он был задан в настройках
        if (settings.getTitleList().size() > 0) ++endRow;
        return endRow;
    }

    /**
     * Формирование строки заголовка
     *
     * @param rowNum - номер строки
     * @return - список строк заголовка
     */
    private static List<Row> createHeadTableRows(int rowNum) {
        List<Row> result = new ArrayList<>();
        int endRow = rowNum;
        int colIndex = 0;
//        Row headRow = sheet.createRow(endRow);
        int countTableRows = 0;
        for (ReportTableHeadColumn currentColumn : settings.getTableHeadColumns()) {
            int currentColumnCountRows = currentColumn.getRows().size();
            if (countTableRows <= currentColumnCountRows) {
                countTableRows = currentColumnCountRows;
            }
        }
        for (int i = 0; i < countTableRows; i++) {
            Row headRow = sheet.createRow(endRow);
            result.add(headRow);
            endRow++;
        }
        for (ReportTableHeadColumn currentColumn : settings.getTableHeadColumns()) {
            int currentRow = rowNum;
            for (String currentRowString : currentColumn.getRows()) {
                sheet.getRow(currentRow).createCell(colIndex).setCellValue(currentRowString);
                currentRow++;
            }
            colIndex++;
        }
        mergeEmptyCells(result);
        return result;
    }

    /**
     * Слияние пустых ячеек в одну
     * (В случае если строк <=2)
     *
     * @param rowList - список строк
     * @return - true - если было хотя бы одно объединение, иначе - false
     */
    private static boolean mergeEmptyCells(List<Row> rowList) {
        boolean result = false;
        int rowCount = rowList.size();
        int cellCount = rowList.get(0).getLastCellNum();
        boolean[][] mergedMap = new boolean[rowCount][cellCount];
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < cellCount; j++) {
                String cellStr = rowList.get(i).getCell(j).getStringCellValue();
                if (!cellStr.equals("")) {
                    boolean merged = true;
                    int countRowMerged = 1;
                    int countColMerged = 1;
                    int rowN = i + 1;
                    int colN = j + 1;
                    boolean isRowMerged = false;
                    boolean isColMerged = false;
                    while (merged) {
                        mergedMap[rowN - 1][j] = true;
                        if (rowN < rowCount && settings.getTableHeadColumns().get(j).getRows().get(rowN).equals("") &&
                                !mergedMap[rowN][j]) {
                            mergedMap[rowN][j] = true;
                            countRowMerged++;
                            rowN++;
                            isRowMerged = true;
                        } else merged = false;
                    }
                    if (isRowMerged) {
                        CellRangeAddress hRange = new CellRangeAddress(rowList.get(i).getRowNum(), rowList.get(rowN - 1).getRowNum(), j, j);
                        sheet.addMergedRegion(hRange);
                        result = true;
                    } else {
                        merged = true;
                        while (merged) {
                            mergedMap[i][colN - 1] = true;
                            if (colN < maxTableColumn
                                    && settings.getTableHeadColumns().get(colN).getRows().get(i).equals("")
                                    && !mergedMap[i][colN]) {
                                mergedMap[i][colN] = true;
                                countColMerged++;
                                colN++;
                                isColMerged = true;
                            } else merged = false;
                        }
                        if (isColMerged) {
                            CellRangeAddress hRange = new CellRangeAddress(rowList.get(i).getRowNum(),
                                    rowList.get(i).getRowNum(),
                                    j, colN - 1);
                            sheet.addMergedRegion(hRange);
                            result = true;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Создание строки с номерами
     *
     * @param rowNum - номер строки
     * @return - номер строки
     */

    private static int createNumericTableRow(int rowNum) {
        int endRow = rowNum;
        Row headRow = sheet.getRow(endRow);
        Row numericRow = sheet.createRow(++endRow);
        ;
        for (int i = 0; i < headRow.getPhysicalNumberOfCells(); i++) {
            numericRow.createCell(i).setCellValue(i + 1);
        }
        return endRow;
    }

    /**
     * Формирование строки с информацией о ресурсе в периоде
     *
     * @param rowNum                - номер последней заполненной строки
     * @param currentResourcePeriod - информация о ресурсе в текущем периоде
     * @param countResourcePeriod   - счетчик ресурсов в периоде в таблице
     * @param previousResource   - previousResource в периоде в таблице
     * @return - строка с информацией о ресурсы в периоде
     */
    private static Row createTableRow(int rowNum, Material currentResourcePeriod, int countResourcePeriod, Material previousResource) {
        Row infoRow = sheet.createRow(rowNum);
        for (int i = 0; i < maxTableColumn; i++) {
            infoRow.createCell(i).setCellValue("-");
        }
        infoRow.getCell(0).setCellValue(countResourcePeriod);
        double currentRublPrice = 0d;
        if (ObjectUtils.notEqual(currentResourcePeriod.getFields().getCodeTsn(), null)) {
            infoRow.getCell(1).setCellValue(currentResourcePeriod.getFields().getCodeTsn());
        }
        if (ObjectUtils.notEqual(currentResourcePeriod.getFields().getRName(), null)) {
            infoRow.getCell(2).setCellValue(currentResourcePeriod.getFields().getRName());
        }
        if (ObjectUtils.notEqual(currentResourcePeriod.getFields().getCodeOkpd2(), null)) {
            infoRow.getCell(3).setCellValue(currentResourcePeriod.getFields().getCodeOkpd2());
        }
        if (ObjectUtils.notEqual(currentResourcePeriod.getFields().getEstimatedBasePrice(), null)) {
            infoRow.getCell(4).setCellValue(currentResourcePeriod.getFields().getEstimatedBasePrice());
        }
        if (ObjectUtils.notEqual(currentResourcePeriod.getFields().getEstimatedBasePriceIncludeTaxByDriver(), null)) {
            infoRow.getCell(5).setCellValue(currentResourcePeriod.getFields().getEstimatedBasePriceIncludeTaxByDriver());
        }

        if (ObjectUtils.notEqual(CalculationsServiceImpl.manualInputValidation(currentResourcePeriod.getFields().getCoefRecountCurrent(),currentResourcePeriod.getFields().getCoefRecountCurrentH()), null)) {
            infoRow.getCell(6).setCellValue(CalculationsServiceImpl.manualInputValidation(currentResourcePeriod.getFields().getCoefRecountCurrent(),currentResourcePeriod.getFields().getCoefRecountCurrentH()));
        }
        if (ObjectUtils.notEqual(currentResourcePeriod.getFields().getCoefRecountOfIncludeTaxByDriver(), null)) {
            infoRow.getCell(7).setCellValue(currentResourcePeriod.getFields().getCoefRecountOfIncludeTaxByDriver());
        }
        if (ObjectUtils.notEqual(CalculationsServiceImpl.manualInputValidation(currentResourcePeriod.getFields().getEstimatedCurrentPrice(),currentResourcePeriod.getFields().getEstimatedCurrentPriceH()), null)) {
            infoRow.getCell(8).setCellValue(CalculationsServiceImpl.manualInputValidation(currentResourcePeriod.getFields().getEstimatedCurrentPrice(),currentResourcePeriod.getFields().getEstimatedCurrentPriceH()));
        }
        if (ObjectUtils.notEqual(currentResourcePeriod.getCostItems().getSalary().getIndexSalary(), null)) {
            infoRow.getCell(9).setCellValue(currentResourcePeriod.getCostItems().getSalary().getIndexSalary());
        }

        if (ObjectUtils.notEqual(previousResource, null)) {
            if (ObjectUtils.notEqual(previousResource.getCostItems().getSalary().getIndexSalary(), null)) {
           infoRow.getCell(11).setCellValue(previousResource.getCostItems().getSalary().getIndexSalary());
        }

        if (ObjectUtils.notEqual(previousResource.getCostItems().getSalary().getIndexSalary(), null)) {
            infoRow.getCell(12).setCellValue(currentResourcePeriod.getCostItems().getSalary().getIndexSalary()-previousResource.getCostItems().getSalary().getIndexSalary());
        }

        if (ObjectUtils.notEqual(previousResource.getCostItems().getSalary().getIndexSalary(), null)) {
            infoRow.getCell(13).setCellValue((currentResourcePeriod.getCostItems().getSalary().getIndexSalary()-previousResource.getCostItems().getSalary().getIndexSalary()
                    /previousResource.getCostItems().getSalary().getIndexSalary())*100);
        }
        }
        return infoRow;
        }


        /**
         * Формирование строки с информацией футера
         *
         * @param rowNum - номер последней заполненной строки
         * @return - номер строки
         */

    private static int createFooterRow(int rowNum) {
        int endRow = rowNum;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        for (String strRow : settings.getFooterList()) {
            Row currentRow = sheet.createRow(++endRow);
            if (strRow.contains("[data]")) {
                StringBuilder sb = new StringBuilder(strRow);
                strRow = sb.substring(0, sb.indexOf("["));
                Date currentDate = new Date();
                currentRow.createCell(2).setCellValue(sdf.format(currentDate));
            }
            currentRow.createCell(0).setCellValue(strRow);
            sheet.addMergedRegion(new CellRangeAddress(endRow, endRow, 0, 1));
            setUpFooterStyle(resultExcel, currentRow);
        }
        return endRow;
    }


    /**
     * Установка настроек для информации о ресурсе в периоде
     *
     * @param row - строка для установки стиля
     */
    private static void setUpResourcePeriodInfoStyle(Row row) {
        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
            // применяем созданный выше стиль к каждой ячейке
            row.getCell(i).setCellStyle(leftTableInfoStyle);
        }
        row.getCell(0).setCellStyle(centerTableInfoStyle);
        row.getCell(5).setCellStyle(centerTableInfoStyle);
        row.getCell(6).setCellStyle(rightTableInfoStyle);
    }

    /**
     * Установка настроек стиля ячеек таблицы с выравниванием по левой стороне
     *
     * @param workbook - exсel файл
     */
    private static void setUpLeftTableInfoStyle(Workbook workbook) {
        // создаем шрифт
        Font font = workbook.createFont();
        // указываем, что хотим его видеть жирным
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        font.setFontHeightInPoints(settings.getTableHeadFontSize());
        font.setFontName(settings.getFontName());
        // применяем к стилю шрифт
        leftTableInfoStyle.setFont(font);
        //применяем к стилю вырванивание текста по центру
        leftTableInfoStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        leftTableInfoStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        //Создание рамки
        leftTableInfoStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        leftTableInfoStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        leftTableInfoStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        leftTableInfoStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);

        //Установка переноса
        leftTableInfoStyle.setWrapText(true);
    }

    /**
     * Установка настроек стиля ячеек таблицы с выравниванием по центру
     *
     * @param workbook - exсel файл
     */
    private static void setUpCenterTableInfoStyle(Workbook workbook) {

        // создаем шрифт
        Font font = workbook.createFont();
        // указываем, что хотим его видеть жирным
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        font.setFontHeightInPoints(settings.getTableHeadFontSize());
        font.setFontName(settings.getFontName());
        // применяем к стилю шрифт
        centerTableInfoStyle.setFont(font);
        //применяем к стилю вырванивание текста по центру
        centerTableInfoStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        centerTableInfoStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        //Создание рамки
        centerTableInfoStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        centerTableInfoStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        centerTableInfoStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        centerTableInfoStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);

        //Установка переноса
        centerTableInfoStyle.setWrapText(true);
    }

    /**
     * Установка настроек стиля ячеек таблицы с выравниванием по правой стороне
     *
     * @param workbook - exсel файл
     */
    private static void setUpRightTableInfoStyle(Workbook workbook) {
        // создаем шрифт
        Font font = workbook.createFont();
        // указываем, что хотим его видеть жирным
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        font.setFontHeightInPoints(settings.getTableHeadFontSize());
        font.setFontName(settings.getFontName());
        // применяем к стилю шрифт
        rightTableInfoStyle.setFont(font);
        //применяем к стилю вырванивание текста по центру
        rightTableInfoStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        rightTableInfoStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        //Создание рамки
        rightTableInfoStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        rightTableInfoStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        rightTableInfoStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        rightTableInfoStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);

        //Установка переноса
        rightTableInfoStyle.setWrapText(true);
    }

    /**
     * Установка настроек для информации о разделе ресурса
     *
     * @param workbook - exсel файл
     * @param row      - строка для установки стиля
     */
    private static void setUpSectionInfoStyle(Workbook workbook, Row row) {
        // создаем стиль для ячейки
        CellStyle style = workbook.createCellStyle();
        // создаем шрифт
        Font font = workbook.createFont();
        // указываем, что хотим его видеть жирным
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints(settings.getTableHeadFontSize());
        font.setFontName(settings.getFontName());
        // применяем к стилю шрифт
        style.setFont(font);
        //применяем к стилю вырванивание текста по центру
        style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_JUSTIFY);
        //Создание рамки
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);

        //Установка переноса
        style.setWrapText(true);
        int k = row.getPhysicalNumberOfCells();
        // проходим по всем ячейкам этой строки
        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
            // применяем созданный выше стиль к каждой ячейке
            row.getCell(i).setCellStyle(style);
        }
    }

    /**
     * Установка настроек служебной информации
     *
     * @param workbook - exсel файл
     * @param row      - строка для установки стиля
     */
    private static void setUpServiceStyle(Workbook workbook, Row row) {
        // создаем стиль для ячейки
        CellStyle style = workbook.createCellStyle();
        //Вырванивание текста по правой стороне
        style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

        // создаем шрифт
        Font font = workbook.createFont();
        // указываем, что хоти его видеть жирным
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        font.setFontHeightInPoints(settings.getFooterFontSize());
        font.setFontName(settings.getFontName());
        // применяем к стилю шрифт
        style.setFont(font);

        row.getCell(0).setCellStyle(style);
    }

    /**
     * Установка настроек служебной информации
     *
     * @param workbook - exсel файл
     * @param row      - строка для установки стиля
     */
    private static void setUpFooterStyle(Workbook workbook, Row row) {
        // создаем стиль для ячейки
        CellStyle style = workbook.createCellStyle();
        //Вырванивание текста по правой стороне
        style.setAlignment(HSSFCellStyle.ALIGN_LEFT);

        // создаем шрифт
        Font font = workbook.createFont();
        // указываем, что хоти его видеть жирным
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        font.setFontHeightInPoints(settings.getFooterFontSize());
        font.setFontName(settings.getFontName());
        // применяем к стилю шрифт
        style.setFont(font);

        row.getCell(0).setCellStyle(style);
        if (row.getCell(2) != null) {
            row.getCell(2).setCellStyle(style);
        }
    }

    /**
     * Установка настрроек служебной информации
     *
     * @param workbook - exсel файл
     * @param row      - строка для установки стиля
     */
    private static void setUpTitleStyle(Workbook workbook, Row row) {
        // создаем стиль для ячейки
        CellStyle style = workbook.createCellStyle();
        //Вырванивание текста по центру
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        // создаем шрифт
        Font font = workbook.createFont();
        // указываем, что хотим его видеть жирным
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints(settings.getTitleTableFontSize());
        font.setFontName(settings.getFontName());
        // применяем к стилю шрифт
        style.setFont(font);
        //Установка переноса
        style.setWrapText(true);
        row.getCell(0).setCellStyle(style);
    }

    /**
     * Установка настроек для заголовка таблицы
     *
     * @param workbook - exсel файл
     * @param row      - строка для установки стиля
     */
    private static void setUpHeaderTableStyle(Workbook workbook, Row row) {
        // создаем стиль для ячейки
        CellStyle style = workbook.createCellStyle();
        // создаем шрифт
        Font font = workbook.createFont();
        // указываем, что хотим его видеть жирным
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints(settings.getTableHeadFontSize());
        font.setFontName(settings.getFontName());
        // применяем к стилю шрифт
        style.setFont(font);
        //применяем к стилю вырванивание текста по центру
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        //Создание рамки
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);

        //Установка переноса
        style.setWrapText(true);
        // проходим по всем ячейкам этой строки
        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
            // применяем созданный выше стиль к каждой ячейке
            row.getCell(i).setCellStyle(style);
        }
    }

    /**
     * Установка настроек для нумерации заголовка таблицы
     *
     * @param workbook - exсel файл
     * @param row      - строка для установки стиля
     */
    private static void setUpNumericTableStyle(Workbook workbook, Row row) {
        // создаем стиль для ячейки
        CellStyle style = workbook.createCellStyle();
        // создаем шрифт
        Font font = workbook.createFont();
        // указываем, что хотим его видеть жирным
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints(settings.getTableHeadFontSize());
        font.setFontName(settings.getFontName());
        // применяем к стилю шрифт
        style.setFont(font);

        //применяем к стилю вырванивание текста по центру
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        //Создание рамки
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        // проходим по всем ячейкам этой строки
        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
            // применяем созданный выше стиль к каждой ячейке
            row.getCell(i).setCellStyle(style);
        }
    }

}
