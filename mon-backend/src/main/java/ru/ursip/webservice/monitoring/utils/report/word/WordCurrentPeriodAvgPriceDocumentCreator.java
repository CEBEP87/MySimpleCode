package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report.word;


import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.Period;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.Resource;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.ResourceSection;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.report.ReportSettings;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.report.ReportTableHeadColumn;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report.ReportConfiguration;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report.ReportDocTypeProperties;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Формирование Word - документа
 *
 * @author samsonov
 * @since 26.06.2017
 */
public class WordCurrentPeriodAvgPriceDocumentCreator {

    /**
     * Настройки отчета
     */
    private static ReportSettings settings = null;

    /**
     * Результирующий Word - документ
     */
    private static XWPFDocument resultDoc = null;

    /**
     * Строка параграфа в ячейке таблицы
     */
    private static XWPFRun tableStroke = null;

    /**
     * Список параграфов таблицы
     */
    private static List<XWPFParagraph> tableParagraphList = null;

    /**
     * Параграф в таблице
     */
    private static XWPFParagraph tableParagraph = null;

    /**
     * CTPPr
     */
    private static CTPPr tablePpr = null;

    /**
     * CTSpacing
     */
    private static CTSpacing tableSpacing = null;

    /**
     * Содержимое ячейки таблицы
     */
    private static StringBuilder tableCellText = new StringBuilder();


    /**
     * Создание отчета
     *
     * @param resourcesPeriod - список ресурсов в периоде со средним значением цен
     * @param sections        - список разделов
     * @param period          - период
     * @param docType         - тип отчета
     * @return - объект word документа
     */
    public static XWPFDocument createNewWordDocument(Map<Resource, Double> resourcesPeriod,
                                                     List<ResourceSection> sections,
                                                     Period period,
                                                     ReportDocTypeProperties docType) throws Exception {
        //Создание Word документа
        resultDoc = new XWPFDocument();
        XWPFStyles styles = resultDoc.createStyles();
        CTStyle ctStyle = CTStyle.Factory.newInstance();
        ctStyle.setStyleId("default");
        XWPFStyle style = new XWPFStyle(ctStyle);
        resultDoc.createStyles().addStyle(style);
        changeOrientation(resultDoc, "landscape");
        // Получение настроек для документа
        settings = ReportConfiguration.getReportSettings(docType);
        // Создание блока со служебной информацией
        createServiceBlock();
        // Создание блока с заголовком таблицы
        createTableTitleBlock();
        // Создание таблицы
        createTable(resourcesPeriod, sections, period);
        // Создание footer'a
        createFootereBlock();
        return resultDoc;
    }

    /**
     * Создание таблицы
     *
     * @param resourcesPeriod - список ресурсов в периоде со средним значением цен
     * @param sections        - список разделов
     * @param period          - период
     */
    private static void createTable(Map<Resource, Double> resourcesPeriod,
                                    List<ResourceSection> sections,
                                    Period period) {
        // Создание таблицы
        XWPFTable table = resultDoc.createTable();
        CTSectPr sectPr = resultDoc.getDocument().getBody().getSectPr();
        if (sectPr == null) return;
        CTPageSz pageSize = sectPr.getPgSz();
        if (pageSize == null) return;

        CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
        width.setType(STTblWidth.DXA);
        width.setW(BigInteger.valueOf(715 * 20));

        //Создание строки с заголовками столбцов
        createTableHeadRows(table);
        //Создание строки с нумерацией
        createNumericTableRow(table);
        // Пустая строка таблицы - отступ
        table.createRow();
        // Наполнение таблицы данными о ресурсах
        int index = 0;
        XWPFTableRow resourceInfoRow;
        XWPFTableRow sectionRow;
        for (ResourceSection currentSections : sections) {
            sectionRow = table.createRow();
            createResourceSectionRow(sectionRow, currentSections);
            for (Resource resource : resourcesPeriod.keySet()) {
                if (resource.getResourceSection().getId() == currentSections.getId()) {
                    resourceInfoRow = table.createRow();
                    createParagraphForCell(resourceInfoRow.getCell(0), ParagraphAlignment.LEFT).setText(String.valueOf(++index));
                    createResourcePeriodTableRow(resourceInfoRow, resource, resourcesPeriod.get(resource));
                    for (XWPFTableCell cell : resourceInfoRow.getTableCells()) {
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    }
                }
            }
        }
        resultDoc.createParagraph().createRun().addBreak();

    }

