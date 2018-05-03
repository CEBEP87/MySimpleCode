package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service;

import com.mongodb.DBObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Activity;

import java.util.List;

/**
 * Service for work with Activityes
 *
 * @author samsonov
 * @since 22.09.2017
 */
@Service
public interface ActivityService {

    /**
     * find list Activity springData
     *
     * @return -new Activity
     */
    List<DBObject> findAll();

    /**
     * find list Activity native
     *
     * @return -new Activity
     */
    List<DBObject> findAllNative() throws Exception;

    /**
     * find Activity
     *
     * @param id - identificator by Activity
     * @return -new Activity
     */
    Activity findOneById(String id);

    /**
     * find Activity by position
     *
     * @param position - identificator by Activity
     * @return -new Activity
     */
    Activity findOneByPosition(String position);

    /**
     * post Activity
     *
     * @param activity - posting Activity
     * @return -new Activity
     */
    Activity postActivity(Activity activity);

    /**
     * put Activity
     *
     * @param activity - put Activity
     * @return -new Activity
     */
    Activity putActivity(Activity activity);

    /**
     * delete Activity
     *
     * @param id - put Activity
     */
    void deleteActivity(String id);
    /**
     * choose return: find by id, or find by period
     * @param id - get object by id
     * @param position - get object by position
     *@return - list objects or one object
     */
    ResponseEntity getActivitysService(String id, String position);

}
