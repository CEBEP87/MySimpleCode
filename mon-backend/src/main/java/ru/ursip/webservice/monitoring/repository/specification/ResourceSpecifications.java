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
 * Спецификация для ресурса
 *
 * @author samsonov
 * @since 13.02.2017
 */
@Component
public class ResourceSpecifications {

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
     * @param resource Ресурсы
     * @return условия динамического запроса
     */
    public static Specification<Resource> withDynamicQuery(final Resource resource) {
        return new Specification<Resource>() {
            @Override
            public Predicate toPredicate(Root<Resource> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
                if (resource == null) {
                    throw new IllegalStateException("At least one parameter should be provided to construct complex query");
                }
                List<Predicate> predicates = new ArrayList<Predicate>();

                //Фильтры ресурса
                if (resource.getId() != null) {
                    predicates.add(builder.and(builder.equal(root.get(Resource_.id), resource.getId())));
                }


                if (resource.getIsDeleted() != null) {
                    predicates.add(builder.and(builder.equal(root.get(Resource_.isDeleted),
                            resource.getIsDeleted())));
                }

                if (resource.getIsActive() != null) {
                    predicates.add(builder.and(builder.equal(root.get(Resource_.isActive),
                            resource.getIsActive())));
                }

                // Фильтры типа ресурсов
                if (resource.getResourceType() != null) {
                    Join<Resource, ResourceType> resourceTypeJoin = root.join(Resource_.resourceType, JoinType.LEFT);
                    if (resource.getResourceType().getId() != null) {
                        predicates.add(builder.and(builder.equal(resourceTypeJoin.get(ResourceType_.id),
                                resource.getResourceType().getId())));
                    }
                }

                //Фильтры раздела
                if (resource.getResourceSection() != null) {
                    // Получение дочерниз узлов раздела
                    List<ResourceSection> sections = null;
                    if (resource.getResourceSection().getId() !=null) {
                        ResourceSection sectionInDb = null;
                        try {
                            sectionInDb = resourceSectionService.get(resource.getResourceSection().getId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sections = resourceSectionService.getResourceSectionNode(sectionInDb);
                    }
                    Join<Resource, ResourceSection> resourceSectionJoin = root.join(Resource_.resourceSection, JoinType.LEFT);
                    if (sections != null) {
//                        predicates.add(builder.and(builder.equal(resourceSectionJoin.get(ResourceSection_.id),
//                                resource.getResourceSection().getId())));
                        predicates.add(builder.and(root.get(Resource_.resourceSection).in(sections)));
                    }
                    if (resource.getResourceSection().getEmployeeId() != null) {
                        predicates.add(builder.and(builder.equal(resourceSectionJoin.get(ResourceSection_.employeeId),
                                resource.getResourceSection().getEmployeeId())));
                    }
                    //                    //Фильтр сотрудника привязанного к ресурсу
//                    if (resourcePeriod.getFkResource().getEmployeeId() != null) {
//                        predicates.add(builder.and(builder.equal(resourceJoin.get(Resource_.employeeId),
//                                resourcePeriod.getFkResource().getEmployeeId())));
//                    }


                    if (resource.getResourceSection().getIsDeleted() != null) {
                        predicates.add(builder.and(builder.equal(resourceSectionJoin.get(ResourceSection_.isDeleted),
                                resource.getResourceSection().getIsDeleted())));
                    }
                }

                //Фильтры статуса
                if (resource.getResourceStatus() != null) {
                    Join<Resource, ResourceStatus> resourceStatusJoin = root.join(Resource_.resourceStatus, JoinType.LEFT);
                    if (resource.getResourceStatus().getId() != null) {
                        predicates.add(builder.and(builder.equal(resourceStatusJoin.get(ResourceStatus_.id),
                                resource.getResourceStatus().getId())));
                    }
                }

                //Фильтры статуса действий
                if (resource.getResourceStatusAction() != null) {
                    Join<Resource, ResourceStatusAction> resourceStatusActionJoin = root.join(Resource_.resourceStatusAction, JoinType.LEFT);
                    if (resource.getResourceStatusAction().getId() != null) {
                        predicates.add(builder.and(builder.equal(resourceStatusActionJoin.get(ResourceStatusAction_.id),
                                resource.getResourceStatusAction().getId())));
                    }
                }




                Predicate[] predicatesArray = new Predicate[predicates.size()];
                return builder.and(predicates.toArray(predicatesArray));
            }
        };
    }
}
