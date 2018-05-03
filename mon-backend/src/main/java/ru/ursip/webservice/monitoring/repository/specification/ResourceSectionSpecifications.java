package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.ResourceSection;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.ResourceSection_;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Спецификация для разделов ресурса
 *
 * @author aneichikes
 * @since 01.02.2017
 */
public class ResourceSectionSpecifications {

	/**
	 * Формирование динамического запроса
	 *
	 * @param section разделы ресурса
	 * @return условия динамического запроса
	 */
	public static Specification<ResourceSection> withDynamicQuery(final ResourceSection section) {
		return new Specification<ResourceSection>() {
			@Override
			public Predicate toPredicate(Root<ResourceSection> sectionRoot, CriteriaQuery<?> query, CriteriaBuilder builder) {
				if (section == null) {
					throw new IllegalStateException("At least one parameter should be provided to construct complex query");
				}
				List<Predicate> predicates = new ArrayList<Predicate>();

				//Фильтры раздела
				if (section.getId() != null) {
					predicates.add(builder.and(builder.equal(sectionRoot.get(ResourceSection_.id), section.getId())));
				}

				if (section.getIsDeleted() != null) {
					predicates.add(builder.and(builder.equal(sectionRoot.get(ResourceSection_.isDeleted), section.getIsDeleted())));
				}

                if (section.getSectionRoot() != null) {
                    predicates.add(builder.and(builder.equal(sectionRoot.get(ResourceSection_.sectionRoot), section.getSectionRoot())));
                }

				Predicate[] predicatesArray = new Predicate[predicates.size()];
				return builder.and(predicates.toArray(predicatesArray));
			}
		};
	}
}
