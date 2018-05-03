package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Period;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.DigestService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaAction;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaController;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;

import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Digest Coeficient Controller
 */
@RequestMapping(path = "v1/digest/", produces = "application/json; charset=utf-8")
@RestController
@CrossOrigin
@MetaController(name = "digest", description = "digest")
@Api(value = "digest")
public class DigestCoeficientController {
    /**
     * Logger
     */

    private static Logger log =
            Logger.getLogger(DigestCoeficientController.class.getName());
    /**
     * digestService
     */

    @Autowired
    private DigestService digestService;
    /**
     * Path download prices directory
     */
    private static final String fileLogDir = "./fileLog/";


    /**
     * Get  resource by id
     *
     * @param idPeriod idPeriod
     * @param response response
     * @param type     response
     * @return ResponseEntity
     */
    @GetMapping(value = "request")
    @MetaAction(name = "request", description = "get digest")
    @ApiOperation(value = "get  digest")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully processed", responseContainer = "List", response = Period.class),
            @ApiResponse(code = 500, message = "Inner server error", response = ServiceError.class)
    })
    public ResponseEntity getDigest(@RequestParam(name = "idPeriod", required = true) String idPeriod,
                                    @RequestParam(name = "type", required = true) String type,
                                    HttpServletResponse response) {
        return digestService.getDigest(idPeriod, response, type,fileLogDir);
    }
}
