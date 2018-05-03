package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.WinterRiseList;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.WinterRiseListCurrent;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.SearchService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.WinterRiseListService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaAction;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaController;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;

/**
 * Controller by Overhead
 *
 * @author samsonov
 * @since 21.11.2017
 */
@RequestMapping(path = "v1/search/", produces = "application/json; charset=utf-8")
@RestController
@CrossOrigin
@MetaController(name = "WinterRiseList", description = "WinterRiseList")
@Api(value = "WinterRiseList")
public class SearchController {

    /**
     * WinterRiseList
     */
    @Autowired
    private SearchService searchService;


    /**
     * string
     *@param idPeriod idPeriod
     * @param row - string
     * @return - WinterRiseList, http - status
     */
    @GetMapping(value = "request")
    @MetaAction(name = "request", description = "get WinterRiseList")
    @ApiOperation(value = "get WinterRiseList")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully processed", responseContainer = "List", response = WinterRiseList.class),
            @ApiResponse(code = 500, message = "Inner server error", response = ServiceError.class)
    })
    public ResponseEntity getWinterRiseList(@RequestParam(name = "row", required = true) String row,
                                            @RequestParam(name = "idPeriod", required = true) String idPeriod) {
        return  searchService.searchByRow(row,idPeriod);
    }
}
