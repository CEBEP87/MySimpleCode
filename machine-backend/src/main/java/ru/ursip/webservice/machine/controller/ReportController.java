package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.CalculationsService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.ReportService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.exceptions.ServiceErrorException;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaAction;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.meta.annotations.MetaController;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * Controller by report
 *
 * @author samsonov
 * @since 21.09.2017
 */
@RequestMapping(path = "v1/reports/", produces = "application/json; charset=utf-8")
@RestController
@CrossOrigin
@MetaController(name = "reports", description = "reports")
@Api(value = "Reports")
public class ReportController {

    /**
     * Object Logging
     */
    private Logger logger = LoggerFactory.getLogger(ReportController.class);

    /**
     * Service by period
     */
    @Autowired
    private ReportService reportService;

    /**
     * InputCalculation Service
     */

    @Autowired
    private CalculationsService calculationsService;

    /**
     * Report index-rise
     *
     * @param response -responce
     * @param periodId - period's id
     */
    @GetMapping(value = "index-rise")
    @MetaAction(name = "otchet", description = "create report")
    @ApiOperation(value = "get report rise index")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Запрос успешно обработан"),
            @ApiResponse(code = 409, message = "Во время обработки запроса произошел конфликт", response = ServiceError.class),
            @ApiResponse(code = 412, message = "Полученные данные не прошли валидацию", response = ServiceError.class),
            @ApiResponse(code = 500, message = "Внутренняя ошибка сервера", response = ServiceError.class)
    })
    public void getReport(@RequestParam(value = "idPeriod", required = true) String periodId,
                          HttpServletResponse response) throws IOException {
        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response);
        try {
            Workbook report = reportService.createExcelReportIndexRise(periodId);
            wrapper.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8");
            wrapper.setHeader("Content-disposition", "attachment;filename=monitoringReport.xlsx");
            report.write(wrapper.getOutputStream());
        } catch (ServiceErrorException serviceErr) {
            ServiceError error = new ServiceError(serviceErr.getMessage(), null, null);
            wrapper.setContentType("application/json; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(error));
        } catch (Exception e) {
            logger.error("Ошибка при формировании отчета", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Report current-index
     *
     * @param response -responce
     * @param periodId - идентификатор периода
     */
    @GetMapping(value = "index-current")
    @MetaAction(name = "otchet", description = "Сформировать отчет")
    @ApiOperation(value = "Получение отчета по ценам поставщиков ресурсов в периоде")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Запрос успешно обработан"),
            @ApiResponse(code = 409, message = "Во время обработки запроса произошел конфликт", response = ServiceError.class),
            @ApiResponse(code = 412, message = "Полученные данные не прошли валидацию", response = ServiceError.class),
            @ApiResponse(code = 500, message = "Внутренняя ошибка сервера", response = ServiceError.class)
    })
    public void getReportCurrentIndex(@RequestParam(value = "idPeriod", required = true) String periodId,
                                      HttpServletResponse response) throws IOException {
        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response);
        try {
            Workbook report = reportService.createExcelReportCurrentIndex(periodId);
            wrapper.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8");
            wrapper.setHeader("Content-disposition", "attachment;filename=monitoringReport.xlsx");
            report.write(wrapper.getOutputStream());
        } catch (ServiceErrorException serviceErr) {
            ServiceError error = new ServiceError(serviceErr.getMessage(), null, null);
            wrapper.setContentType("application/json; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(error));
        } catch (Exception e) {
            logger.error("Ошибка при формировании отчета", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}