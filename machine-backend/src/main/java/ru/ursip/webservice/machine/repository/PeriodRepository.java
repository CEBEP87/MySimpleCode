package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.repository;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.Period;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.PeriodList;
import org.springframework.data.mongodb.core.MongoTemplate;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

/**
 * Period Repository
 *
 * @author samsonov
 * @since 22.09.2017
 */
public interface PeriodRepository extends MongoRepository<Period, String> {
    /**
     *  Find period by id
     *  @param id - period's id
      * @return - period
     */

    @Query(value ="{'id' : ?0}")
    Period findOne(String id);

    /**
     *  Find period by id whithout costItems
     *  @param id - period's id
     * @return - period
     */
    @Query(value ="{'id' : ?0}" ,fields = "{\"resource.cost_items\":0}")
    Period findOneWithoutCostItems(String id);



}
