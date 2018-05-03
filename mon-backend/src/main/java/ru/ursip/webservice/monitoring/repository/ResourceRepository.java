package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.Resource;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.ResourceSection;

import java.util.List;

/**
 * Репозиторий для ресурсов
 *
 * @author samsonov
 * @since 13.02.2017
 */
public interface ResourceRepository extends JpaRepository<Resource, Integer>, JpaSpecificationExecutor<Resource> {

    /**
     * Получение количества ресурсов для раздела
     *
     * @param resourceSection - радел ресурса
     * @return - количесво ресурсов в разделе
     */
    @Query("select count(r) from Resource r where r.resourceSection = :resourceSection and r.isDeleted = false and r.isActive = true")
    Integer countResourceSectionResources(@Param("resourceSection") ResourceSection resourceSection);

//    /**
//     * Получение количества ресурсов для сотрудника
//     *
//     * @param employeeId - идентификатор сотрудника
//     * @return - количесво ресурсов в разделе
//     */
//    @Query("select count(r) from Resource r where r.employeeId = :employeeId and r.isDeleted = false and r.isActive = true")
//    Integer countResourcesByEmployeeId(@Param("employeeId") Integer employeeId);

    /**
     * Получение ресурса, который является заменой для заданного ресурса
     *
     * @param resourceId - идентификатор ресурса(идентификатор заданного ресурса)
     * @return - замена заданного ресурса
     */
    @Query("select r from Resource r where r.resourceRoot = :resourceId")
    Resource getResourceReplacement(@Param("resourceId") Integer resourceId);

    /**
     * Получение списка ресурсов, которые являются заменой
     *
     * @return - список ресурсов, которые являются заменой
     */
    @Query("select r from Resource r where r.resourceRoot is not null")
    List<Resource> getResourcesReplacement();

    /**
     * Получение списка ресурсов по заданному ТСН
     *
     * @param tsnString - строка с ТСН
     * @return - список ресурсов по заданному ТСН
     */
    @Query("select r from Resource r where r.codeTSN = :tsn and r.isDeleted = false and r.isActive = true")
    List<Resource> getResourcesByTSN(@Param("tsn") String tsnString);

    /**
     * Получение списка новых утвержденных ресурсов(для добавления в новый период)
     *
     * @param resourceType - Номер типа ресурсов
     * @param fkResourceStatusAction - Статус ресурса
     * @return - список утвержденных ресурсов
     */
    @Query("select r from Resource r where FK_RESOURCE_STATUS_action = :fkResourceStatusAction and FK_RESOURCE_STATUS = 4 and FK_RESOURCE_TYPE= :resourceType")
    List<Resource> getResourcesStatement(@Param("resourceType") Integer resourceType,@Param("fkResourceStatusAction") Integer fkResourceStatusAction);

    /**
     * Добавление ресурсов в выбранный период
     *
     * @param periodId - Идентификатор периода
     * @param resourceId - Идентификатор периода
     * @param companyName - Имя компании
     *
     */
    @Query(value = ("INSERT INTO resource_period(FK_PERIOD,FK_RESOURCE,FK_COMPANY) VALUES (:periodId, :resourceId,\':companyName\')"),nativeQuery = true)
     void setAddedResource(@Param("periodId") Integer periodId,@Param("resourceId") Integer resourceId,@Param("companyName") String companyName );



    /**
     * Обновление StatusAction для Ресурса
     *
     * @param resourceId - идентификатор ресурса
     * @param newStatusAction - параметр столбца status_action
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Resource r SET FK_RESOURCE_STATUS_action= :newStatusAction WHERE r.id= :resourceId")
    void updateResourceStatusAction(@Param("resourceId") Integer resourceId, @Param("newStatusAction") int newStatusAction);


    /**
     * Обновление StatusAction для Ресурса
     *
     * @param resourceId - идентификатор ресурса
     * @param newStatus - параметр столбца status
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Resource r SET fk_resource_status= :newStatus WHERE id= :resourceId")
    void updateResourceStatus(@Param("resourceId") Integer resourceId, @Param("newStatus") Integer newStatus);

    /**
     * Удаление по идентификатору (стандартный delete падает с ошибкой)
     *
     * @param resourceId - идентификатор ресурса
     */

    @Modifying(clearAutomatically = true)
    @Query(value = ("delete FROM mon.resource where id=:resourceId"),nativeQuery = true)
    void deleteById(@Param("resourceId") Integer resourceId);

    /**
//     * Назначение сотрудника на ресурсы списка разделов
//     *
//     * @param employeeId - идентификатор ресурса
//     * @param sections   - список разделов
//     */


//    @Modifying(clearAutomatically = true)
//    @Query("update Resource r set r.employeeId =:employeeId where r.resourceSection in :sections")
//    void setEmployeeByResourceSectionList(@Param("employeeId") Integer employeeId, @Param("sections") List<ResourceSection> sections);
}
