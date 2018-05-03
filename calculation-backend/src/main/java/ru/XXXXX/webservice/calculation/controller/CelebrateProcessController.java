package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Activity;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.CelebrateProcess;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.ActivityService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.CelebrateProcessService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaAction;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaController;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;

/**
 * Controller by  celebrateProcess
 *
 * @author samsonov
 * @since 21.11.2017
 */
@RequestMapping(path = "v1/celebrate-process/", produces = "application/json; charset=utf-8")
@RestController
@CrossOrigin
@MetaController(name = "celebrateProcess", description = "celebrateProcess")
@Api(value = "celebrateProcess")
public class CelebrateProcessController {

    /**
     * Activity service
     */
    @Autowired
    private CelebrateProcessService celebrateProcessService;

    /**
     * Get  celebrateProcess by id
     *
     * @param id - identficator of celebrateProcess
     *@param idPeriod - identficator of period
     * @return - celebrateProcess list, http - status
     */
    @GetMapping(value = "request")
    @MetaAction(name = "request", description = "get celebrateProcess")
    @ApiOperation(value = "get list celebrateProcess")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully processed", responseContainer = "List", response = CelebrateProcess.class),
            @ApiResponse(code = 500, message = "Inner server error", response = ServiceError.class)
    })
    public ResponseEntity getCelebrateProcess(@RequestParam(name = "id", required = false) String id,
                                      @RequestParam(name = "idPeriod", required = false) String idPeriod) {
        return  celebrateProcessService.getCelebrateProcessService(id,idPeriod);
    }


    /**
     * Post  celebrateProcess
     *
     * @param celebrateProcess - new celebrateProcess's fields
     * @return - celebrateProcess list, http - status
     */

    @PostMapping(value = "request")
    @MetaAction(name = "request", description = "post celebrateProcess")
    @ApiOperation(value = "post celebrateProcess")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully processed", responseContainer = "List", response = CelebrateProcess.class),
            @ApiResponse(code = 500, message = "Inner server error", response = ServiceError.class)
    })
    public ResponseEntity postCelebrateProcess(@RequestBody CelebrateProcess celebrateProcess) {
        return new ResponseEntity(celebrateProcessService.postCelebrateProcess(celebrateProcess), HttpStatus.OK);
    }


    /**
     * put  celebrateProcess by id
     *
     * @param celebrateProcess - celebrateProcess
     * @return - celebrateProcess list, http - status
     */
    @PutMapping(value = "request")
    @MetaAction(name = "request", description = "put celebrateProcess")
    @ApiOperation(value = "put list celebrateProcess")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully processed", responseContainer = "List", response = CelebrateProcess.class),
            @ApiResponse(code = 500, message = "Inner server error", response = ServiceError.class)
    })
    public ResponseEntity putCelebrateProcess(@RequestBody CelebrateProcess celebrateProcess) {
        return new ResponseEntity(celebrateProcessService.putCelebrateProcess(celebrateProcess), HttpStatus.OK);

    }

    /**
     * Delete CelebrateProcess by id
     *
     * @param id - CelebrateProcess identificator
     * @return - CelebrateProcess list, http - status
     */
    @DeleteMapping(value = "request")
    @MetaAction(name = "request", description = "put CelebrateProcess")
    @ApiOperation(value = "put list CelebrateProcess")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully processed", responseContainer = "List", response = CelebrateProcess.class),
            @ApiResponse(code = 500, message = "Inner server error", response = ServiceError.class)
    })
    public ResponseEntity putCelebrateProcess(@RequestParam(name = "id", required = false) String id) {
        celebrateProcessService.deleteCelebrateProcess(id);
        return new ResponseEntity("ok", HttpStatus.OK);
    }




    /**
     * Post  celebrateProcess
     *
     * @param celebrateProcess - new celebrateProcess's fields
     * @return - celebrateProcess list, http - status
     */

    @PostMapping(value = "request-with-confirm")
    @MetaAction(name = "request", description = "post celebrateProcess")
    @ApiOperation(value = "post celebrateProcess")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully processed", responseContainer = "List", response = CelebrateProcess.class),
            @ApiResponse(code = 500, message = "Inner server error", response = ServiceError.class)
    })
    public ResponseEntity postCelebrateProcessWithConfirm(@RequestBody CelebrateProcess celebrateProcess) {
        return celebrateProcessService.postCelebrateProcessWithConfirm(celebrateProcess);
    }


    /**
     * put  celebrateProcess by id
     *
     * @param celebrateProcess - celebrateProcess
     * @return - celebrateProcess list, http - status
     */
    @PutMapping(value = "request-with-confirm")
    @MetaAction(name = "request", description = "put celebrateProcess")
    @ApiOperation(value = "put list celebrateProcess")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully processed", responseContainer = "List", response = CelebrateProcess.class),
            @ApiResponse(code = 500, message =  "Inner server error", response = ServiceError.class)
    })
    public ResponseEntity putCelebrateProcessWithConfirm(@RequestBody CelebrateProcess celebrateProcess) {
        return celebrateProcessService.putCelebrateProcessWithConfirm(celebrateProcess);

    }

    /**
     * Delete CelebrateProcess by id
     *
     * @param id - CelebrateProcess identificator
     * @return - CelebrateProcess list, http - status
     */
    @DeleteMapping(value = "request-with-confirm")
    @MetaAction(name = "request", description = "put CelebrateProcess")
    @ApiOperation(value = "put list CelebrateProcess")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully processed", responseContainer = "List", response = CelebrateProcess.class),
            @ApiResponse(code = 500, message = "Inner server error", response = ServiceError.class)
    })
    public ResponseEntity DeleteCelebrateProcessWithConfirm(@RequestParam(name = "id", required = true) String id) {
        return  celebrateProcessService.deleteCelebrateProcessWithConfirm(id);
    }
}
