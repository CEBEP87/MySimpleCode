package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.*;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Спецификация для периода
 *
 * @author samsonov
 * @since 14.03.2017
 */
public class PeriodSpecifications {

    /**
     * Формирование динамического запроса
     *
     * @param period - период
     * @return условия динамического запроса
     */
    public static Specification<Period> withDynamicQuery(final Period period) {
        return new Specification<Period>() {
            @Override
            public Predicate toPredicate(Root<Period> sectionRoot, CriteriaQuery<?> query, CriteriaBuilder builder) {
                if (period == null) {
                    throw new IllegalStateException("At least one parameter should be provided to construct complex query");
                }
                List<Predicate> predicates = new ArrayList<Predicate>();

                //Фильтры периода
                if (period.getId() != null) {
                    predicates.add(builder.and(builder.equal(sectionRoot.get(Period_.id), period.getId())));
                }

                //Фильтры статуса периода
                if (period.getStatus() != null) {
                    Join<Period, PeriodStatus> periodStatusJoin = sectionRoot.join(Period_.status, JoinType.LEFT);
                    if (period.getStatus().getId() != null) {
                        predicates.add(builder.and(builder.equal(periodStatusJoin.get(PeriodStatus_.id), period.getStatus().getId())));
                    }
                }

                //Фильтры типа ресурса
                if (period.getResourceType() != null) {
                    Join<Period, ResourceType> resourceTypeJoin = sectionRoot.join(Period_.resourceType, JoinType.LEFT);
                    if (period.getResourceType().getId() != null) {
                        predicates.add(builder.and(builder.equal(resourceTypeJoin.get(ResourceType_.id), period.getResourceType().getId())));
                    }
                }

                Predicate[] predicatesArray = new Predicate[predicates.size()];
                return builder.and(predicates.toArray(predicatesArray));
            }
        };
    }
}
