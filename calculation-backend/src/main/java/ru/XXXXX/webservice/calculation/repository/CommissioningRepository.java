package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.CelebrateProcess;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Commissioning;

import java.util.List;

/**
 * Commissioning Repository
 *
 * @author samsonov
 * @since 22.09.2017
 */
public interface CommissioningRepository extends MongoRepository<Commissioning, String> {
    /**
     *  Find period by id
     *  @param id - period's id
      * @return - period
     */

    @Query(value ="{'id' : ?0}")
    Commissioning findOne(String id);

    /**
     *  Find period by id
     *  @param id - period's id
     * @return - period
     */

    @Query(value ="{'id_period' : ?0}")
    List<Commissioning> findOneByIdPeriod(String id);

    /**
     * Find by TSN
     *
     * @param tsn - object's tsn
     * @return - period
     */
    @Query(value = "{'pressmark' : ?0}")
    List<Commissioning> findByTSN(String tsn);

    /**
     * deleteByIdPeriod
     *
     * @param idPeriod - idPeriod
     */
    void deleteByIdPeriod(String idPeriod);


    /**
     * find Title by regex
     *
     * @param row - row
     * @param idPeriod - idPeriod
     * @return - List
     */
    @Query("{ 'Title' : { $regex: ?0 } ,'id_period' : ?1}")
    List<Commissioning> findTitleByRegex(String row,String idPeriod);
    /**
     * find pressmark by regex
     *
     * @param row - row
     * @param idPeriod - idPeriod
     * @return - List
     */
    @Query("{ 'pressmark' : { $regex: ?0 } ,'id_period' : ?1}")
    List<Commissioning> findPressmarkByRegex(String row,String idPeriod);
    /**
     * find OKP by regex
     *
     * @param row - row
     * @param idPeriod - idPeriod
     * @return - List
     */
    @Query("{ 'OKP' : { $regex: ?0 },'id_period' : ?1 }")
    List<Commissioning> findOKPByRegex(String row,String idPeriod);
    /**
     * find grounding by regex
     *
     * @param row - row
     * @param idPeriod - idPeriod
     * @return - List
     */
    @Query("{ 'Grounding' : { $regex: ?0 },'id_period' : ?1 }")
    List<Commissioning> findGroundingByRegex(String row,String idPeriod);
}
