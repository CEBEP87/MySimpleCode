package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.impl;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.exceptions.ParserException;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.AvePriceResponse;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.ParserErrors;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.Period;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.Resource;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.ExcelParserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * parser realization
 *
 * @author samsonov
 * @since 31.01.2018
 */
@Service
public class ExcelParserServiceImpl implements ExcelParserService {
    /**
     * Лист ресурспериодов
     */

    private List<Resource> resultList = new ArrayList<Resource>();
    /**
     * Объект логирования
     */
    private Logger logger = LoggerFactory.getLogger(ExcelParserServiceImpl.class);
    /**
     * совпадение по ТСН
     */
    private boolean coincidenceCodeTSN = false;

    /**
     * совпадение по организацию
     */
    private boolean coincidenceOrganization;

    @Override
    public AvePriceResponse parserXlsxComplexAvePrice(String filename, Period period) throws ParserException {
        List<ParserErrors> errors = new ArrayList<>();
        List<Resource> result = null;
        try {
            FileInputStream excelFile = new FileInputStream(new File(filename));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();
            iterator.hasNext();
            errors = parseRowsWithErrors(iterator, period);
            result = resultList;
            resultList = new ArrayList<Resource>();
            excelFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!errors.isEmpty()) throw new ParserException(errors);

        return new AvePriceResponse(period.getId(), result, resultList.size());
    }

    @Override
    public AvePriceResponse parserXlsComplexAvePrice(String filename, Period period) throws ParserException {
        FileInputStream excelFile = null;
        List<ParserErrors> errors = new ArrayList<>();
        List<Resource> result = null;
        try {
            excelFile = new FileInputStream(new File(filename));
            Workbook workbook = new HSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();
            iterator.hasNext();
            errors = parseRowsWithErrors(iterator, period);
            result = resultList;
            resultList = new ArrayList<Resource>();
            excelFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!errors.isEmpty()) throw new ParserException(errors);
        return new AvePriceResponse(period.getId(), result, resultList.size());
    }

    /**
     * Парсер строк с ошибками
     *
     * @param period   - period
     * @param iterator - итератор
     * @return - лист ошибок
     */
    private List<ParserErrors> parseRowsWithErrors(Iterator<Row> iterator, Period period) {
        Row row0;
        List<ParserErrors> errors = new ArrayList<>();
        while (iterator.hasNext()) {
            row0 = iterator.next();
            if (row0.getRowNum() == 0) continue;
            if (row0.getCell(0) == null) continue;
            if (row0.getCell(1) == null) continue;
            Resource res = null;
            try {
                if (row0.getCell(0).getCellType() == 0) {
                    Double tempDoubleName = row0.getCell(0).getNumericCellValue();
                    try {
                        res = period.getResource().stream().filter(s -> s.getFields().getCodeTsn().equals(tempDoubleName.toString())).findFirst().get();
                    } catch (Exception e) {
                        errors.add(new ParserErrors(row0.getRowNum() + 1, "ресурс c данным ТСН не обнаружен"));
                    }
                }
                if (row0.getCell(0).getCellType() == 1) {
                    String tempString = row0.getCell(0).getStringCellValue();
                    try {
                        res = period.getResource().stream().filter(s -> s.getFields().getCodeTsn().equals(tempString)).findFirst().get();
                    } catch (Exception e) {
                        errors.add(new ParserErrors(row0.getRowNum() + 1, "ресурс c данным ТСН не обнаружен"));
                    }
                }
                if (row0.getCell(0).getCellType() == 2) {
                    String tempString = row0.getCell(0).getStringCellValue();
                    try {
                        res = period.getResource().stream().filter(s -> s.getFields().getCodeTsn().equals(tempString)).findFirst().get();
                    } catch (Exception e) {
                        errors.add(new ParserErrors(row0.getRowNum() + 1, "ресурс c данным ТСН не обнаружен"));
                    }
                }
                if (row0.getCell(0).getCellType() == 3)
                    errors.add(new ParserErrors(row0.getRowNum() + 1, "ТСН не удалось прочитать"));
            } catch (Exception e) {
                errors.add(new ParserErrors(row0.getRowNum() + 1, "Не удалось прочитать ТСН номер"));
            }
            if (res != null)
                try {
                    String tempPrice = new String();
                    if (row0.getCell(1).getCellType() == 0) {
                        BigDecimal tempDoubleName = new BigDecimal(row0.getCell(1).getNumericCellValue(), MathContext.DECIMAL64);
                        tempPrice = tempDoubleName.toString();
                    } else if (row0.getCell(1).getCellType() == 1) {
                        tempPrice = row0.getCell(1).getStringCellValue();
                    }
                    Double abc = Double.parseDouble(tempPrice);
                    long tempDouble = round(abc);
                    res.getCostItems().getAmortization().setAvePrice(tempDouble / 100d);
                } catch (Throwable t) {
                    errors.add(new ParserErrors(row0.getRowNum() + 1, "Недопустимый формат цены"));
                }

            if (errors.isEmpty()) {
                res.getFields().setIsSotrudnik(true);
                resultList.add(res);
            }
        }
        return errors;

    }

    /**
     * rounding and return long value
     *
     * @param value - список организаций
     * @return long - итератор
     */
    private static long round(double value) {
        DecimalFormat df = new DecimalFormat("#.");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String tewp = df.format(((Number) value).doubleValue() * 100);
        return (long) (Double.parseDouble(tewp.replace(",", ".")));
    }
}





