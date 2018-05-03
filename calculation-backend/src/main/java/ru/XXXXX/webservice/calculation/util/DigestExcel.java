package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Group;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Period;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.PeriodService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Logger;


/**
 * Digest Service Impl
 *
 * @author Samsonov_KY
 * @since 21.02.2018
 */
@Component
public class DigestExcel {
    /**
     * Logger
     */

    private static Logger log =
            Logger.getLogger(DigestExcel.class.getName());
    /**
     * getCollectionForReportGenerator
     */
    @Autowired
    private GetCollectionForReportGenerator getCollectionForReportGenerator;

    /**
     * periodService
     */
    @Autowired
    private PeriodService periodService;

    /**
     * get Digest
     *
     * @param idPeriod idPeriod
     * @param response response
     * @param fileLogDir fileLogDir
     * @return ResponseEntity
     */

    public ResponseEntity getDigestExcel(String idPeriod, HttpServletResponse response, String fileLogDir) {
        try {
            HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response);
            FileInputStream excelFile = new FileInputStream(new File(fileLogDir+"template.xlsx"));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Period period = periodService.findOne(idPeriod);
            pushData(workbook, period);
            excelFile.close();
            wrapper.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8");
            wrapper.setHeader("Content-disposition", "attachment;filename=Коэф. пересчета " + period.getTitle() + ".xlsx");
            workbook.write(wrapper.getOutputStream());

            return new ResponseEntity("Digest generated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Внутренняя ошибка сервера", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * pushData to document
     *
     * @param workbook workbook
     * @param period period
     */
    private void pushData(Workbook workbook, Period period) {
        Group chapter = null;
        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        pushList(workbook, 0, period, chapter, "Material");
        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        pushList(workbook, 1, period, chapter, "Machine");
        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        pushList(workbook, 2, period, chapter, "Construction");

        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        pushList(workbook, 3, period, chapter, "Installation");
        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        pushList(workbook, 4, period, chapter, "Commissioning");
        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        pushList(workbook, 5, period, chapter, "Repair");
        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        pushList(workbook, 6, period, chapter, "Restoration");
        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        pushList(workbook, 7, period, chapter, "Overhead");
        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        pushList(workbook, 8, period, chapter, "WinterRise");
        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        pushList(workbook, 9, period, chapter, "TemporaryBuilding");
        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        pushList(workbook, 10, period, chapter, "Service");
        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        pushList(workbook, 11, period, chapter, "Transport");
        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        pushList(workbook, 12, period, chapter, "LargeProcess");
        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        pushList(workbook, 13, period, chapter, "CelebrateProcess");


    }

    /**
     * push list to document
     *
     * @param workbook workbook
     * @param period period
     * @param sheet period
     * @param chapter period
     * @param collectionName period
     */
    private void pushList(Workbook workbook, int sheet, Period period, Group chapter, String collectionName) {
        try {
            int rowIterator = 0;
            boolean startWrite = false;
            int resultTableRow = 0;
            Integer columnNumeration = null;
            Integer columnPressmark = null;
            Integer columnRate = null;
            Integer columnRateMaterial = null;
            Integer columnRateMachine = null;
            Integer columnRateCommon = null;
            Integer columnRateSalary = null;
            Integer columnRateOtherWork = null;


            int rowStart = 0;
            SimpleDateFormat dateFormat = new SimpleDateFormat();
            List<List<String>> resultCollection = getCollectionForReportGenerator.getCollection(collectionName, period, chapter, false);
            Sheet datatypeSheet = workbook.getSheetAt(sheet);
            Row row0 = null;
            if (resultCollection != null && resultCollection.size() != 0)
                while (datatypeSheet.getLastRowNum() > rowIterator) {
                    row0 = datatypeSheet.getRow(rowIterator);
                    if (startWrite) {
                        writeRow(datatypeSheet, row0, columnNumeration, columnRate, columnRateMaterial, columnRateMachine, columnRateCommon, columnPressmark, columnRateSalary,
                                columnRateOtherWork, rowIterator, resultTableRow, resultCollection);
                        resultTableRow++;
                        if (resultCollection.size() == resultTableRow) break;
                    } else
                        for (int cell = 0; cell < 20; cell++) {
                            try {

                                if (row0.getCell(cell).getStringCellValue().equals("period.group.Title"))
                                    row0.getCell(cell).setCellValue(chapter.getTitle());
                                if (row0.getCell(cell).getStringCellValue().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"")) {
                                    dateFormat = new SimpleDateFormat("yyyy.MM.dd ");
                                    row0.getCell(cell).setCellValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX \"" + period.getTitle() + "\" с " + dateFormat.format(period.getDateFrom()) + " по " + dateFormat.format(period.getDateTo()) + "");
                                }
                                if (row0.getCell(cell).getStringCellValue().equals("Порядковый номер")) {
                                    startWrite = true;
                                    columnNumeration = cell;
                                    rowStart = row0.getRowNum();
                                }
                                if (row0.getCell(cell).getStringCellValue().equals("pressmark")) columnPressmark = cell;
                                if (row0.getCell(cell).getStringCellValue().equals("rate")) columnRate = cell;
                                if (row0.getCell(cell).getStringCellValue().equals("Rate_material"))
                                    columnRateMaterial = cell;
                                if (row0.getCell(cell).getStringCellValue().equals("Rate_machine"))
                                    columnRateMachine = cell;
                                if (row0.getCell(cell).getStringCellValue().equals("rate_common"))
                                    columnRateCommon = cell;
                                if (row0.getCell(cell).getStringCellValue().equals("Rate_salary"))
                                    columnRateSalary = cell;
                                if (row0.getCell(cell).getStringCellValue().equals("Rate_other_work"))
                                    columnRateOtherWork = cell;


                            } catch (Exception e) {
                            }
                            //заполнение столбцов
                            //сохранение стиля
                        }
                    rowIterator++;
                }
            removeRow(datatypeSheet, rowStart);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * writeRow
     *
     * @param datatypeSheet datatypeSheet
     * @param row0 row record
     * @param columnNumeration columnNumeration
     * @param columnRate columnRate
     * @param columnRateMaterial columnRateMaterial
     * @param columnRateMachine columnRateMachine
     * @param columnRateCommon columnRateCommon
     * @param columnPressmark columnPressmark
     * @param columnRateSalary columnRateSalary
     * @param columnRateOtherWork columnRateOtherWork
     * @param rowIterator rowIterator
     * @param resultTableRow resultTableRow
     * @param resultCollection resultCollection
     */
    private void writeRow(Sheet datatypeSheet, Row row0, Integer columnNumeration, Integer columnRate,
                          Integer columnRateMaterial, Integer columnRateMachine, Integer columnRateCommon,
                          Integer columnPressmark, Integer columnRateSalary, Integer columnRateOtherWork, Integer rowIterator,
                          Integer resultTableRow, List<List<String>> resultCollection) {
        try {
            copyRow(datatypeSheet, rowIterator - 1);
            if (columnNumeration != null) {
                row0.createCell(columnNumeration).setCellValue(resultTableRow + 1);
                row0.getCell(columnNumeration).setCellStyle(datatypeSheet.getRow(rowIterator - 1).getCell(columnNumeration).getCellStyle());
            }
            if (columnPressmark != null) {
                row0.createCell(columnPressmark).setCellValue(resultCollection.get(resultTableRow).get(1));
                row0.getCell(columnPressmark).setCellStyle(datatypeSheet.getRow(rowIterator - 1).getCell(columnPressmark).getCellStyle());
            }
            if (columnRate != null) {
                row0.createCell(columnRate).setCellValue(resultCollection.get(resultTableRow).get(2));
                row0.getCell(columnRate).setCellStyle(datatypeSheet.getRow(rowIterator - 1).getCell(columnRate).getCellStyle());
            }
            if (columnRateMachine != null) {
                row0.createCell(columnRateMachine).setCellValue(resultCollection.get(resultTableRow).get(2));
                row0.getCell(columnRateMachine).setCellStyle(datatypeSheet.getRow(rowIterator - 1).getCell(columnRateMachine).getCellStyle());
            }
            if (columnRateMaterial != null) {
                row0.createCell(columnRateMaterial).setCellValue(resultCollection.get(resultTableRow).get(3));
                row0.getCell(columnRateMaterial).setCellStyle(datatypeSheet.getRow(rowIterator - 1).getCell(columnRateMaterial).getCellStyle());
            }
            if (columnRateCommon != null) {
                row0.createCell(columnRateCommon).setCellValue(resultCollection.get(resultTableRow).get(2));
                row0.getCell(columnRateCommon).setCellStyle(datatypeSheet.getRow(rowIterator - 1).getCell(columnRateCommon).getCellStyle());
            }
            if (columnRateSalary != null) {
                row0.createCell(columnRateSalary).setCellValue(resultCollection.get(resultTableRow).get(3));
                row0.getCell(columnRateSalary).setCellStyle(datatypeSheet.getRow(rowIterator - 1).getCell(columnRateSalary).getCellStyle());
            }
            //// ATTENTION HARDCORE
            if (columnRateMachine != null && columnRateCommon != null) {
                row0.createCell(columnRateMachine).setCellValue(resultCollection.get(resultTableRow).get(4));
                row0.getCell(columnRateMachine).setCellStyle(datatypeSheet.getRow(rowIterator - 1).getCell(columnRateMachine).getCellStyle());
            }
            if (columnRateMaterial != null && columnRateCommon != null) {
                row0.createCell(columnRateMaterial).setCellValue(resultCollection.get(resultTableRow).get(5));
                row0.getCell(columnRateMaterial).setCellStyle(datatypeSheet.getRow(rowIterator - 1).getCell(columnRateMaterial).getCellStyle());
            }

            if (columnRateOtherWork != null) {
                row0.createCell(columnRateOtherWork).setCellValue(resultCollection.get(resultTableRow).get(6));
                row0.getCell(columnRateOtherWork).setCellStyle(datatypeSheet.getRow(rowIterator - 1).getCell(columnRateOtherWork).getCellStyle());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * writeRow
     *
     * @param worksheet worksheet
     * @param sourceRowNum sourceRowNum
     */
    private static void copyRow(Sheet worksheet, int sourceRowNum) {
        // Get the source / new row
        Row sourceRow = worksheet.getRow(sourceRowNum);
     //   Row newRow = worksheet.getRow(sourceRowNum + 1);
        Row newRow = worksheet.createRow(worksheet.getLastRowNum() + 1);
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
     * @param sheet sheet
     * @param rowIndex rowIndex
     */
    public static void removeRow(Sheet sheet, int rowIndex) {
        int lastRowNum = sheet.getLastRowNum();
        if (rowIndex >= 0 && rowIndex < lastRowNum) {
            sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
        }
        if (rowIndex == lastRowNum) {
            sheet.removeRow(sheet.getRow(rowIndex));
        }
    }
}
