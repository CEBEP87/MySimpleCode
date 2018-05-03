package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.service;

import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.exceptions.ParserException;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.Resource;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.ResourcePeriod;

import java.util.HashMap;
import java.util.List;


/**
 * Сервис с общими методами работы с сущностями
 *
 * @author Samsonov
 * @since 26.01.2018
 */
public interface ExcelParserService {

    /**
     * парсер файлов Xlsx & parserXlsm
     *
     * @param filename - filename
     * @param org      - org
     * @return  List<ResourcePeriod>
     */
    List<ResourcePeriod> parserXlsx(String filename, List<HashMap> org);

    /**
     * парсер файлов Xls
     *
     * @param filename - filename
     * @param org      - org
     * @return  List<ResourcePeriod>
     */
    List<ResourcePeriod> parserXls(String filename, List<HashMap> org);

    /**
     * Комплексный парсер файлов Xlsx & parserXlsm
     *
     * @param filename - filename
     * @param org      - org
     * @param resourceList      - org
     * @return  List<ResourcePeriod>
     */
    List<ResourcePeriod> parserXlsxComplex(String filename, List<HashMap> org,List<Resource> resourceList) throws ParserException;

    /**
     * Комплексный  файлов Xls
     *
     * @param filename - filename
     * @param org      - org
     * @param resourceList      - org
     * @return  List<ResourcePeriod>
     */
    List<ResourcePeriod> parserXlsComplex(String filename, List<HashMap> org,List<Resource> resourceList) throws ParserException;
}
