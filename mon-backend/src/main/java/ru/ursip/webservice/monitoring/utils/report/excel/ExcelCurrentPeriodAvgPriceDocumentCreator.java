package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report.excel;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.Period;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.Resource;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.ResourceSection;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.report.ReportSettings;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.report.ReportTableHeadColumn;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report.ReportConfiguration;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report.ReportDocTypeProperties;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Формирование Excel - документа содерржащие средние цены по ресурсам в периоде
 *
 * @author samsonov
 * @since 15.03.2017
 */
public class ExcelCurrentPeriodAvgPriceDocumentCreator {

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
     * Основной стиль ячеек в таблице
     */
    private static CellStyle tableInfoStyle = null;

    /**
     * Дополнительный стиль ячеек в таблице
     */
    private static CellStyle extraTableInfoStyle = null;

    /**
     * Дополнительный стиль ячеек в таблице для колонки ТСН
     */
    private static CellStyle TSNTableInfoStyle = null;
    /**
     * Дополнительный стиль ячеек в таблице для колонки price
     */
    private static CellStyle PriceTableInfoStyle = null;

    /**
     * Создание отчета
     *
     * @param resourcesPeriod - список ресурсов в периоде со средним значением цен
     * @param sections        - список разделов
     * @param period          - период
     * @param docType         - тип отчета
     * @return - excel отчет
     */
    public static Workbook createNewExcelDocument(Map<Resource, Double> resourcesPeriod,
                                                  List<ResourceSection> sections,
                                                  Period period,
                                                  ReportDocTypeProperties docType) throws IOException {
        // создание результирующего report файла в памяти
//        resultExcel = new HSSFWorkbook();
        resultExcel = new SXSSFWorkbook();
        // Получаем настройки отчета
        settings = ReportConfiguration.getReportSettings(docType);
        List<ReportTableHeadColumn> reportTableHeadColumn = settings.getTableHeadColumns();
        maxTableColumn = reportTableHeadColumn.size();

        // Добавляем в заголовок таблицы информацию о текущем периоде
        List<String> reportTableTitle = settings.getTitleList();
        int lastTitleIndex = reportTableTitle.size() - 1;
        // Информация о периоде

        // Формируем строку с информацией о периоде
        Calendar cal = Calendar.getInstance();
        cal.setTime(period.getDateTill());
        String lastTitleStringRow = reportTableTitle.get(lastTitleIndex) + " " +
                period.getName() + " " + cal.get(Calendar.YEAR) + " год";
        reportTableTitle.remove(lastTitleIndex);
        reportTableTitle.add(lastTitleStringRow);
        settings.setTitleList(reportTableTitle);

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
            tableInfoStyle = resultExcel.createCellStyle();
            setUpTableInfoStyle(resultExcel);
            extraTableInfoStyle = resultExcel.createCellStyle();
            TSNTableInfoStyle = resultExcel.createCellStyle();
            PriceTableInfoStyle=resultExcel.createCellStyle();
            setUpExtraTableInfoStyle(resultExcel);
            setUpTsnTableInfoStyle(resultExcel);
            setUpPriceTableInfoStyle(resultExcel);
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

            //Счетчик записи цен поставщиков
            int countResourcePeriod = 0;
              List<Resource> list=new ArrayList<Resource>();
            for (Resource resource : resourcesPeriod.keySet()) {
                list.add(resource);
            }


                List<Resource> sortedList = list.stream()
                    .sorted(
                            (e1, e2) -> {
                                String[] s1 = e1.getCodeTSN().split("\\.");
                                String[] s3 = e2.getCodeTSN().split("\\.");
                                if ((e1.getCodeTSN().contains("."))&
                                        (e2.getCodeTSN().contains("."))){
                                    String[] s2 = s1[1].split("\\-");
                                    String[] s4 = s3[1].split("\\-");

                                    int result;

                                    result = s1[0].compareTo(s3[0]);
                                    if (result != 0) return result;

                                    result = s2[0].compareTo(s4[0]);
                                    if (result != 0) return result;

                                    result = s2[1].compareTo(s4[1]);
                                    if (result != 0) return result;

                                    return Integer.valueOf(s2[2]).compareTo(Integer.valueOf(s4[2]));
                                }
                                else{
                                    return Integer.valueOf(s1[0]).compareTo(Integer.valueOf(s3[0]));
                                }
                            }
                    )
                    .collect(Collectors.toList());

            for (ResourceSection currentSections : sections) {
                Row resourceSectionRow = createTableSectionRow(++rowNum, currentSections);
                //Установка стилей для строки информации о ресурсы в периоде
                setUpSectionInfoStyle(resultExcel, resourceSectionRow);
                rowNum = resourceSectionRow.getRowNum();
                for (Resource resource : sortedList) {
                    if (resource.getResourceSection().getId() == currentSections.getId()) {
                        Row resourcePeriodRow = createTableRow(++rowNum, resource, resourcesPeriod.get(resource), ++countResourcePeriod);
                        //Установка стилей для строки информации о ресурсы в периоде
                        setUpResourcePeriodInfoStyle(resourcePeriodRow);
                        rowNum = resourcePeriodRow.getRowNum();
                    }
                }
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
        sheet.setColumnWidth(5, 3000);
        sheet.setColumnWidth(6, 3000);
        sheet.setColumnWidth(7, 3000);
        sheet.setColumnWidth(8, 3000);
        sheet.setColumnWidth(9, 3000);


        sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
        sheet.getPrintSetup().setLandscape(true);
        sheet.getPrintSetup().setScale((short) 100);


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
     * @return - строки заголовка
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
     * @param rowNum              - номер последней заполненной строки
     * @param resource            - информация о ресурсе в периоде
     * @param avgValue            - средняя цена для ресурса
     * @param countResourcePeriod - счетчик ресурсов в периоде в таблице
     * @return - строка с информацией о ресурсы в периоде
     */
    private static Row createTableRow(int rowNum,
                                      Resource resource, Double avgValue, int countResourcePeriod) {
        Row infoRow = sheet.createRow(rowNum);
        for (int i = 0; i < maxTableColumn; i++) {
            infoRow.createCell(i).setCellValue("");
        }
        infoRow.getCell(0).setCellValue(countResourcePeriod);
        double currentRublPrice = 0d;
        if (ObjectUtils.notEqual(resource, null)) {
            if (ObjectUtils.notEqual(resource.getCodeTSN(), null)) {
                infoRow.getCell(1).setCellValue(resource.getCodeTSN());
            }
            if (ObjectUtils.notEqual(resource.getCodeOKP(), null)) {
                infoRow.getCell(2).setCellValue(resource.getCodeOKP());
            }
            if (ObjectUtils.notEqual(resource.getCodeOKPD(), null)) {
                infoRow.getCell(3).setCellValue(resource.getCodeOKPD());
            }
            if (ObjectUtils.notEqual(resource.getName(), null)) {
                infoRow.getCell(4).setCellValue(resource.getName());
            }
            if (ObjectUtils.notEqual(resource.getMeasure(), null)) {
                if (ObjectUtils.notEqual(resource.getMeasure().getCode(), null)) {
                    infoRow.getCell(5).setCellValue(resource.getMeasure().getCode());
                }
            }
             if (ObjectUtils.notEqual(avgValue, null)) {
                currentRublPrice = avgValue / 100d;
                infoRow.getCell(6).setCellValue(new BigDecimal(currentRublPrice).setScale(2, RoundingMode.HALF_UP).doubleValue());
            }

        }
        return infoRow;
    }


    /**
     * Формирование строки с информацией о разделе
     *
     * @param rowNum          - номер последней заполненной строки
     * @param resourceSection - информация о разделе
     * @return - строка с информацией о разделе
     */
    private static Row createTableSectionRow(int rowNum,
                                             ResourceSection resourceSection) {
        Row infoRow = sheet.createRow(rowNum);
        for (int i = 0; i < maxTableColumn; i++) {
            infoRow.createCell(i).setCellValue("");
        }
        if ((ObjectUtils.notEqual(resourceSection.getName(), null))&&
                (ObjectUtils.notEqual(resourceSection.getSectionRoot(), null))) {
            infoRow.getCell(4).setCellValue(resourceSection.getName());
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
            row.getCell(i).setCellStyle(tableInfoStyle);
        }
        row.getCell(0).setCellStyle(extraTableInfoStyle);
        row.getCell(5).setCellStyle(TSNTableInfoStyle);
        row.getCell(6).setCellStyle(PriceTableInfoStyle);
    }

    /**
     * Установка настроек для информации о ресурсе в периоде
     *
     * @param workbook - exсel файл
     */
    private static void setUpTableInfoStyle(Workbook workbook) {
        // создаем шрифт
        Font font = workbook.createFont();
        // указываем, что хотим его видеть жирным
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        font.setFontHeightInPoints(settings.getTableHeadFontSize());
        font.setFontName(settings.getFontName());
        // применяем к стилю шрифт
        tableInfoStyle.setFont(font);
        //применяем к стилю вырванивание текста по центру
        tableInfoStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        tableInfoStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        //Создание рамки
        tableInfoStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        tableInfoStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        tableInfoStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        tableInfoStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);

        //Установка переноса
        tableInfoStyle.setWrapText(true);

    }

    /**
     * Установка настроек для едениц изменерия ТСН
     *
     * @param workbook - exсel файл
     */


    /**
     * Установка настроек для информации о ресурсе в периоде
     *
     * @param workbook - exсel файл
     */
    private static void setUpExtraTableInfoStyle(Workbook workbook) {

        // создаем шрифт
        Font font = workbook.createFont();
        // указываем, что хотим его видеть жирным
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        font.setFontHeightInPoints(settings.getTableHeadFontSize());
        font.setFontName(settings.getFontName());
        // применяем к стилю шрифт
        extraTableInfoStyle.setFont(font);
        //применяем к стилю вырванивание текста по центру
        extraTableInfoStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        extraTableInfoStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        //Создание рамки
        extraTableInfoStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        extraTableInfoStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        extraTableInfoStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        extraTableInfoStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);

        //Установка переноса
        extraTableInfoStyle.setWrapText(true);
    }

