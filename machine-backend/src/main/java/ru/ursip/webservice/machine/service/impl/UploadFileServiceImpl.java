package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.exceptions.ParserException;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.AvePriceResponse;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.Period;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * реализация сервиса для работы с коридором значений
 *
 * @author samsonov
 * @since 03.03.2017
 */
@Service
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class UploadFileServiceImpl implements UploadFileService {
    /**
     * Объект логирования
     */
    private Logger logger = LoggerFactory.getLogger(UploadFileServiceImpl.class);

    /**
     * Сервис  для работы с внешними ресурсами
     */
    @Autowired
    private ResourceService resourceService;

    /**
     * Сервис  для работы с периодами
     */
    @Autowired
    private PeriodService periodService;

    /**
     * Сервис  для работы с внешними ресурсами
     */
    @Autowired
    private ExcelParserService excelParserService;

    /**
     * Сервис  для работы с внешними ресурсами
     */
    @Autowired
    private CalculationsService calculationsService;


    @Override
    public ResponseEntity avePrice(String periodId, MultipartFile file, String fileLogDir) {
        try {
            logger.info("Check directory");
            String filePath = fileLogDir + file.getOriginalFilename();
            logger.info("Download File");
            downloadFile(file, fileLogDir);
            logger.info("Find period "+periodId);
            Period period = periodService.findOne(periodId);
            AvePriceResponse avePriceResponse = null;
            logger.info("Parse File");
            if (file.getOriginalFilename().charAt(file.getOriginalFilename().length() - 1) == 'x')
                avePriceResponse = excelParserService.parserXlsxComplexAvePrice(filePath, period);
            if (file.getOriginalFilename().charAt(file.getOriginalFilename().length() - 1) == 'm')
                avePriceResponse = excelParserService.parserXlsxComplexAvePrice(filePath, period);
            if (file.getOriginalFilename().charAt(file.getOriginalFilename().length() - 1) == 's')
                avePriceResponse = excelParserService.parserXlsComplexAvePrice(filePath, period);
            ///добавить
            File excelFile = new File(file.getOriginalFilename());
            logger.info("Удалить файл");
            excelFile.delete();
            avePriceResponse.getResource().forEach(newRes -> {
                period.getResource().forEach(res -> {
                    if (newRes.getFields().getCodeTsn().equals(res.getFields().getCodeTsn())) {
                        res = calculationsService.calculateCostItems(newRes, period.getFields()).getInputResource();
                    }
                });
            });
            periodService.save(period);
            return new ResponseEntity(avePriceResponse, HttpStatus.OK);
        } catch (ParserException pe) {
            return new ResponseEntity(pe.getErrors(), HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Внутренняя ошибка сервера", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity estimatePrice(String periodId, MultipartFile file, String fileLogDir) {
        try {
            String filePath = fileLogDir + file.getOriginalFilename();
            downloadFile(file, fileLogDir);
            Period period = periodService.findOne(periodId);
            AvePriceResponse avePriceResponse = null;
            if (file.getOriginalFilename().charAt(file.getOriginalFilename().length() - 1) == 'x')
                avePriceResponse = excelParserService.parserXlsxComplexAvePrice(filePath, period);
            if (file.getOriginalFilename().charAt(file.getOriginalFilename().length() - 1) == 'm')
                avePriceResponse = excelParserService.parserXlsxComplexAvePrice(filePath, period);
            if (file.getOriginalFilename().charAt(file.getOriginalFilename().length() - 1) == 's')
                avePriceResponse = excelParserService.parserXlsComplexAvePrice(filePath, period);
            ///добавить
            File excelFile = new File(file.getOriginalFilename());
            excelFile.delete();
            avePriceResponse.getResource().forEach(newRes -> {
                period.getResource().forEach(res -> {
                    if (newRes.getFields().getCodeTsn().equals(res.getFields().getCodeTsn())) {
                        newRes.getFields().setEstimatedCurrentPriceH(newRes.getCostItems().getAmortization().getAvePrice());
                        newRes.getCostItems().getAmortization().setAvePrice(res.getCostItems().getAmortization().getAvePrice());
                        newRes.getCostItems().getSalary().setIndexSalaryH(calculationsService.rounding(newRes.getFields().getEstimatedBasePriceIncludeTaxByDriver()*period.getFields().getIndexSalary()));
                        if(newRes.getFields().getEstimatedBasePrice()!=null)if(newRes.getFields().getEstimatedBasePrice()!=0d)
                            newRes.getFields().setCoefRecountCurrentH(calculationsService.rounding(newRes.getFields().getEstimatedCurrentPriceH()/newRes.getFields().getEstimatedBasePrice()));
                        res = newRes;

                    }
                });
            });
            periodService.save(period);
            return new ResponseEntity(avePriceResponse, HttpStatus.OK);
        } catch (ParserException pe) {
            return new ResponseEntity(pe.getErrors(), HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Внутренняя ошибка сервера", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Download file
     *
     * @param file       - file
     * @param fileLogDir -directory
     */
    private void downloadFile(MultipartFile file, String fileLogDir) {
        final File dir1 = new File(fileLogDir);
        if (!dir1.exists()) dir1.mkdir();
        String filePath = fileLogDir + file.getOriginalFilename();

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(filePath)));
                stream.write(bytes);
                stream.close();

            } catch (Exception e) {
                logger.info("Не удалось загрузить файл на сервер");
            }
        }
    }
}
