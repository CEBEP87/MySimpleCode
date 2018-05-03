package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.CelebrateProcess;

import java.util.List;

/**
 * CelebrateProcess Repository
 *
 * @author samsonov
 * @since 22.09.2017
 */
public interface CelebrateProcessRepository extends MongoRepository<CelebrateProcess, String> {
    /**
     * Find  by id
     *
     * @param id - period's id
     * @return - period
     */
    @Query(value = "{'id' : ?0}")
    CelebrateProcess findOne(String id);

    /**
     * Find  by id period
     *
     * @param id - period's id
     * @return - period
     */
    @Query(value = "{'id_period' : ?0}")
    List<CelebrateProcess> findOneByIdPeriod(String id);

    /**
     * Find by TSN
     *
     * @param tsn - object's tsn
     * @return - period
     */
    @Query(value = "{'pressmark' : ?0}")
    List<CelebrateProcess> findByTSN(String tsn);

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
    List<CelebrateProcess> findTitleByRegex(String row,String idPeriod);
    /**
     * find pressmark by regex
     *
     * @param row - row
     * @param idPeriod - idPeriod
     * @return - List
     */
    @Query("{ 'pressmark' : { $regex: ?0 } ,'id_period' : ?1}")
    List<CelebrateProcess> findPressmarkByRegex(String row,String idPeriod);
    /**
     * find OKP by regex
     *
     * @param row - row
     * @param idPeriod - idPeriod
     * @return - List
     */
    @Query("{ 'OKP' : { $regex: ?0 },'id_period' : ?1 }")
    List<CelebrateProcess> findOKPByRegex(String row,String idPeriod);
    /**
     * find grounding by regex
     *
     * @param row - row
     * @param idPeriod - idPeriod
     * @return - List
     */
    @Query("{ 'Grounding' : { $regex: ?0 },'id_period' : ?1 }")
    List<CelebrateProcess> findGroundingByRegex(String row,String idPeriod);
}
