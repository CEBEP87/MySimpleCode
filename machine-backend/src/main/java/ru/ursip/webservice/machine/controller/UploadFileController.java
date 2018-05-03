package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.UploadFileService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaAction;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaController;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;

import java.util.List;


/**
 * Download files controller
 *
 * @author samsonov
 * @since 31.01.2018
 */
@RequestMapping(path = "v1/upload", produces = "application/json; charset=utf-8")
@RestController
@CrossOrigin
@MetaController(name = "upload", description = "upload prices")
@Api(value = "Download files controller")
public class UploadFileController {
    /**
     * Path download prices directory
     */
    private static final String fileLogDir = "./fileLog/";


    /**
     * Сервис для работы с выгрузкой прайсов
     */
    @Autowired
    private UploadFileService uploadFileService;



    /**
     * Запрос на загрузку восстановительных цен
     *
     * @param periodId - идентификатор периода
     * @param file     - файл
     * @return - информация о ресурсе с указанным кодом, http - статус
     */
    @PostMapping("ave-price")
    @MetaAction(name = "pricelist", description = "загрузка прайса")
    @ApiOperation(value = "загрузка прайса")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Раздел успешно изменен", response = List.class),
            @ApiResponse(code = 409, message = "Во время обработки запроса произошел конфликт", response = ServiceError.class),
            @ApiResponse(code = 412, message = "Полученные данные не прошли валидацию", response = ServiceError.class),
            @ApiResponse(code = 500, message = "Внутренняя ошибка сервера", response = ServiceError.class)
    })
    public
    @ResponseBody
    ResponseEntity avePrice(@RequestParam("periodId") String periodId,
                                    @RequestParam("file") MultipartFile file) {
        return uploadFileService.avePrice(periodId, file, fileLogDir);
    }

    /**
     * Запрос на загрузку цен сметных
     *
     * @param periodId - идентификатор периода
     * @param file     - файл
     * @return - информация о ресурсе с указанным кодом, http - статус
     */
        @PostMapping("estimate-price")
    @MetaAction(name = "estimate", description = "estimate")
    @ApiOperation(value = "estimate")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Раздел успешно изменен", response = List.class),
            @ApiResponse(code = 409, message = "Во время обработки запроса произошел конфликт", response = ServiceError.class),
            @ApiResponse(code = 412, message = "Полученные данные не прошли валидацию", response = ServiceError.class),
            @ApiResponse(code = 500, message = "Внутренняя ошибка сервера", response = ServiceError.class)
    })
    public
    @ResponseBody
    ResponseEntity estimatePrice(@RequestParam("periodId") String periodId,
                            @RequestParam("file") MultipartFile file) {
        return uploadFileService.estimatePrice(periodId, file, fileLogDir);
    }
}
