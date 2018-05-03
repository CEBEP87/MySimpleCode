package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Activity;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Equipment;

import java.util.List;

/**
 * Activity Repository
 *
 * @author samsonov
 * @since 22.09.2017
 */
public interface ActivityRepository extends MongoRepository<Activity, String> {
    /**
     *  Find period by id
     *  @param id - period's id
      * @return - period
     */

    @Query(value ="{'id' : ?0}")
    Activity findOne(String id);

    /**
     *  Find period by id
     *  @param id - period's id
     * @return - period
     */

    @Query(value ="{'Position' : ?0}")
    Activity findOneByIdPosition(String id);


}
