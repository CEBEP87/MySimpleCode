package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.Period;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.Resource;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.ResourcePeriod;

import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Репозиторий для ресурсов периода
 *
 * @author samsonov
 * @since 16.02.2017
 */
public interface ResourcePeriodRepository extends JpaRepository<ResourcePeriod, Integer>,
        JpaSpecificationExecutor<ResourcePeriod> {

    /**
     * Получение ресурсов в предидущем периоде
     *
     * @param resources  - список ресурсов
     * @param prevPeriod - период
     * @return - список ресурсов в предидущем периоде
     */
    @Query("select rp from ResourcePeriod rp where rp.fkPeriod = :prevPeriod and rp.fkResource in :resourcePeriodList")
    List<ResourcePeriod> getPrevResourcePeriodListByCurrentResourcePeriod(@Param("resourcePeriodList") List<Resource> resources,
                                                                          @Param("prevPeriod") Period prevPeriod);

    /**
     * Получение ресурсов в текущем периоде
     *
     * @param period            - период
     * @param isDeletedResource - метка "является ли удаленным ресурс"
     * @return - список ресурсов в предидущем периоде
     */
    @Query("select rp from ResourcePeriod rp" +
            " left join fetch rp.fkResource r" +
            " left join fetch rp.fkPeriod p" +
            " left join fetch p.status" +
            " left join fetch p.resourceType" +
            " left join fetch r.resourceType" +
            " left join fetch r.resourceSection" +
            " left join fetch r.measure" +
            " left join fetch r.resourceStatus" +
            " left join fetch r.resourceStatusAction" +
            " where rp.fkPeriod = :period and rp.isDeleted = :isDeletedResourcePeriod")
    List<ResourcePeriod> findAllResourcePeriodByPeriodJF(@Param("period") Period period,
                                                         @Param("isDeletedResourcePeriod") Boolean isDeletedResource);

    /**
     * Удаление ресурса из текущего периода
     *
     * @param res            -id ресурса
     * @param per            -id периода ресурса
     */
    @Modifying(clearAutomatically = true)
    @Query(value = ("DELETE FROM mon.resource_period where fk_resource= :res and fk_period= :per"),nativeQuery = true)
    void deleteByFkResource(@Param("res") Integer res,@Param("per") Integer per);


//    /**
//     * Получение ресурсов в предидущем периоде
//     *
//     * @param fkResources - список ресурсов
//     * @param fkPeriod    - период
//     * @return - список ресурсов в предидущем периоде
//     */
//    List<ResourcePeriod> findByFkPeriodAndFkResourceIn(List<Resource> fkResources, Period fkPeriod);

//    /**
//     * Получение ресурсов периода с минимальной ценой
//     * @param period - период
//     * @return - список ресурсов в предидущем периоде
//     */
//    @Query("select P.* from tovar T, ResourcePeriod P," +
//            "(select tovar.code, MIN(price.price) AS myfunction FROM tovar INNER JOIN price ON tovar.id = price.FK_tovar group by tovar.code) as MYSELECT\n"+
//            "where (T.id = P.FK_tovar and T.code = MYSELECT.code  and P.price = MYSELECT.myfunction)")
//    List<ResourcePeriod> getResourcePeriodListWithMinPriceByPeriod(@Param("period") Period period);
}
