package ru.XXXXXXXXX.renovation.web.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.XXXXXXXXX.data.service.audit.Limit;
import ru.XXXXXXXXX.renovation.domain.dto.FilterRenovationDto;
import ru.XXXXXXXXX.renovation.domain.entity.Building;
import ru.XXXXXXXXX.renovation.domain.entity.BuildingSite;
import ru.XXXXXXXXX.renovation.domain.entity.Renovation;
import ru.XXXXXXXXX.renovation.domain.filter.RenovationFilter;
import ru.XXXXXXXXX.renovation.service.RenovationService;

@RestController
@RequestMapping("/api/renovations")
@Slf4j
public class ApiRenovationController {
    private final RenovationService renovationService;


    @Autowired
    public ApiRenovationController(RenovationService renovationService) {
        this.renovationService = renovationService;
    }

    @PostMapping("/readAll")
    public FilterRenovationDto getShortList(@RequestBody RenovationFilter<Renovation> filter) {
        return renovationService.findByFilter(filter);
    }

    @GetMapping("/{renovationId}")
    public Renovation getRenovation(@PathVariable Integer renovationId) {
        return renovationService.findRenovation(renovationId);
    }

    @PutMapping("/renovation")
    public Object putRenovation(@RequestBody Object renovation) {
        return renovationService.updateRenovation(renovation);
    }

    @PostMapping("/building")
    public Building postBuilding(@RequestBody Building building)
    {
        return renovationService.postBuilding(building);
    }

    @PostMapping("/building-site")
    public BuildingSite postBuildingSite(@RequestBody BuildingSite buildingSite) {
        return renovationService.postBuildingSite(buildingSite);
    }

    @GetMapping(value = "/{id:\\d+}/hist", produces = "application/json;charset=UTF-8")
    @Transactional
    public String history(@PathVariable Integer id) {
        return renovationService.getHistory(id, new Limit(null, null));
    }

}
