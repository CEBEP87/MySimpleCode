package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.Measure;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.Zsr;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.service.ZsrService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaAction;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaController;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;

/**
 * Контроллер действий для работы с Заготовительно-складские расходы
 *
 * @author samsonov
 * @since 09.02.2017
 */
@RequestMapping(path = "v1/zsr/", produces = "application/json; charset=utf-8")
@RestController
@CrossOrigin
@MetaController(name = "zsr", description = "Заготовительно-складские расходы")
@Api(value = "Транспортные затраты")
public class ZsrController {

    /**
     * Сервис для работы  с Заготовительно-складские расходы
     */
    @Autowired
    private ZsrService zsrService;

    /**
     * Получение информации о Заготовительно-складские расходы
     *
     * @param id - идентификатор Заготовительно-складские расходы
     * @return - информация о Заготовительно-складские расходы, http - статус
     */
    @GetMapping(value = "request")
    @ApiOperation(value = "Получение Заготовительно-складские расходы пустой id даст весь лист")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Запрос успешно обработан", response = Measure.class),
            @ApiResponse(code = 500, message = "Внутренняя ошибка сервера", response = ServiceError.class)
    })
    public ResponseEntity getById(@RequestParam Integer id) {
        return zsrService.getByIdZsr(id);
    }

    /**
     * Создание новой Заготовительно-складские расходы
     *
     * @param zsr - информация о Заготовительно-складские расходы
     * @return - информацияо Заготовительно-складские расходы, http - статус
     */
    @PostMapping(value = "request")
    @MetaAction(name = "request", description = "Создание Заготовительно-складские расходы")
    @ApiOperation(value = "Добавление новой Заготовительно-складские расходы")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Заготовительно-складские расходы успешно добавлены", response = Measure.class),
            @ApiResponse(code = 500, message = "Внутренняя ошибка сервера", response = ServiceError.class)
    })
    public ResponseEntity create(@RequestBody Zsr zsr) {
        return zsrService.createZsr(zsr);
    }

    /**
     * Редактирование Заготовительно-складские расходы
     *
     * @param zsr - информация о Заготовительно-складские расходы
     * @return - информацияо Заготовительно-складские расходы, http - статус
     */
    @PutMapping(value = "request")
    @MetaAction(name = "request", description = "Редактирование Заготовительно-складские расходы")
    @ApiOperation(value = "Редактирование Заготовительно-складские расходы")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Заготовительно-складские расходы успешно обновлена", response = Measure.class),
            @ApiResponse(code = 500, message = "Внутренняя ошибка сервера", response = ServiceError.class)
    })
    public ResponseEntity update(@RequestBody Zsr zsr) {
        return zsrService.updateZsr(zsr);
    }

    /**
     * Удаление Заготовительно-складские расходы
     *
     * @param id - id о Заготовительно-складские расходы
     * @return - http - статус
     */
    @DeleteMapping(value = "request")
    @MetaAction(name = "request", description = "Удаление Заготовительно-складские расходы")
    @ApiOperation(value = "Удаление Заготовительно-складские расходы")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Заготовительно-складские расходы успешно удалена"),
            @ApiResponse(code = 500, message = "Внутренняя ошибка сервера", response = ServiceError.class)
    })
    public ResponseEntity remove(@RequestParam Integer id) {
        return zsrService.removeZsr(id);
    }


}
