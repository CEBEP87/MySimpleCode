package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.CalculationsService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.PeriodService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaAction;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaController;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.impl.CalculationsServiceImpl.calculateDeviation;
import static ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.impl.CalculationsServiceImpl.manualInputValidation;

/**
 * Controller by period
 *
 * @author samsonov
 * @since 21.09.2017
 */
@RequestMapping(path = "v1/periods/", produces = "application/json; charset=utf-8")
@RestController
@CrossOrigin
@MetaController(name = "periods", description = "period")
@Api(value = "Periods")
public class PeriodController {

    /**
     * Service by period
     */
    @Autowired
    private PeriodService periodService;


    /**
     * InputCalculation Service
     */
    @Autowired
    private CalculationsService calculationsService;

    /**
     * Get last period
     * @return - period list, http - status
     */
    @GetMapping(value = "last-period")
    @MetaAction(name = "last-period", description = "get last periods")
    @ApiOperation(value = "get last period")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully processed", responseContainer = "List", response = Period.class),
            @ApiResponse(code = 500, message = "Inner server error", response = ServiceError.class)
    })
    public ResponseEntity getLastPeriods() {
        try {
            return new ResponseEntity(periodService.sortByTSN(periodService.lastPeriod()), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Inner server error", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Get  period by id
     * @param id - identficator of priod
     * @param gsmValues - get gsm Values boolean
     * @return - period list, http - status
     */
    @GetMapping(value = "request")
    @MetaAction(name = "request", description = "get periods")
    @ApiOperation(value = "get list period")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully processed", responseContainer = "List", response = Period.class),
            @ApiResponse(code = 500, message = "Inner server error", response = ServiceError.class)
    })
    public ResponseEntity getPeriods(@RequestParam(name = "id", required = false) String id,
                                     @RequestParam(name = "gsm-values", required = false) boolean gsmValues) {
        try {
            if(gsmValues)return new ResponseEntity(periodService.getEstimatedPrice(id), HttpStatus.OK);
            if (id == null) return new ResponseEntity(periodService.findPeriodsListWithDate(), HttpStatus.OK);
            return new ResponseEntity(periodService.sortByTSN(periodService.findOne(id)), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Inner server error", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Post  period
     * @param newPeriodFields - new period's fields
     * @return - period list, http - status
     */
    @PostMapping(value = "request")
    @MetaAction(name = "request", description = "post periods")
    @ApiOperation(value = "post period")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully processed", responseContainer = "List", response = Period.class),
            @ApiResponse(code = 500, message = "Inner server error", response = ServiceError.class)
    })
    public ResponseEntity postPeriods(@RequestBody PeriodFields newPeriodFields) {
      return periodService.postPeriod(newPeriodFields);
    }

    /**
     * put  period by id
     *
     * @param data - new period's fields
     * @return - period list, http - status
     */
    @PutMapping(value = "request")
    @MetaAction(name = "request", description = "put periods")
    @ApiOperation(value = "put list period")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully processed", responseContainer = "List", response = Period.class),
            @ApiResponse(code = 500, message = "Inner server error", response = ServiceError.class)
    })
    public ResponseEntity putPeriods(@RequestBody PutPeriod data) {
        return periodService.putPeriod(data);

    }


}
