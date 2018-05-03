package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.repository.specification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.service.ResourceSectionService;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Спецификация для ресурсов периода
 *
 * @author samsonov
 * @since 16.02.2017
 */
@Component
public class ResourcePeriodSpecifications {

    /**
     * Сервис для работы с разделами
     */
    @Autowired
    ResourceSectionService resourceSection;

    /**
     * Сервис для работы с разделами
     */
    private static ResourceSectionService resourceSectionService;

    /**
     * Инициализация бина - сервиса для работы с разделами
     */
    @PostConstruct
    public void initResourceSection() {
        resourceSectionService = this.resourceSection;
    }

    /**
     * Формирование динамического запроса
     *
     * @param resourcePeriod период ресурса
     * @return спецификация периода ресурса
     */
    public static Specification<ResourcePeriod> withDynamicQuery(final ResourcePeriod resourcePeriod) {
        return new Specification<ResourcePeriod>() {
            @Override
            public Predicate toPredicate(Root<ResourcePeriod> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
                if (resourcePeriod == null) {
                    throw new IllegalStateException("At least one parameter should be provided to construct complex query");
                }
                List<Predicate> predicates = new ArrayList<Predicate>();

                // Фильтры ресурса периода
                if (resourcePeriod.getId() != null) {
                    predicates.add(builder.and(builder.equal(root.get(ResourcePeriod_.id), resourcePeriod.getId())));
                }

                if (resourcePeriod.getFkCompany() != null) {
                    predicates.add(builder.and(builder.equal(root.get(ResourcePeriod_.fkCompany), resourcePeriod.getFkCompany())));
                }

                if (resourcePeriod.getIsDeleted() != null) {
                    predicates.add(builder.and(builder.equal(root.get(ResourcePeriod_.isDeleted), resourcePeriod.getIsDeleted())));
                }

//                if (resourcePeriod.getFkCompany() != null) {
//                    predicates.add(builder.and(builder.equal(root.get(ResourcePeriod_.fkCompany), resourcePeriod.getFkCompany())));
//                }

                // Фильтры периода
                if (resourcePeriod.getFkPeriod() != null) {
                    Join<ResourcePeriod, Period> resourcePeriodJoin = root.join(ResourcePeriod_.fkPeriod, JoinType.LEFT);
                    if (resourcePeriod.getFkPeriod().getId() != null) {
                        predicates.add(builder.and(builder.equal(resourcePeriodJoin.get(Period_.id),
                                resourcePeriod.getFkPeriod().getId())));
                    }
                }


                //Фильтры ресурса
                if (resourcePeriod.getFkResource() != null) {
                    Join<ResourcePeriod, Resource> resourceJoin = root.join(ResourcePeriod_.fkResource, JoinType.LEFT);
                    //Фильтры ресурса
                    if (resourcePeriod.getFkResource().getId() != null) {
                        predicates.add(builder.and(builder.equal(resourceJoin.get(Resource_.id),
                                resourcePeriod.getFkResource().getId())));
                    }
//                    //Фильтр сотрудника привязанного к ресурсу
//                    if (resourcePeriod.getFkResource().getEmployeeId() != null) {
//                        predicates.add(builder.and(builder.equal(resourceJoin.get(Resource_.employeeId),
//                                resourcePeriod.getFkResource().getEmployeeId())));
//                    }
                    //Фильтр метки "Удаления" ресурса
                    if (resourcePeriod.getFkResource().getIsDeleted() != null) {
                        predicates.add(builder.and(builder.equal(resourceJoin.get(Resource_.isDeleted),
                                resourcePeriod.getFkResource().getIsDeleted())));
                    }
                    //Фильтр типа ресурсов
                    if (resourcePeriod.getFkResource().getResourceType() != null) {
                        Join<Resource, ResourceType> resourceTypeJoin = resourceJoin.join(Resource_.resourceType, JoinType.LEFT);
                        if (resourcePeriod.getFkResource().getResourceType().getId() != null) {
                            predicates.add(builder.and(builder.equal(resourceTypeJoin.get(ResourceType_.id),
                                    resourcePeriod.getFkResource().getResourceType().getId())));
                        }
                    }

                    //Фильтры раздела
                    if (resourcePeriod.getFkResource().getResourceSection() != null) {
                        // Получение дочерних узлов раздела
                        List<ResourceSection> sections = resourceSectionService.
                                getResourceSectionNode(resourcePeriod.getFkResource().getResourceSection());
                        Join<Resource, ResourceSection> resourceSectionJoin = resourceJoin.join(Resource_.resourceSection, JoinType.LEFT);
                        if (resourcePeriod.getFkResource().getResourceSection().getId() != null) {
                            predicates.add(builder.and(resourceJoin.get(Resource_.resourceSection).in(sections)));
                        }
                        //Фильтр сотрудника привязанного к разделу
                        if (resourcePeriod.getFkResource().getResourceSection().getEmployeeId() != null) {
                            predicates.add(builder.and(builder.equal(resourceSectionJoin.get(ResourceSection_.employeeId),
                                    resourcePeriod.getFkResource().getResourceSection().getEmployeeId())));
                        }
                    }

                    //Фильтры статуса ресурса
                    if (resourcePeriod.getFkResource().getResourceStatus() != null) {
                        Join<Resource, ResourceStatus> resourceStatusJoin = resourceJoin.join(Resource_.resourceStatus, JoinType.LEFT);
                        if (resourcePeriod.getFkResource().getResourceStatus().getId() != null) {
                            predicates.add(builder.and(builder.equal(resourceStatusJoin.get(ResourceStatus_.id),
                                    resourcePeriod.getFkResource().getResourceStatus().getId())));
                        }
                    }

                    //Фильтры статуса действия ресурса
                    if (resourcePeriod.getFkResource().getResourceStatusAction() != null) {
                        Join<Resource, ResourceStatusAction> resourceStatusActionJoin =
                                resourceJoin.join(Resource_.resourceStatusAction, JoinType.LEFT);
                        if (resourcePeriod.getFkResource().getResourceStatusAction().getId() != null) {
                            predicates.add(builder.and(builder.equal(resourceStatusActionJoin.get(ResourceStatusAction_.id),
                                    resourcePeriod.getFkResource().getResourceStatusAction().getId())));
                        }
                    }
                    //Фильтры TSN ресурса
                    if (resourcePeriod.getFkResource().getCodeTSN() != null) {
                        predicates.add(builder.and(builder.equal(resourceJoin.get(Resource_.codeTSN),
                                resourcePeriod.getFkResource().getCodeTSN())));
                    }
//
                }

                Predicate[] predicatesArray = new Predicate[predicates.size()];
                return builder.and(predicates.toArray(predicatesArray));
            }
        };
    }
}
