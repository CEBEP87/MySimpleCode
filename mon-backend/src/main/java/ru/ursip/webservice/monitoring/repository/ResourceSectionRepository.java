package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.ResourceSection;

import java.util.List;
import java.util.Set;

/**
 * Репозиторий для разделов ресурсов
 *
 * @author aneichikes
 * @since 23.01.2017
 */
public interface ResourceSectionRepository extends JpaRepository<ResourceSection, Integer>, JpaSpecificationExecutor<ResourceSection> {

    /**
     * Получение количества дочерних узлов для раздела
     *
     * @param resourceSectionId - идентификатор раздела
     * @return - количесвто дочерних узлов
     */
    @Query("select count(rs) from ResourceSection rs where rs.sectionRoot = :sectionRoot")
    Integer countResourceSectionChild(@Param("sectionRoot") Integer resourceSectionId);


    /**
     * Получение дочерних разделов ресурсов
     *
     * @param resourceSectionHedId - идентификатор родителя
     * @param isDelete             - только удаленные(метка удаленные)
     * @return - список дочерних разделов ресурсов
     */
    @Query("select rs from ResourceSection rs where rs.sectionRoot = :resourceSectionHeadId and rs.isDeleted = :isDelete")
    List<ResourceSection> findResourceSectionChild(@Param("resourceSectionHeadId") Integer resourceSectionHedId,
                                                   @Param("isDelete") Boolean isDelete);

    /**
     * Получение корневых разделов ресурсов
     *
     * @param isDeleted - только удаленные(метка удаленные)
     * @return - список корневых разделов ресурсов
     */
    @Query("select rs from ResourceSection rs where rs.sectionRoot is null and rs.isDeleted = :isDeleted")
    List<ResourceSection> findRootResourceSections(@Param("isDeleted") Boolean isDeleted);

    /**
     * Получение списка разделов ресурсов с заданнй меткой "Удален"
     *
     * @param isDeleted - только удаленные(метка удаленные)
     * @return - список разделов ресурсов с заданнй меткой "Удален"
     */
    @Query("select rs from ResourceSection rs where rs.isDeleted = :isDeleted")
    List<ResourceSection> findResourceSectionsWithIsDeleted(@Param("isDeleted") Boolean isDeleted);

    /**
     * Получение дочерних разделов
     *
     * @param resourceSectionId - идентификатор раздела родиетля
     * @return - список дочерних подразделений
     */
    @Query("select rs from ResourceSection rs where rs.sectionRoot = :headResourceSection")
    List getResourceSectionChild(@Param("headResourceSection") Integer resourceSectionId);
}