    /**
     * Установка настроек для информации о ед изм ТСН
     *
     * @param workbook - exсel файл
     */
    private static void setUpTsnTableInfoStyle(Workbook workbook) {

        // создаем шрифт
        Font font = workbook.createFont();
        // указываем, что хотим его видеть жирным
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        font.setFontHeightInPoints(settings.getTableHeadFontSize());
        font.setFontName(settings.getFontName());
        // применяем к стилю шрифт
        TSNTableInfoStyle.setFont(font);
        //применяем к стилю вырванивание текста по центру
        TSNTableInfoStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        TSNTableInfoStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        //Создание рамки
        TSNTableInfoStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        TSNTableInfoStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        TSNTableInfoStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        TSNTableInfoStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);

        //Установка переноса
        TSNTableInfoStyle.setWrapText(true);
    }

    /**
     * Установка настроек для информации о цене
     *
     * @param workbook - exсel файл
     */
    private static void setUpPriceTableInfoStyle(Workbook workbook) {

        // создаем шрифт
        Font font = workbook.createFont();
        // указываем, что хотим его видеть жирным
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        font.setFontHeightInPoints(settings.getTableHeadFontSize());
        font.setFontName(settings.getFontName());
        // применяем к стилю шрифт
        PriceTableInfoStyle.setFont(font);
        //применяем к стилю вырванивание текста по центру
        PriceTableInfoStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        PriceTableInfoStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        //Создание рамки
        PriceTableInfoStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        PriceTableInfoStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        PriceTableInfoStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        PriceTableInfoStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);

        //Установка переноса
        PriceTableInfoStyle.setWrapText(true);
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
        style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
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
