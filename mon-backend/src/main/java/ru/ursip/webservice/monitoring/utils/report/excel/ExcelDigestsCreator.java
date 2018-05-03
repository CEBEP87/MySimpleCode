package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.report.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.Period;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Реализация сервиса созданитя отчета
 *
 * @author Samsonov
 * @since 26.03.2018
 */
public class ExcelDigestsCreator {
    /**
     * Path download prices directory
     */
    private static final String fileLogDir = "./fileLog/";

    /**
     * create Digest
     *
     * @param organizationList organizationList
     * @param period           period
     * @@return Workbook
     */
    public static Workbook createDigestOrganisations(List<HashMap> organizationList, Period period) throws Exception {
        Workbook workbook = null;
        try {
            final File dir1 = new File(fileLogDir);
            if (!dir1.exists()) dir1.mkdir();
            FileInputStream excelFile = new FileInputStream(new File(fileLogDir + "digest_template_org.xlsx"));
            workbook = new XSSFWorkbook(excelFile);
            pushData(workbook, organizationList, period);
            excelFile.close();

        } catch (Exception e) {
            throw new Exception("не удалось создать отчет");
        }
        return workbook;
    }

    /**
     * pushData to document
     *
     * @param workbook         workbook
     * @param organizationList organizations List
     * @param period           period
     */
    private static void pushData(Workbook workbook, List<HashMap> organizationList, Period period) {

        int rowIterator = 0;
        boolean startWrite = false;
        int resultTableRow = 0;
        Integer columnNumeration = null;
        Integer columnName = null;
        Integer columnForm = null;
        Integer columnAddress = null;
        Integer columnEmail = null;
        Integer columnPhone = null;
        Integer columnWebSite = null;
        int rowStart = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        Sheet datatypeSheet = workbook.getSheetAt(0);
        if (organizationList != null && organizationList.size() != 0)
            while (datatypeSheet.getLastRowNum() + 2 > rowIterator) {
                Row row0 = datatypeSheet.getRow(rowIterator);
                if (startWrite) {
                    writeRow(datatypeSheet, datatypeSheet.getRow(rowIterator - 1), columnNumeration, columnForm, columnAddress, columnEmail, columnPhone, columnName, columnWebSite,
                            rowIterator, resultTableRow, organizationList);
                    resultTableRow++;
                    if (organizationList.size() == resultTableRow) break;
                } else
                    for (int cell = 0; cell < 20; cell++) {
                        try {
                            if (row0.getCell(cell).getStringCellValue().equals(
                                    "Протокол регистрации организаций, включенных в отчет по результатам мониторинга текущих отпускных цен поставщиков строительных материалов, изделий и конструкций за период\n" +
                                            "с {period.date_from} по {period.date_till}")) {
                                dateFormat = new SimpleDateFormat("yyyy.MM.dd ");
                                row0.getCell(cell).setCellValue("Протокол регистрации организаций, включенных в отчет по результатам мониторинга текущих отпускных цен поставщиков строительных материалов, изделий и конструкций за период\n" +
                                        "с" + dateFormat.format(period.getDateFrom()) + " по "
                                        + dateFormat.format(period.getDateTill()) + "");
                            }
                            if (row0.getCell(cell).getStringCellValue().equals("{Порядковый номер}")) {
                                startWrite = true;
                                columnNumeration = cell;
                                rowStart = row0.getRowNum();
                            }
                            if (row0.getCell(cell).getStringCellValue().equals("{company.name}")) columnName = cell;
                            if (row0.getCell(cell).getStringCellValue().equals("{company_form.name}"))
                                columnForm = cell;
                            if (row0.getCell(cell).getStringCellValue().equals("{company.address}"))
                                columnAddress = cell;
                            if (row0.getCell(cell).getStringCellValue().equals("{company.email}"))
                                columnEmail = cell;
                            if (row0.getCell(cell).getStringCellValue().equals("{company.phone}"))
                                columnPhone = cell;
                            if (row0.getCell(cell).getStringCellValue().equals("{company.website}"))
                                columnWebSite = cell;
                        } catch (Exception e) {
                        }
                    }
                rowIterator++;
            }
        removeRow(datatypeSheet, datatypeSheet.getLastRowNum());
    }

