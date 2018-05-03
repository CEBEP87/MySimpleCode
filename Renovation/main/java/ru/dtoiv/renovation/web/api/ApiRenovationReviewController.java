package ru.XXXXXXXXX.renovation.web.api;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import ru.XXXXXXXXX.data.domain.CommentModel;
import ru.XXXXXXXXX.renovation.service.RenovationWorkflowService;

@RestController
@RequestMapping("/api/renovations")
@Slf4j
public class ApiRenovationReviewController {

    private final RenovationWorkflowService workflowService;

    @Autowired
    public ApiRenovationReviewController(RenovationWorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/{id}/sendOnReview")
    public ResponseEntity sendOnReview(@RequestBody CommentModel comment, @PathVariable final Integer id) {
        Assert.notNull(id);
        Assert.notNull(comment);
        try {
            return new ResponseEntity(workflowService.sendOnReview(id, comment), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity(e, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/{id}/confirmReview", method = RequestMethod.GET, produces = {"application/json; charset=UTF-8"})
    public ResponseEntity confirmReview(@PathVariable Integer id) {
        Assert.notNull(id);
        try {
            return new ResponseEntity(workflowService.confirmReview(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity(e, HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/{id}/rejectReview")
    public ResponseEntity rejectReview(@RequestBody CommentModel comment, @PathVariable final Integer id) {
        Assert.notNull(id);
        Assert.notNull(comment);
        try {
            return new ResponseEntity(workflowService.rejectReview(id, comment), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity(e, HttpStatus.CONFLICT);
        }
    }

}