    /**
     * Создание параграфа для ячейки таблицы
     *
     * @param cell      - ячейка таблицы
     * @param alignment - выравнивание
     * @return - параграф для записи текста
     */
    private static XWPFRun createParagraphForCell(XWPFTableCell cell, ParagraphAlignment alignment) {
        tableParagraphList = null;
        tablePpr = null;
        tablePpr = null;
        tableSpacing = null;
        tableStroke = null;
        tableParagraphList = cell.getParagraphs();
        tableParagraph = tableParagraphList.get(0);
        tableParagraph.setAlignment(alignment);
        tableParagraph.setWordWrap(true);
        tablePpr = tableParagraph.getCTP().getPPr();
        if (tablePpr == null) tablePpr = tableParagraph.getCTP().addNewPPr();
        tableSpacing = tablePpr.isSetSpacing() ? tablePpr.getSpacing() : tablePpr.addNewSpacing();
        tableSpacing.setAfter(BigInteger.valueOf(100));
        tableSpacing.setBefore(BigInteger.valueOf(100));
        tableSpacing.setLineRule(STLineSpacingRule.AUTO);
        tableStroke = tableParagraph.createRun();
        tableStroke.setFontFamily(settings.getFontName());
        tableStroke.setFontSize((short) 7);
        return tableStroke;
    }

    /**
     * Создание блока, содержащего служебную информация
     */
    private static void createServiceBlock() {
        tableParagraph = null;
        if (settings.getServiceList() != null) {
            if (settings.getServiceList().size() > 0) {
                tableParagraph = resultDoc.createParagraph();
                tableParagraph.setAlignment(ParagraphAlignment.RIGHT);
//                tableParagraph.setWordWrap(true);
            }
            tableStroke = null;
            for (String serviceStr : settings.getServiceList()) {
                tableStroke = tableParagraph.createRun();
                tableStroke.setFontFamily(settings.getFontName());
                tableStroke.setFontSize((short) 7);
                tableStroke.setText(serviceStr);
                tableStroke.addBreak();
            }
            if (tableStroke != null) {
                tableStroke.removeBreak();
            }
        }
    }

    /**
     * Создание блока, содержащего информацию о заголовке таблицы
     */
    private static void createTableTitleBlock() {
        tableParagraph = null;
        if (settings.getTitleList().size() > 0) {
            tableParagraph = resultDoc.createParagraph();
            tableParagraph.setAlignment(ParagraphAlignment.CENTER);
            tableParagraph.setWordWrap(true);
        }
        tableStroke = null;
        for (String serviceStr : settings.getTitleList()) {
            tableStroke = tableParagraph.createRun();
            tableStroke.setBold(true);
            tableStroke.setFontFamily(settings.getFontName());
            tableStroke.setFontSize((short) 7);
            tableStroke.setText(serviceStr);
            tableStroke.addBreak();
        }
        if (tableStroke != null) {
            tableStroke.removeBreak();
        }
    }


