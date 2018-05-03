package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service;

import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.exceptions.ParserException;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.AvePriceResponse;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.Period;

import java.util.List;


/**
 * Сервис с общими методами работы с сущностями
 *
 * @author Samsonov
 * @since 26.01.2018
 */
public interface ExcelParserService {


    /**
     * Комплексный парсер файлов Xlsx & parserXlsm
     *
     * @param filename - filename
     * @param period   - current period
     * @return List<AvePriceResponse>
     */
    AvePriceResponse parserXlsxComplexAvePrice(String filename, Period period) throws ParserException;

    /**
     * Комплексный  файлов Xls
     *
     * @param filename - filename
     * @param period   - current period
     * @return List<AvePriceResponse>
     */
    AvePriceResponse parserXlsComplexAvePrice(String filename, Period period) throws ParserException;
}
