package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.ResourceSection;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.service.UploadFileService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaAction;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaController;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;


/**
 * Контроллер действий для upload prices
 *
 * @author samsonov
 * @since 01.03.2017
 */
@RequestMapping(path = "v1/upload", produces = "application/json; charset=utf-8")
@RestController
@CrossOrigin
@MetaController(name = "upload", description = "upload prices")
@Api(value = "Статусы периода")
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
     * Запрос на загрузку прайса
     *
     * @param name     - имя файла
     * @param periodId - идентификатор периода
     * @param userId   - идентификатор пользователя
     * @param file     - файл
     * @return - информация о ресурсе с указанным кодом, http - статус
     */
    @PostMapping("pricelist")
    @MetaAction(name = "pricelist", description = "загрузка прайса")
    @ApiOperation(value = "загрузка прайса")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Раздел успешно изменен", response = ResourceSection.class),
            @ApiResponse(code = 409, message = "Во время обработки запроса произошел конфликт", response = ServiceError.class),
            @ApiResponse(code = 412, message = "Полученные данные не прошли валидацию", response = ServiceError.class),
            @ApiResponse(code = 500, message = "Внутренняя ошибка сервера", response = ServiceError.class)
    })
    public
    @ResponseBody
    ResponseEntity handleFileUpload(@RequestParam("name") String name,
                                    @RequestParam("periodId") Integer periodId,
                                    @RequestParam("userId") Integer userId,
                                    @RequestParam("file") MultipartFile file) {
        return uploadFileService.handleFileUpload(name, periodId, userId, file, fileLogDir);
    }

    /**
     * Запрос на загрузку цен поставщиков
     *
     * @param periodId - идентификатор периода
     * @param userId   - идентификатор пользователя
     * @param file     - файл
     * @return - информация о ресурсе с указанным кодом, http - статус
     */
    @PostMapping("pricelist-provider")
    @MetaAction(name = "pricelist", description = "загрузка прайса")
    @ApiOperation(value = "загрузка прайса")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Раздел успешно изменен", response = ResourceSection.class),
            @ApiResponse(code = 409, message = "Во время обработки запроса произошел конфликт", response = ServiceError.class),
            @ApiResponse(code = 412, message = "Полученные данные не прошли валидацию", response = ServiceError.class),
            @ApiResponse(code = 500, message = "Внутренняя ошибка сервера", response = ServiceError.class)
    })
    public
    @ResponseBody
    ResponseEntity uploadPriceProvider(@RequestParam("periodId") Integer periodId,
                                       @RequestParam("userId") Integer userId,
                                       @RequestParam("file") MultipartFile file) {
        return uploadFileService.priceListProvider(periodId, userId, file, fileLogDir);
    }


}
