package ru.XXXXXXXXX.renovation.domain.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Builder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.XXXXXXXXX.data.dao.filterentity.SearchFilterSpecification;
import ru.XXXXXXXXX.data.dao.filterentity.SortFilterSpecification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class RenovationFilter<T> implements Specification<T> {

    private Integer page;
    private Integer size;

    private String search;
    private Set<Integer> type;
    private List<String> sort;
    private Set<Integer> state;
    private Set<Integer> area;
    private Set<Integer> okrug;

    @JsonIgnore
    private static final List<String> SORT_COLUMNS = Arrays.asList("address", "areaName", "lastChangeDate");
    @JsonIgnore
    private static final List<String> SEARCH_STRING_PATH = Arrays.asList("address");
    @JsonIgnore
    private static final Sort SORT_DEFAULT = new Sort(
            new Sort.Order(Sort.Direction.ASC, "areaName"),
            new Sort.Order(Sort.Direction.ASC, "address")
    );

    private HashSet<String> renovationTypes() {
        return type.stream()
                .map(type -> type == 72083001 ? "Building" : "Building_Site")
                .collect(Collectors.toCollection(HashSet<String>::new));
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicateList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(this.getType())) {
            predicateList.add(root.get("type").in(this.renovationTypes()));
        }
        if (CollectionUtils.isNotEmpty(this.getState())) {
            predicateList.add(root.get("status").in(this.getState()));
        }
        if (CollectionUtils.isNotEmpty(this.getArea())) {
            predicateList.add(root.get("district").in(this.getArea()));
        }
        if (CollectionUtils.isNotEmpty(this.getOkrug())) {
            predicateList.add(root.get("okrug").in(this.getOkrug()));
        }
        SearchFilterSpecification searchFilterSpecification =
                new SearchFilterSpecification(this.getSearch(), Collections.EMPTY_LIST, SEARCH_STRING_PATH);
        if (StringUtils.isNotEmpty(this.getSearch()) && searchFilterSpecification != null) {
            predicateList.add(searchFilterSpecification.toPredicate(root, criteriaQuery, criteriaBuilder));
        }

        return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
    }

    @JsonIgnore
    public PageRequest getPageRequest() {

        if (page == null || size == null) return new PageRequest(0, 20, this.getSortForPageRequest());

        int validPage = page == null || page < 0 ? 0 : page;
        int validSize =  size == null || size <= 0 ? Integer.MAX_VALUE : size;
        return new PageRequest(validPage, validSize, this.getSortForPageRequest());
    }

    @JsonIgnore
    public final Sort getSortForPageRequest() {
        SortFilterSpecification sortFilterSpecification = new SortFilterSpecification(sort, SORT_COLUMNS, SORT_DEFAULT);

        return sortFilterSpecification == null ? SORT_DEFAULT : sortFilterSpecification.getSort();
    }
}