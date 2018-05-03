package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ObjectUtils;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.Period;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.Resource;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.ResourceSection;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.report.ReportSettings;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.report.ReportTableHeadColumn;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report.ReportConfiguration;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report.ReportDocTypeProperties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Формирование PDF - документа
 *
 * @author samsonov
 * @since 29.06.2017
 */
public class PDFCurrentPeriodAvgPriceDocumentCreator {

    /**
     * Количество столбцов в таблице
     */
    private static int maxTableColumn = 0;

    /**
     * Настройки отчета
     */
    private static ReportSettings settings = null;

    /**
     * PDF - документ
     */
    private static Document resultDoc = null;

    /**
     * Шрифт в UTF-кодировке для отображения кирилицы
     */
    private static BaseFont customBaseFont = null;

    /**
     * Создание отчета
     *
     * @param resourcesPeriod - список ресурсов в периоде со средним значением цен
     * @param sections        - список разделов
     * @param period          - период
     * @param docType         - тип отчета
     * @return - объект pdf документа
     */
    public static InputStream createNewPDFDocument(Map<Resource, Double> resourcesPeriod,
                                                    List<ResourceSection> sections,
                                                    Period period,
                                                    ReportDocTypeProperties docType) throws Exception {
//        String filePathOne = "d:\\TestMonitoring\\CalcMethodMonitoring_One" + period.getId() + ".pdf";
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        //Подключаем файл шрифта, который поддерживает кириллицу(лежит в ресурсах)
        byte[] fontBytes;
        InputStream configFile = new ClassPathResource("/report/fonts/arial_unicode.ttf").getInputStream();
//        fontBytes = configFile.
        fontBytes = IOUtils.toByteArray(configFile);
//        fontBytes = IOUtils.toByteArray(PDFCurrentPeriodAvgPriceDocumentCreator.class.getClassLoader().
//                getResource("/report/fonts/arial_unicode.ttf")
//                .getResourceAsStream("/report/fonts/arial_unicode.ttf"));
        customBaseFont = BaseFont.createFont("arial_unicode.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontBytes, null);
//        ClassLoader classLoader = PDFCurrentPeriodAvgPriceDocumentCreator.class.getClassLoader();
//        String fontPath = classLoader.getResource("report/fonts/arial_unicode.ttf").getPath();
//        customBaseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        //Создание PDF документа
        resultDoc = new Document(PageSize.LETTER.rotate());
//        PdfWriter writer = PdfWriter.getInstance(resultDoc, bytes);
        PdfWriter writer = PdfWriter.getInstance(resultDoc, result);
        resultDoc.open();
        // Получение настроек для документа
        settings = ReportConfiguration.getReportSettings(docType);
        // Получаем информацию о количестве колонок в таблице
        maxTableColumn = settings.getTableHeadColumns().size();
        // Формирование дополнительных данных для заголовков отчета
        // Добавляем в заголовок таблицы информацию о текущем периоде
        List<String> reportTableTitle = settings.getTitleList();
        int lastTitleIndex = reportTableTitle.size() - 1;
        // Формируем строку с информацией о периоде
        Calendar cal = Calendar.getInstance();
        cal.setTime(period.getDateTill());
        String lastTitleStringRow = reportTableTitle.get(lastTitleIndex) + " " +
                period.getName() + " " + cal.get(Calendar.YEAR) + " год";
        reportTableTitle.remove(lastTitleIndex);
        reportTableTitle.add(lastTitleStringRow);
        settings.setTitleList(reportTableTitle);
        // Формирование блока с сервисной информацией
        createServiceBlock();
        // Формирование блоока с заголовком таблицы
        createTableTitleBlock();

        //Формирование таблицы
        if (settings.getTableHeadColumns() != null) {
            // Пустая строка (отступ)
            resultDoc.add(Chunk.NEWLINE);
            float[] columnWidths = {0.5f, 1f, 1f, 1.8f, 4.5f, 1f, 1f, 1f, 1f};
            // Создаем таблицу в PDF с заданной шириной ячеек
            PdfPTable table = new PdfPTable(columnWidths);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            // Задаем процент занимаемой таблицой
            table.setWidthPercentage(100f);
            // Формирование шапки таблицы
            createTableHeadRows(table);
            // Наполнение таблицы
            createTableContentRows(table, resourcesPeriod, sections);
            resultDoc.add(table);
        }
        // Формирование блоока с подвалом
        createFooterBlock();
        resultDoc.close();

        return  new ByteArrayInputStream(result.toByteArray());
    }

    /**
     * Создание блока, содержащего служебную информация
     */
    private static void createServiceBlock() throws DocumentException {
        Paragraph paragraph = null;
        if (settings.getServiceList() != null) {
            if (settings.getServiceList().isEmpty()) {
                return;
            }
            Font serviceFont = new Font(customBaseFont, settings.getServiceFontSize(), Font.NORMAL);
            for (String serviceStr : settings.getServiceList()) {
                paragraph = new Paragraph();
                paragraph.setAlignment(Element.ALIGN_RIGHT);
                paragraph.setFont(serviceFont);
                paragraph.add(serviceStr);
                resultDoc.add(paragraph);
            }
        }
    }

    /**
     * Создание блока, содержащего информацию о заголовке таблицы
     */
    private static void createTableTitleBlock() throws DocumentException {
        Paragraph paragraph = null;
        if (settings.getTitleList() != null) {
            if (settings.getTitleList().isEmpty()) {
                return;
            }
            Font tableTitleFont = new Font(customBaseFont, settings.getTitleTableFontSize(), Font.BOLD);
            for (String serviceStr : settings.getTitleList()) {
                paragraph = new Paragraph();
                paragraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.setFont(tableTitleFont);
                paragraph.add(serviceStr);
                resultDoc.add(paragraph);
            }
        }
    }

    /**
     * Создание блока, содержащего footer информацию
     */
    private static void createFooterBlock() throws DocumentException {
        Paragraph paragraph = null;
        if (settings.getFooterList() != null) {
            if (settings.getFooterList().isEmpty()) {
                return;
            }
            Font footerFont = new Font(customBaseFont, settings.getFooterFontSize(), Font.NORMAL);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            for (String footerStr : settings.getFooterList()) {
                paragraph = new Paragraph();
                if (footerStr.contains("[data]")) {
                    StringBuilder sb = new StringBuilder(footerStr);
                    footerStr = sb.substring(0, sb.indexOf("["));
                    Date currentDate = new Date();
                    footerStr = footerStr + " " + sdf.format(currentDate);
                }
                paragraph.setAlignment(Element.ALIGN_LEFT);
                paragraph.setFont(footerFont);
                paragraph.add(footerStr);
                resultDoc.add(paragraph);
            }
        }
    }

    /**
     * Создание строк таблицы с заголовками столбцов
     *
     * @param table - таблица
     */
    private static void createTableHeadRows(PdfPTable table) {
        List<PdfPRow> result = new ArrayList<>();
        // Количество строк в шапке таблицы
        int countTableRows = 0;
        for (ReportTableHeadColumn currentColumn : settings.getTableHeadColumns()) {
            int currentColumnCountRows = currentColumn.getRows().size();
            if (countTableRows <= currentColumnCountRows) {
                countTableRows = currentColumnCountRows;
            }
        }
        PdfPCell cell;
        boolean[][] headerMergedMap = new boolean[countTableRows][settings.getTableHeadColumns().size()];
        Font tableHeadFont = new Font(customBaseFont, 6, Font.BOLD);
        for (int i = 0; i < countTableRows; i++) {
            for (int j = 0; j < maxTableColumn; j++) {
                String cellStr = settings.getTableHeadColumns().get(j).getRows().get(i);
                if (!cellStr.equals("")) {
                    cell = new PdfPCell(new Phrase(cellStr, tableHeadFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    boolean merged = true;
                    int countRowMerged = 1;
                    int countColMerged = 1;
                    int rowN = i + 1;
                    int colN = j + 1;
                    boolean isRowMerged = false;
                    boolean isColMerged = false;
                    while (merged) {
                        headerMergedMap[rowN - 1][j] = true;
                        if (rowN < countTableRows && settings.getTableHeadColumns().get(j).getRows().get(rowN).equals("") &&
                                !headerMergedMap[rowN][j]) {
                            headerMergedMap[rowN][j] = true;
                            countRowMerged++;
                            rowN++;
                            isRowMerged = true;
                        } else merged = false;
                    }
                    if (isRowMerged) {
                        cell.setRowspan(countRowMerged);
                    } else {
                        merged = true;
                        while (merged) {
                            headerMergedMap[i][colN - 1] = true;
                            if (colN < maxTableColumn
                                    && settings.getTableHeadColumns().get(colN).getRows().get(i).equals("")
                                    && !headerMergedMap[i][colN]) {
                                headerMergedMap[i][colN] = true;
                                countColMerged++;
                                colN++;
                                isColMerged = true;
                            } else merged = false;
                        }
                    }
                    if (isColMerged) {
                        cell.setColspan(countColMerged);
                    }
                    table.addCell(cell);
                }
            }
        }
        StringBuilder colNumberSb = new StringBuilder();
        for (int i = 0; i < settings.getTableHeadColumns().size(); i++) {
            colNumberSb.delete(0, colNumberSb.length());
            colNumberSb.trimToSize();
            colNumberSb.append(i + 1);
            cell = new PdfPCell(new Phrase(colNumberSb.toString(), tableHeadFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    /**
     * Создание блока, содержащего content информацию
     *
     * @param table          - таблица
     * @param resourceAVGMap - наименование ресурса со средним значением
     * @param sections       - список разделов
     */
    private static void createTableContentRows(PdfPTable table, Map<Resource, Double> resourceAVGMap,
                                               List<ResourceSection> sections) {
        if (sections != null && !sections.isEmpty() && resourceAVGMap != null && !resourceAVGMap.isEmpty()) {
            //Счетчик записи цен поставщиков
            int countResourcePeriod = 0;
            // Вывод цен поставщиков ресурсов по разделам
            for (ResourceSection currentSection : sections) {
                // Создание строки с информацией о разделе
                createTableSectionRow(table, currentSection);
                // Поиск цен постащиков ресурсов заданного раздела
                for (Resource resource : resourceAVGMap.keySet()) {
                    if (ObjectUtils.nullSafeEquals(resource.getResourceSection().getId(), currentSection.getId())) {
                        createTableResourcePeriodRow(table, resource, resourceAVGMap.get(resource), ++countResourcePeriod);
                    }
                }
            }
        }
    }

    /**
     * Создание строки с записью о ресурсы
     *
     * @param table               - исходная таблица
     * @param resource            - информация о ресурсе
     * @param avgResourcePrice    - информация о средней цене ресурса
     * @param countResourcePeriod - счетчик в таблице
     */
    private static void createTableResourcePeriodRow(PdfPTable table, Resource resource,
                                                     Double avgResourcePrice, int countResourcePeriod) {
        PdfPCell cell = null;
        Font tableContentFont = new Font(customBaseFont, settings.getTableFontSize(), Font.NORMAL);
        StringBuilder cellStr = new StringBuilder();
        for (int i = 0; i < maxTableColumn; i++) {
            cellStr.delete(0, cellStr.length());
            cellStr.trimToSize();
            cellStr.append("");
            cell = new PdfPCell();
            switch (i) {
                case 0:
                    cellStr.append(countResourcePeriod);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    break;
                case 1:
                    cellStr.append(resource.getCodeTSN());
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    break;
                case 2:
                    cellStr.append(resource.getCodeOKP());
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    break;
                case 3:
                    cellStr.append(resource.getCodeOKPD());
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    break;
                case 4:
                    cellStr.append(resource.getName());
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    break;
                case 5:
                    cellStr.append(resource.getMeasure().getCode());
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    break;
                case 6:
                    cellStr.append(resource.getWeightNet());
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    break;
                case 7:
                    cellStr.append(resource.getWeightGross());
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    break;
                case 8:
                    cellStr.append(avgResourcePrice / 100);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    break;
            }
            cell.setPhrase(new Phrase(cellStr.toString(), tableContentFont));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

    }

    /**
     * Создание строки с записью о разделе
     *
     * @param table           - исходная таблица
     * @param resourceSection - информация о разделе
     */
    private static void createTableSectionRow(PdfPTable table, ResourceSection resourceSection) {
        PdfPCell cell;
        Font tableContentFont = new Font(customBaseFont, settings.getTableFontSize(), Font.BOLD);
        StringBuilder cellStr = new StringBuilder();
        for (int i = 0; i < maxTableColumn; i++) {
            cellStr.delete(0, cellStr.length());
            cellStr.trimToSize();
            cellStr.append("");
            if (i == 4 && resourceSection.getName() != null) {
                cellStr.append(resourceSection.getName());
            }
            cell = new PdfPCell(new Phrase(cellStr.toString(), tableContentFont));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

}