    /**
     * Создание строк таблицы с заголовками столбцов
     *
     * @param table - таблица
     * @return - список строк таблицы
     */
    private static List<XWPFTableRow> createTableHeadRows(XWPFTable table) {
        List<XWPFTableRow> result = new ArrayList<>();
        tableParagraph = null;
        tableParagraphList = null;
        // Количество строк в шапке таблицы
        int countTableRows = 0;
        for (ReportTableHeadColumn currentColumn : settings.getTableHeadColumns()) {
            int currentColumnCountRows = currentColumn.getRows().size();
            if (countTableRows <= currentColumnCountRows) {
                countTableRows = currentColumnCountRows;
            }
        }
        for (int i = 0; i < countTableRows; i++) {
            // Строка таблицы
            XWPFTableRow tableHeadRow = table.getRow(i);
            for (int j = 0; j < settings.getTableHeadColumns().size(); j++) {
                if (j == 0) {
                    tableParagraphList = tableHeadRow.getCell(i).getParagraphs();
                    tableParagraph = tableParagraphList.get(0);
                } else {
                    tableParagraphList = tableHeadRow.addNewTableCell().getParagraphs();
                    tableParagraph = tableParagraphList.get(0);
                }
                tableParagraph.setAlignment(ParagraphAlignment.CENTER);
                tableParagraph.setWordWrap(true);

                tablePpr = null;
                tablePpr = tableParagraph.getCTP().getPPr();
                if (tablePpr == null) tablePpr = tableParagraph.getCTP().addNewPPr();
                tableSpacing = null;
                tableSpacing = tablePpr.isSetSpacing() ? tablePpr.getSpacing() : tablePpr.addNewSpacing();
                tableSpacing.setAfter(BigInteger.valueOf(200));
                tableSpacing.setBefore(BigInteger.valueOf(200));
                tableSpacing.setLineRule(STLineSpacingRule.EXACT);


                tableStroke = tableParagraph.createRun();
                tableStroke.setBold(true);
                tableStroke.setFontFamily(settings.getFontName());
                tableStroke.setFontSize((short) 7);
                tableStroke.setText(settings.getTableHeadColumns().get(j).getRows().get(i));
            }
            for (XWPFTableCell cell : tableHeadRow.getTableCells()) {
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            }
            result.add(tableHeadRow);
        }
        return result;
    }

    /**
     * Создание строки таблицы с нумерацией столбцов
     *
     * @param table - таблица
     */
    private static void createNumericTableRow(XWPFTable table) {
        XWPFTableRow row = table.createRow();
        tableParagraph = null;
        tableParagraphList = null;
        for (int i = 0; i < settings.getTableHeadColumns().size(); i++) {
            tableParagraphList = row.getCell(i).getParagraphs();
            tableParagraph = tableParagraphList.get(0);
            tableParagraph.setAlignment(ParagraphAlignment.CENTER);
            CTPPr ppr = tableParagraph.getCTP().getPPr();
            if (ppr == null) ppr = tableParagraph.getCTP().addNewPPr();
            CTSpacing spacing = ppr.isSetSpacing() ? ppr.getSpacing() : ppr.addNewSpacing();
            spacing.setAfter(BigInteger.valueOf(100));
            spacing.setBefore(BigInteger.valueOf(100));
            spacing.setLineRule(STLineSpacingRule.EXACT);

            tableStroke = tableParagraph.createRun();
            tableStroke.setFontFamily(settings.getFontName());
            tableStroke.setFontSize((short) 7);
            tableStroke.setText(Integer.toString(i + 1));
        }
        for (XWPFTableCell cell : row.getTableCells()) {
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        }
    }

    /**
     * Создание строки таблицы, содержащей информацию о разделе ресурса
     *
     * @param row             - строка таблицы
     * @param resourceSection - информация о разделе ресурса
     */
    private static void createResourceSectionRow(XWPFTableRow row, ResourceSection resourceSection) {
        if (ObjectUtils.notEqual(resourceSection, null)) {
            if (ObjectUtils.notEqual(resourceSection.getName(), null)) {
                createParagraphForCell(row.getCell(4), ParagraphAlignment.LEFT).setText(" " + resourceSection.getName());
            }
        }
    }

