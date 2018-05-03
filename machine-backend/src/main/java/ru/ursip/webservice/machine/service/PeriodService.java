package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.*;

import java.util.List;
import java.util.Map;

/**
 * Service for work with periods
 *
 * @author samsonov
 * @since 22.09.2017
 */
@Service
public interface PeriodService {

    /**
     * Get period's list
     *
     * @return - period's list
     * @throws Exception
     */
    List<PeriodList> findPeriodsList() throws Exception;

    /**
     * Get period's list whit date
     *
     * @return - period's list
     * @throws Exception
     */
    List<PeriodListWithDate> findPeriodsListWithDate() throws Exception;

    /**
     * Get one period by id
     *
     * @param id     -period's identificator
     * @return - period
     * @throws Exception
     */
    Period findOne(String id)  throws Exception;

    /**
     * Get one last period
     *
     * @return - period
     * @throws Exception
     */
    Period lastPeriod()  throws Exception;

    /**
     * Get one previous period
     * @param periodId - period id
     * @return - period
     * @throws Exception
     */
    Period previousPeriod(String periodId)  throws Exception;

    /**
     * Add New Period
     *
     * @param period     -period
     * @return -new period
     * @throws Exception
     */
    Period save(Period period)  throws Exception;

    /**
     * Vallidation fields not null
     *
     * @param newPeriodFields     -period's fields
     * @return -new period
     * @throws Exception
     */
    Boolean validation(PeriodFields newPeriodFields)  throws Exception;

    /**
     * Sort resources by TSN
     *
     * @param period     -period's fields
     * @return -sorted resources in period
     * @throws Exception
     */
    Period sortByTSN(Period period)  throws Exception;

    /**
     * Post Period
     *
     * @param newPeriodFields     -period's fields
     * @return -period
     */
    ResponseEntity postPeriod(PeriodFields newPeriodFields);

    /**
     * get Estimated Price By Monitoring
     *
     * @param id     -id period By Monitoring
     * @return -data
     */

    Map<String, String> getEstimatedPrice(String id);

    /**
     * put Period
     *
     * @param data     -period's fields
     * @return -period
     */
    ResponseEntity putPeriod(PutPeriod data);
 }