    /**
     * writeRow
     *
     * @param datatypeSheet    datatypeSheet
     * @param row0             row record
     * @param columnNumeration columnNumeration
     * @param columnForm       columnForm
     * @param columnAddress    columnAddress
     * @param columnEmail      columnEmail
     * @param columnPhone      columnPhone
     * @param columnName       columnName
     * @param columnWebSite    columnWebSite
     * @param resultTableRow   resultTableRow
     * @param rowIterator      rowIterator
     * @param resultCollection resultCollection
     */
    private static void writeRow(Sheet datatypeSheet, Row row0, Integer columnNumeration, Integer columnForm,
                                 Integer columnAddress, Integer columnEmail, Integer columnPhone,
                                 Integer columnName, Integer columnWebSite, Integer rowIterator,
                                 Integer resultTableRow, List<HashMap> resultCollection) {
        try {
            HashMap company = resultCollection.get(resultTableRow);
            Row newRow = datatypeSheet.createRow(datatypeSheet.getLastRowNum() + 1);
            copyRow(datatypeSheet, rowIterator - 1);
            if (columnNumeration != null) {
                if (row0.getCell(columnNumeration) == null)
                    row0.createCell(columnNumeration).setCellValue(resultTableRow + 1);
                else
                    row0.getCell(columnNumeration).setCellValue(resultTableRow + 1);
                row0.getCell(columnNumeration).setCellStyle(datatypeSheet.getRow(rowIterator - 1).getCell(columnNumeration).getCellStyle());
            }
            if (columnName != null) {
                if (row0.getCell(columnName) == null)
                    row0.createCell(columnName).setCellValue((String) company.get("nazvanie"));
                else
                    row0.getCell(columnName).setCellValue((String) company.get("nazvanie"));
                row0.getCell(columnName).setCellStyle(datatypeSheet.getRow(rowIterator - 1).getCell(columnName).getCellStyle());
            }
            HashMap opfCompany = (HashMap) company.get("opf");

            if (columnForm != null) {
                if (row0.getCell(columnForm) == null)
                    row0.createCell(columnForm).setCellValue((String) opfCompany.get("nazvanie"));
                else
                    row0.getCell(columnForm).setCellValue((String) opfCompany.get("nazvanie"));
                row0.getCell(columnForm).setCellStyle(datatypeSheet.getRow(rowIterator - 1).getCell(columnForm).getCellStyle());
            }
            if (columnAddress != null) {
                if (row0.getCell(columnAddress) == null)
                    row0.createCell(columnAddress).setCellValue((String) company.get("adres_pochtoviy"));
                else
                    row0.getCell(columnAddress).setCellValue((String) company.get("adres_pochtoviy"));
                row0.getCell(columnAddress).setCellStyle(datatypeSheet.getRow(rowIterator - 1).getCell(columnAddress).getCellStyle());
            }
            if (columnEmail != null) {
                if (row0.getCell(columnEmail) == null)
                    row0.createCell(columnEmail).setCellValue((String) company.get("email"));
                else
                    row0.getCell(columnEmail).setCellValue((String) company.get("email"));
                row0.getCell(columnEmail).setCellStyle(datatypeSheet.getRow(rowIterator - 1).getCell(columnEmail).getCellStyle());
            }
            if (columnPhone != null) {
                if (row0.getCell(columnPhone) == null)
                    row0.createCell(columnPhone).setCellValue((String) company.get("telefon"));
                else
                    row0.getCell(columnPhone).setCellValue((String) company.get("telefon"));
                row0.getCell(columnPhone).setCellStyle(datatypeSheet.getRow(rowIterator - 1).getCell(columnPhone).getCellStyle());
            }

            if (columnWebSite != null) {
                if (row0.getCell(columnPhone) == null)
                    row0.createCell(columnWebSite).setCellValue((String) company.get("web_sait"));
                else
                    row0.getCell(columnWebSite).setCellValue((String) company.get("web_sait"));
                row0.getCell(columnWebSite).setCellStyle(datatypeSheet.getRow(rowIterator - 1).getCell(columnWebSite).getCellStyle());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * writeRow
     *
     * @param worksheet    worksheet
     * @param sourceRowNum sourceRowNum
     */
    private static void copyRow(Sheet worksheet, int sourceRowNum) {
        // Get the source / new row
        Row sourceRow = worksheet.getRow(sourceRowNum);
        Row newRow = worksheet.getRow(sourceRowNum + 1);

        // Loop through source columns to add to new row
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            // Grab a copy of the old/new cell
            Cell oldCell = sourceRow.getCell(i);
            Cell newCell = newRow.createCell(i);

            // If the old cell is null jump to next cell
            if (oldCell == null) {
                newCell = null;
                continue;
            }

            // Use old cell style
            newCell.setCellStyle(oldCell.getCellStyle());

            // If there is a cell comment, copy
            if (newCell.getCellComment() != null) {
                newCell.setCellComment(oldCell.getCellComment());
            }

            // If there is a cell hyperlink, copy
            if (oldCell.getHyperlink() != null) {
                newCell.setHyperlink(oldCell.getHyperlink());
            }

            // Set the cell data type
            newCell.setCellType(oldCell.getCellType());

            // Set the cell data value
            switch (oldCell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    newCell.setCellValue(oldCell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR:
                    newCell.setCellErrorValue(oldCell.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    newCell.setCellFormula(oldCell.getCellFormula());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    newCell.setCellValue(oldCell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    newCell.setCellValue(oldCell.getRichStringCellValue());
                    break;
            }
        }
    }

    /**
     * writeRow
     *
     * @param sheet    sheet
     * @param rowIndex rowIndex
     */
    public static void removeRow(Sheet sheet, int rowIndex) {
        sheet.removeRow(sheet.getRow(rowIndex));
    }
}