    /**
     * Создание строки таблицы, содержащей информацию о ресурсе
     *
     * @param row      - строка таблицы
     * @param resource - информация о ресурсе
     * @param avgPrice - среднее значение цены ресурса
     */
    private static void createResourcePeriodTableRow(XWPFTableRow row, Resource resource, Double avgPrice) {
        if (ObjectUtils.notEqual(resource, null)) {
            createArrow(resource.getCodeTSN(), 1, row, ParagraphAlignment.LEFT, tableCellText.append(resource.getCodeTSN()).toString());
            createArrow(resource.getCodeOKP(), 2, row, ParagraphAlignment.LEFT, tableCellText.append(resource.getCodeOKP()).toString());
            createArrow(resource.getCodeOKPD(), 3, row, ParagraphAlignment.LEFT, tableCellText.append(resource.getCodeOKPD()).toString());
            createArrow(resource.getName(), 4, row, ParagraphAlignment.LEFT, tableCellText.append(resource.getName()).toString());
            createArrow(resource.getWeightNet(), 6, row, ParagraphAlignment.LEFT, tableCellText.append(resource.getWeightNet()).toString());
            createArrow(resource.getWeightGross(), 7, row, ParagraphAlignment.LEFT, tableCellText.append(resource.getWeightGross()).toString());
            createArrow(avgPrice, 8, row, ParagraphAlignment.LEFT, tableCellText.append(avgPrice / 100).toString());

            if (ObjectUtils.notEqual(resource.getMeasure(), null)) {
                createArrow(resource.getMeasure().getCode(), 5, row, ParagraphAlignment.CENTER, tableCellText.append(resource.getMeasure().getCode()).toString());
            }

        }
    }

    /**
     * Наполнение строки
     *
     * @param ob        - object
     * @param pos       - column
     * @param row       - row
     * @param alignment - alignment
     * @param str        - str
     */
    private static void createArrow(Object ob, Integer pos, XWPFTableRow row, ParagraphAlignment alignment, String str) {
        if (ObjectUtils.notEqual(ob, null)) {
            createParagraphForCell(row.getCell(pos), alignment).setText(str);
            tableCellText.delete(0, tableCellText.length());
            tableCellText.trimToSize();
        }
    }

    /**
     * Создание блока, содержащего footer информацию
     */
    private static void createFootereBlock() {
        tableParagraph = null;
        if (settings.getFooterList() != null) {
            if (settings.getFooterList().size() > 0) {
                tableParagraph = resultDoc.createParagraph();
                tableParagraph.setAlignment(ParagraphAlignment.LEFT);
//                tableParagraph.setWordWrap(true);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            tableStroke = null;
            for (String footerStr : settings.getFooterList()) {
                tableStroke = tableParagraph.createRun();
                if (footerStr.contains("[data]")) {
                    StringBuilder sb = new StringBuilder(footerStr);
                    footerStr = sb.substring(0, sb.indexOf("["));
                    Date currentDate = new Date();
                    footerStr = footerStr + " " + sdf.format(currentDate);
                }
                tableStroke.setFontFamily(settings.getFontName());
                tableStroke.setFontSize((short) 7);
                tableStroke.setText(footerStr);
                tableStroke.addBreak();
            }
        }
    }

    /**
     * Смена ориентации листа
     *
     * @param document    - word - документ
     * @param orientation - ориентация (для альбомной landscape)
     */
    private static void changeOrientation(XWPFDocument document, String orientation) {
        CTDocument1 doc = document.getDocument();
        CTBody body = doc.getBody();
        CTSectPr section = body.addNewSectPr();
        CTPageSz pageSize;
        if (section.isSetPgSz()) {
            pageSize = section.getPgSz();
        } else {
            pageSize = section.addNewPgSz();
        }
        if (orientation.equals("landscape")) {
            pageSize.setOrient(STPageOrientation.LANDSCAPE);
            pageSize.setW(BigInteger.valueOf(842 * 20));
            pageSize.setH(BigInteger.valueOf(595 * 20));
        } else {
            pageSize.setOrient(STPageOrientation.PORTRAIT);
            pageSize.setH(BigInteger.valueOf(842 * 20));
            pageSize.setW(BigInteger.valueOf(595 * 20));
        }
    }

}
