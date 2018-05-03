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
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.ActivityService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaAction;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaController;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;

/**
 * Controller by Activity
 *
 * @author samsonov
 * @since 21.11.2017
 */
@RequestMapping(path = "v1/activity/", produces = "application/json; charset=utf-8")
@RestController
@CrossOrigin
@MetaController(name = "activity", description = "Activity")
@Api(value = "activity")
public class ActivityController {

    /**
     * Activity service
     */
    @Autowired
    private ActivityService activityService;

    /**
     * Get  Activity by id
     *
     * @param id - identficator of Activity
     *@param position - positioin
     * @return - Activity list, http - status
     */
    @GetMapping(value = "request")
    @MetaAction(name = "request", description = "get activity")
    @ApiOperation(value = "get list Activity")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully processed", responseContainer = "List", response = Activity.class),
            @ApiResponse(code = 500, message = "Inner server error", response = ServiceError.class)
    })
    public ResponseEntity getActivity(@RequestParam(name = "id", required = false) String id,
                                      @RequestParam(name = "position", required = false) String position) {
        return  activityService.getActivitysService(id,position);
    }


    /**
     * Post  Activity
     *
     * @param Activity - new Activity's fields
     * @return - Activity list, http - status
     */

    @PostMapping(value = "request")
    @MetaAction(name = "request", description = "post activity")
    @ApiOperation(value = "post Activity")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully processed", responseContainer = "List", response = Activity.class),
            @ApiResponse(code = 500, message = "Inner server error", response = ServiceError.class)
    })
    public ResponseEntity postActivity(@RequestBody Activity Activity) {
        return new ResponseEntity(activityService.postActivity(Activity), HttpStatus.OK);
    }


    /**
     * put  Activity by id
     *
     * @param Activity - Activity
     * @return - Activity list, http - status
     */
    @PutMapping(value = "request")
    @MetaAction(name = "request", description = "put activity")
    @ApiOperation(value = "put list Activity")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully processed", responseContainer = "List", response = Activity.class),
            @ApiResponse(code = 500, message = "Inner server error", response = ServiceError.class)
    })
    public ResponseEntity putActivity(@RequestBody Activity Activity) {
        return new ResponseEntity(activityService.putActivity(Activity), HttpStatus.OK);

    }

    /**
     * Delete Activity by id
     *
     * @param id - Activity identificator
     * @return - Activity list, http - status
     */
    @DeleteMapping(value = "request")
    @MetaAction(name = "request", description = "put activity")
    @ApiOperation(value = "put list Activity")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request successfully processed", responseContainer = "List", response = Activity.class),
            @ApiResponse(code = 500, message = "Inner server error", response = ServiceError.class)
    })
    public ResponseEntity putActivity(@RequestParam(name = "id", required = false) String id) {
        activityService.deleteActivity(id);
        return new ResponseEntity("ok", HttpStatus.OK);

    }
}
