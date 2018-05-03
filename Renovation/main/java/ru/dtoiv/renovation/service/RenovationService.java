package ru.XXXXXXXXX.renovation.service;

import org.springframework.transaction.annotation.Transactional;
import ru.XXXXXXXXX.data.service.audit.Limit;
import ru.XXXXXXXXX.renovation.domain.dto.FilterRenovationDto;
import ru.XXXXXXXXX.renovation.domain.entity.Building;
import ru.XXXXXXXXX.renovation.domain.entity.BuildingSite;
import ru.XXXXXXXXX.renovation.domain.entity.Renovation;
import ru.XXXXXXXXX.renovation.domain.filter.RenovationFilter;

public interface RenovationService {
    FilterRenovationDto findByFilter(RenovationFilter<Renovation> filter);

    @Transactional
    Renovation findRenovation(Integer renovationId);

    @Transactional
    Building updateBuilding(Building building);

    @Transactional
    BuildingSite updateBuildingSite(BuildingSite buildingSite);

    String getHistory(Number id, Limit limit);

    @Transactional
    Building postBuilding(Building building);
    @Transactional
    BuildingSite postBuildingSite(BuildingSite buildingSite);
    @Transactional
    Renovation findOne(int i);

    @Transactional
    Object updateRenovation(Object buildingSite);

}
