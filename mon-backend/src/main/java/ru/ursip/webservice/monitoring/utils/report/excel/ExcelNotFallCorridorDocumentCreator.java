package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report.excel;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.ResourcePeriod;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.ResourceSection;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.report.CompanyReport;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.report.ReportSettings;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.report.ReportTableHeadColumn;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report.ReportConfiguration;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report.ReportDocTypeProperties;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report.RestData;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Формирование Excel - документа о данных по материалам, которые не попали в установленный коридор роста/снижения цен
 *
 * @author samsonov
 * @since 15.03.2017
 */
public class ExcelNotFallCorridorDocumentCreator {

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
     * @param currentResourcesPeriod    - список ресурсов в текущем периоде
     * @param firstPrevResourcesPeriod  - список ресурсов в первом предидущем периоде
     * @param secondPrevResourcesPeriod - список ресурсов во втором предидущем периоде
     * @param thirdPrevResourcesPeriod  - список ресурсов в третьемпредидущем периоде
     * @param sections                  - список разделов
     * @param docType                   - тип отчета
     * @return - excel отчет
     */
    public static Workbook createNewExcelDocument(List<ResourcePeriod> currentResourcesPeriod,
                                                  List<ResourcePeriod> firstPrevResourcesPeriod,
                                                  List<ResourcePeriod> secondPrevResourcesPeriod,
                                                  List<ResourcePeriod> thirdPrevResourcesPeriod,
                                                  List<ResourceSection> sections,
                                                  ReportDocTypeProperties docType) throws IOException {


        for (CompanyReport currentCompany : RestData.getCompanys()) {
            companys.put(currentCompany.getKod(), currentCompany.getNazvanie());
        }
        resultExcel = new SXSSFWorkbook();
        // Получаем настройки отчета
        settings = ReportConfiguration.getReportSettings(docType);
        maxTableColumn = settings.getTableHeadColumns().size();
        // Формируем строку шапки таблицы с информацией о периодах
        List<ReportTableHeadColumn> reportTableHeadColumn = setTableHead(currentResourcesPeriod, firstPrevResourcesPeriod, secondPrevResourcesPeriod, thirdPrevResourcesPeriod);
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

            //Создаём фильтры на весь диапазон значений таблицы
            sheet.setAutoFilter(CellRangeAddress.valueOf("A" +
                    String.valueOf(6) + ":T" +
                    String.valueOf(6 + currentResourcesPeriod.size())));

            //Заполнение данными
            for (ResourceSection currentSections : sections) {
                Row resourceSectionRow = createTableSectionRow(++rowNum, currentSections);
                //Установка стилей для строки информации о ресурсы в периоде
                setUpSectionInfoStyle(resultExcel, resourceSectionRow);
                rowNum = resourceSectionRow.getRowNum();
                for (ResourcePeriod resourcePeriod : currentResourcesPeriod) {
                    if (resourcePeriod.getFkResource().getResourceSection().getId() == currentSections.getId()) {
                        ResourcePeriod firstPrevResourcePeriod = null;
                        ResourcePeriod secondPrevResourcePeriod = null;
                        ResourcePeriod thirdPrevResourcePeriod = null;

                        firstPrevResourcePeriod = findResourceByLastPeriod(firstPrevResourcesPeriod, resourcePeriod);
                        secondPrevResourcePeriod = findResourceByLastPeriod(secondPrevResourcesPeriod, resourcePeriod);
                        thirdPrevResourcePeriod = findResourceByLastPeriod(thirdPrevResourcesPeriod, resourcePeriod);

                        Row resourcePeriodRow = createTableRow(++rowNum, resourcePeriod, firstPrevResourcePeriod,
                                secondPrevResourcePeriod, thirdPrevResourcePeriod, ++countResourcePeriod);
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

        //Подбор ширины столбцов по самому длинному элементу в таблице
        setColumnWidth();
        return resultExcel;
    }

    /**
     * Формирование шапки таблицы
     *
     * @param currentResourcesPeriod -currentResourcesPeriod
     * @param firstPrevResourcesPeriod -firstPrevResourcesPeriod
     * @param secondPrevResourcesPeriod -secondPrevResourcesPeriod
     * @param thirdPrevResourcesPeriod -thirdPrevResourcesPeriod
     * @return - reportTableHeadColumn
     */
    private static List<ReportTableHeadColumn> setTableHead(List<ResourcePeriod> currentResourcesPeriod, List<ResourcePeriod> firstPrevResourcesPeriod,
                                                            List<ResourcePeriod> secondPrevResourcesPeriod, List<ResourcePeriod> thirdPrevResourcesPeriod) {
        List<ReportTableHeadColumn> reportTableHeadColumn = settings.getTableHeadColumns();
        if (thirdPrevResourcesPeriod != null & !thirdPrevResourcesPeriod.isEmpty()) {
            Calendar thirdPrevPeriodCalendar = Calendar.getInstance();
            thirdPrevPeriodCalendar.setTime(thirdPrevResourcesPeriod.get(0).getFkPeriod().getDateFrom());
            ReportTableHeadColumn thirdPrevPeriodColumn = reportTableHeadColumn.get(8);
            String thirdPrevPeriodStringName = thirdPrevResourcesPeriod.get(0).getFkPeriod().getName() + " " +
                    thirdPrevPeriodCalendar.get(Calendar.YEAR) + " год";
            thirdPrevPeriodColumn.getRows().set(1, thirdPrevPeriodStringName);
            reportTableHeadColumn.set(8, thirdPrevPeriodColumn);
        }
        if (secondPrevResourcesPeriod != null & !secondPrevResourcesPeriod.isEmpty()) {
            Calendar secondPrevPeriodCalendar = Calendar.getInstance();
            secondPrevPeriodCalendar.setTime(secondPrevResourcesPeriod.get(0).getFkPeriod().getDateFrom());
            ReportTableHeadColumn secondPrevPeriodColumn = reportTableHeadColumn.get(9);
            String secondPrevPeriodStringName = secondPrevResourcesPeriod.get(0).getFkPeriod().getName() + " " +
                    secondPrevPeriodCalendar.get(Calendar.YEAR) + " год";
            secondPrevPeriodColumn.getRows().set(1, secondPrevPeriodStringName);
            reportTableHeadColumn.set(9, secondPrevPeriodColumn);
        }
        if (firstPrevResourcesPeriod != null & !firstPrevResourcesPeriod.isEmpty()) {
            Calendar firstPrevPeriodCalendar = Calendar.getInstance();
            firstPrevPeriodCalendar.setTime(firstPrevResourcesPeriod.get(0).getFkPeriod().getDateFrom());
            ReportTableHeadColumn firstPrevPeriodColumn = reportTableHeadColumn.get(10);
            String firstPrevPeriodStringName = firstPrevResourcesPeriod.get(0).getFkPeriod().getName() + " " +
                    firstPrevPeriodCalendar.get(Calendar.YEAR) + " год";
            firstPrevPeriodColumn.getRows().set(1, firstPrevPeriodStringName);
            reportTableHeadColumn.set(10, firstPrevPeriodColumn);
            // Установка заголовка с ценой прошлого периода
            ReportTableHeadColumn firstPrevPricePeriodColumn = reportTableHeadColumn.get(13);
//            String prevPricePeriodTitle = firstPrevPricePeriodColumn.getRows().get(0);
            firstPrevPricePeriodColumn.getRows().set(0, "Отпускная цена за " + firstPrevResourcesPeriod.get(0).getFkPeriod().getName());
            reportTableHeadColumn.set(13, firstPrevPricePeriodColumn);
            // Установка заголовка с поставщиком прошлого периода
            ReportTableHeadColumn firstPrevCompanyPeriodColumn = reportTableHeadColumn.get(15);
//            String firstPrevPeriodTitle = firstPrevCompanyPeriodColumn.getRows().get(0);
            firstPrevCompanyPeriodColumn.getRows().set(0, "Организация поставщик " + firstPrevPeriodStringName);
            reportTableHeadColumn.set(15, firstPrevCompanyPeriodColumn);
        }
        if (!currentResourcesPeriod.isEmpty()) {
            Calendar currentPeriodCalendar = Calendar.getInstance();
            currentPeriodCalendar.setTime(currentResourcesPeriod.get(0).getFkPeriod().getDateFrom());
            ReportTableHeadColumn currentPeriodColumn = reportTableHeadColumn.get(11);
            String currentPeriodStringName = currentResourcesPeriod.get(0).getFkPeriod().getName() + " " + currentPeriodCalendar.get(Calendar.YEAR) + " год";
            currentPeriodColumn.getRows().set(1, currentPeriodStringName);
            reportTableHeadColumn.set(11, currentPeriodColumn);
            //установка заголовка с поставщиком текущего периода
            ReportTableHeadColumn currentCompanyPeriodColumn = reportTableHeadColumn.get(16);
//            String currentPeriodTitle = currentCompanyPeriodColumn.getRows().get(0);
            currentCompanyPeriodColumn.getRows().set(0, "Организация поставщик " + currentPeriodStringName);
            reportTableHeadColumn.set(16, currentCompanyPeriodColumn);
        }
        return reportTableHeadColumn;
    }

    /**
     * Установка щириы колонок
     */
    private static void setColumnWidth() {
        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 2500);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 10000);
        sheet.setColumnWidth(5, 2000);
        sheet.setColumnWidth(6, 2000);
        sheet.setColumnWidth(7, 2000);
        sheet.setColumnWidth(8, 3000);
        sheet.setColumnWidth(9, 3000);
        sheet.setColumnWidth(10, 3000);
        sheet.setColumnWidth(11, 3000);
        sheet.setColumnWidth(12, 3000);
        sheet.setColumnWidth(13, 5000);
        sheet.setColumnWidth(14, 5000);

        sheet.setColumnWidth(15, 5000);
        sheet.setColumnWidth(16, 5000);
        sheet.setColumnWidth(18, 5000);
        sheet.setColumnWidth(19, 3300);
        sheet.setColumnWidth(20, 5000);
        sheet.setColumnWidth(21, 3300);
        sheet.setColumnWidth(22, 3300);
        sheet.setColumnWidth(23, 3300);
        sheet.setColumnWidth(24, 3300);

        sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
        sheet.getPrintSetup().setLandscape(true);
        sheet.getPrintSetup().setScale((short) 50);
    }

    /**
     * Поиск ресурса в прошлом периоде
     *
     * @param PrevResourcesPeriod -PrevResourcesPeriod
     * @param resourcePeriod      - resourcePeriod
     * @return - resource
     */

    private static ResourcePeriod findResourceByLastPeriod(List<ResourcePeriod> PrevResourcesPeriod, ResourcePeriod resourcePeriod) {
        if (PrevResourcesPeriod != null) {
            // Поиск ресурса в первом предидущем периоде
            for (ResourcePeriod firstPrevRP : PrevResourcesPeriod) {
                if (resourcePeriod.getFkResource().getId().equals(firstPrevRP.getFkResource().getId()))
                    if (resourcePeriod.getFkCompany().equals(firstPrevRP.getFkCompany())) {
                        return firstPrevRP;
                    }
                if (resourcePeriod.getFkResource().getLastId() != null)
                    if (resourcePeriod.getFkResource().getLastId() != 0)
                        if (resourcePeriod.getFkResource().getLastId().equals(firstPrevRP.getFkResource().getId()))
                            if (resourcePeriod.getFkCompany().equals(firstPrevRP.getFkCompany())) {
                                return firstPrevRP;
                            }
            }
        }
        return null;
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
                        if (rowN < rowCount && settings.getTableHeadColumns().
                                get(j).
                                getRows().
                                get(rowN).equals("") &&
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
     * @param rowNum                   - номер последней заполненной строки
     * @param currentResourcePeriod    - информация о ресурсе в текущем периоде
     * @param firstPrevResourcePeriod  - информация о ресурсе в первом прошлом периоде
     * @param secondPrevResourcePeriod - информация о ресурсе во втором прошлом периоде
     * @param thirdPrevResourcePeriod  - информация о ресурсе в третьем прошлом периоде
     * @param countResourcePeriod      - счетчик ресурсов в периоде в таблице
     * @return - строка с информацией о ресурсы в периоде
     */
    private static Row createTableRow(int rowNum, ResourcePeriod currentResourcePeriod,
                                      ResourcePeriod firstPrevResourcePeriod, ResourcePeriod secondPrevResourcePeriod,
                                      ResourcePeriod thirdPrevResourcePeriod, int countResourcePeriod) {
        Row infoRow = sheet.createRow(rowNum);
        for (int i = 0; i < maxTableColumn; i++) {
            infoRow.createCell(i).setCellValue("-");
        }
        infoRow.getCell(0).setCellValue(countResourcePeriod);
        // Цена ресурса в текущем периоде в рублях
        double currentRublPrice = 0d;
        if (ObjectUtils.notEqual(currentResourcePeriod.getFkResource(), null)) {
            if (ObjectUtils.notEqual(currentResourcePeriod.getFkResource().getCodeTSN(), null)) {
                infoRow.getCell(1).setCellValue(currentResourcePeriod.getFkResource().getCodeTSN());
            }


            if (ObjectUtils.notEqual(currentResourcePeriod.getFkResource().getCodeOKP(), null)) {
                infoRow.getCell(2).setCellValue(currentResourcePeriod.getFkResource().getCodeOKP());
            }
            if (ObjectUtils.notEqual(currentResourcePeriod.getFkResource().getCodeOKPD(), null)) {
                infoRow.getCell(3).setCellValue(currentResourcePeriod.getFkResource().getCodeOKPD());
            }

            if (ObjectUtils.notEqual(currentResourcePeriod.getFkResource().getName(), null)) {
                infoRow.getCell(4).setCellValue(currentResourcePeriod.getFkResource().getName());
            }
            if (ObjectUtils.notEqual(currentResourcePeriod.getFkResource().getMeasure(), null)) {
                if (ObjectUtils.notEqual(currentResourcePeriod.getFkResource().getMeasure().getCode(), null)) {
                    infoRow.getCell(5).setCellValue(currentResourcePeriod.getFkResource().getMeasure().getCode());
                }
            }
            if (ObjectUtils.notEqual(currentResourcePeriod.getFkResource().getWeightNet(), null)) {
                infoRow.getCell(6).setCellValue(currentResourcePeriod.getFkResource().getWeightNet());
            }
            if (ObjectUtils.notEqual(currentResourcePeriod.getFkResource().getWeightGross(), null)) {
                infoRow.getCell(7).setCellValue(currentResourcePeriod.getFkResource().getWeightGross());
            }
            // Заполнение столбца с ценой ресурса в текущем периоде
            if (ObjectUtils.notEqual(currentResourcePeriod.getChangePrimary(), null)) {
                currentRublPrice = currentResourcePeriod.getChangePrimary() / 100d;
                infoRow.getCell(11).setCellValue(currentRublPrice);
//            } else {
//                if (ObjectUtils.notEqual(currentResourcePeriod.getPricePrimary(), null)) {
//                    currentRublPrice = currentResourcePeriod.getPricePrimary() / 100d;
//                    infoRow.getCell(11).setCellValue(currentRublPrice);
//                }
            }

            if (ObjectUtils.notEqual(currentResourcePeriod.getFkCompany(), null)) {
                infoRow.getCell(16).setCellValue(companys.get(currentResourcePeriod.getFkCompany()));
            }

            if (ObjectUtils.notEqual(currentResourcePeriod.getManufacturer(), null)) {
                infoRow.getCell(17).setCellValue(currentResourcePeriod.getManufacturer());
            }

            if (ObjectUtils.notEqual(currentResourcePeriod.getFkResource().getNote(), null)) {
                infoRow.getCell(18).setCellValue(currentResourcePeriod.getFkResource().getNote());
            }

            if (ObjectUtils.notEqual(currentResourcePeriod.getIsTransportCosts(), null)) {
                if (currentResourcePeriod.getIsTransportCosts()) infoRow.getCell(19).setCellValue("включена");
            }

            /*if (ObjectUtils.notEqual(currentResourcePeriod.getFkResource().getNote(), null)) {
                infoRow.getCell(20).setCellValue(currentResourcePeriod.getIsTransportCosts());
            }
*/

            if (currentResourcePeriod.getFkResource().getResourceType().getId() == 4)
                infoRow.getCell(21).setCellValue("ежемесячно");
            else infoRow.getCell(21).setCellValue("ежеквартально");

            if (ObjectUtils.notEqual(currentResourcePeriod.getIsStatus(), null))
                if (currentResourcePeriod.getIsStatus()) infoRow.getCell(22).setCellValue("да");
                else infoRow.getCell(22).setCellValue("нет");
            if (ObjectUtils.notEqual(currentResourcePeriod.getRemarkChief(), null))
                infoRow.getCell(23).setCellValue(currentResourcePeriod.getRemarkChief());
            if (ObjectUtils.notEqual(currentResourcePeriod.getRemarkLeader(), null))
                infoRow.getCell(24).setCellValue(currentResourcePeriod.getRemarkLeader());
            /*if (ObjectUtils.notEqual(currentResourcePeriod.getPricePrimary(), null)) {
                //       infoRow.getCell(13).setCellValue(currentResourcePeriod.getPricePrimary() / 100d);
                infoRow.getCell(11).setCellValue(currentRublPrice);
            }*/
        }

        // Заполнение столбцов информацией о третьем предидущем периодом
        double thirdPrevRublPrice = 0d;
        if (ObjectUtils.notEqual(secondPrevResourcePeriod, null)) {
            // Заполнение столбца с ценой ресурса в третьем предидущем периоде
            if (ObjectUtils.notEqual(secondPrevResourcePeriod.getMainPrice(), null)) {
                thirdPrevRublPrice = secondPrevResourcePeriod.getMainPrice() / 100d;
                infoRow.getCell(8).setCellValue(thirdPrevRublPrice);
            }
        }
        /// Заполнение столбцов информацией о втором предидущем периодом
        double secondPrevRublPrice = 0d;
        if (ObjectUtils.notEqual(firstPrevResourcePeriod, null)) {
            // Заполнение столбца с ценой ресурса во втором предидущем периоде
            if (ObjectUtils.notEqual(firstPrevResourcePeriod.getMainPrice(), null)) {
                secondPrevRublPrice = firstPrevResourcePeriod.getMainPrice() / 100d;
                infoRow.getCell(9).setCellValue(secondPrevRublPrice);
            }
        }
        // Заполнение столбцов информацией о первом предидущем периодом
        double firstPrevRublPrice = 0d;
        if (ObjectUtils.notEqual(firstPrevResourcePeriod, null)) {
            if (ObjectUtils.notEqual(firstPrevResourcePeriod.getFkCompany(), null)) {
                infoRow.getCell(15).setCellValue(companys.get(firstPrevResourcePeriod.getFkCompany()));
            }
            // Заполнение столбца с ценой ресурса в первом предидущем периоде
            if (ObjectUtils.notEqual(currentResourcePeriod.getMainPrice(), null)) {
                firstPrevRublPrice = currentResourcePeriod.getMainPrice() / 100d;
                infoRow.getCell(10).setCellValue(firstPrevRublPrice);
            }
            if (ObjectUtils.notEqual(currentResourcePeriod.getMainPrice(), null)) {
                firstPrevRublPrice = currentResourcePeriod.getMainPrice() / 100d;
                infoRow.getCell(13).setCellValue(firstPrevRublPrice);
            }
        }
        // Заполнение столбца с отклонением
        if (firstPrevRublPrice != 0d) {
            //  double currentDelta = Math.abs(((firstPrevRublPrice - currentRublPrice) / firstPrevRublPrice) * 100);
            double currentDelta = ((firstPrevRublPrice - currentRublPrice) / firstPrevRublPrice) * 100;
            //  double currentDelta = Math.abs(((currentResourcePeriod.getChangePrimary() - currentResourcePeriod.getMainPrice()) /  currentResourcePeriod.getMainPrice()) * 100);
            currentDelta = new BigDecimal(currentDelta).setScale(0, RoundingMode.HALF_UP).doubleValue();
            //if (currentDelta == 100d) currentDelta = 0d;
            currentDelta = currentDelta * (-1);

            infoRow.getCell(12).setCellValue(currentDelta);
            infoRow.getCell(14).setCellValue(currentDelta);

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
        if ((ObjectUtils.notEqual(resourceSection.getName(), null)) &&
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
            row.getCell(i).setCellStyle(leftTableInfoStyle);
        }
        row.getCell(0).setCellStyle(centerTableInfoStyle);
        row.getCell(5).setCellStyle(centerTableInfoStyle);
        row.getCell(6).setCellStyle(rightTableInfoStyle);
        row.getCell(7).setCellStyle(rightTableInfoStyle);
        row.getCell(8).setCellStyle(rightTableInfoStyle);
        row.getCell(9).setCellStyle(rightTableInfoStyle);
        row.getCell(10).setCellStyle(rightTableInfoStyle);
        row.getCell(11).setCellStyle(rightTableInfoStyle);
        row.getCell(12).setCellStyle(rightTableInfoStyle);
        row.getCell(13).setCellStyle(rightTableInfoStyle);
        row.getCell(14).setCellStyle(rightTableInfoStyle);
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
