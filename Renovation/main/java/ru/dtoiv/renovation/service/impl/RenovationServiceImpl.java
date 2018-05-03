package ru.XXXXXXXXX.renovation.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.XXXXXXXXX.data.jpa.entity.analyse.AnalyseConstants;
import ru.XXXXXXXXX.data.service.audit.AuditService;
import ru.XXXXXXXXX.data.service.audit.Limit;
import ru.XXXXXXXXX.data.service.history.HistoryService;
import ru.XXXXXXXXX.dictionary.domain.entity.DictionaryElementShort;
import ru.XXXXXXXXX.renovation.domain.dto.FilterRenovationDto;
import ru.XXXXXXXXX.renovation.domain.dto.SummaryDto;
import ru.XXXXXXXXX.renovation.domain.entity.*;
import ru.XXXXXXXXX.renovation.domain.filter.RenovationFilter;
import ru.XXXXXXXXX.renovation.repository.RenovationRepository;
import ru.XXXXXXXXX.renovation.service.RenovationService;
import ru.XXXXXXXXX.renovation.service.RenovationWorkflowService;
import ru.XXXXXXXXX.user.domain.entity.UserEntity;
import ru.XXXXXXXXX.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Service Implementation for managing Renovation.
 */
@Transactional
@Service
public class RenovationServiceImpl implements RenovationService {
    private final RenovationRepository repository;
    private final ObjectMapper objectMapper;
    private final HistoryService auditHistoryService;
    private final UserService userService;


    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private AuditService auditService;


    @Autowired
    private RenovationWorkflowService renovationWorkflowService;

    @Autowired
    public RenovationServiceImpl(UserService userService, RenovationRepository repository, ObjectMapper objectMapper, HistoryService auditHistoryService) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.auditHistoryService = auditHistoryService;
        this.userService = userService;
    }

    @Transactional
    @Override
    public FilterRenovationDto findByFilter(RenovationFilter<Renovation> filter) {
        Page<Renovation> page = repository.findAll((Specification) filter, filter.getPageRequest());
        filter.setPage(0);
        filter.setSize(999999999);
        Page<Renovation> fullContent = repository.findAll((Specification) filter, filter.getPageRequest());
        List<Renovation> renovations = fullContent.getContent();

        FilterRenovationDto filterRenovationDto = new FilterRenovationDto(null, null);
        filterRenovationDto.setSummary(new SummaryDto(null, null, null, null));
        if (page == null) return filterRenovationDto;
        filterRenovationDto.getSummary().setTotal((int) fullContent.getTotalElements());
        filterRenovationDto.getSummary().setConfirmed((int) renovations.stream().filter(s -> s.getStatus().getId() == 71085991).count());
        filterRenovationDto.getSummary().setToAgreement((int) renovations.stream().filter(s -> s.getStatus().getId() == 71086011).count());
        filterRenovationDto.getSummary().setToCheck((int) renovations.stream().filter(s -> s.getStatus().getId() == 71086031).count());
        filterRenovationDto.setCollection(page.getContent());
        return filterRenovationDto;
    }

    @Transactional
    @Override
    public Renovation findRenovation(Integer renovationId) {
        return repository.findOne(renovationId);
    }

    @Override
    public String getHistory(Number id, Limit limit) {
        try {
            return objectMapper.writeValueAsString(auditHistoryService.getHistory(
                    Renovation.class, id, limit
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Building updateBuilding(Building building) {
        Building buildingEdit = (Building) updateRenovation(repository.findOne(building.getId()), building);
        buildingEdit = replaceEntity(buildingEdit, building);

        final UserEntity user = userService.getCurrentUser();
        if (!user.isXXXXXXXXX() && !user.isAdministrator())
            building.setStatus(DictionaryElementShort.builder().name("В работе").id(71086031).build());
        building.setCompletion(calculateCompletness(building));

        return repository.save(buildingEdit);
    }

    @Override
    public Object updateRenovation(Object renovation) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        BuildingSite buildingSite = null;
        Building building = null;
        HashMap renovationHashedMap = (HashMap) renovation;
        if (renovationHashedMap.get("type").equals("Building_Site")) {
            buildingSite = mapper.convertValue(renovation, BuildingSite.class);
            return updateBuildingSite(buildingSite);
        } else {
            building = mapper.convertValue(renovation, Building.class);
            return updateBuilding(building);
        }
    }

    @Override
    public BuildingSite updateBuildingSite(BuildingSite buildingSite) {
        BuildingSite buildingEdit = (BuildingSite) updateRenovation(repository.findOne(buildingSite.getId()), buildingSite);
        buildingEdit.setResettlement(buildingSite.getResettlement());
        buildingEdit.setCauseOfConstructionCancellation(buildingSite.getCauseOfConstructionCancellation());
        buildingSite.setCompletion(calculateCompletness(buildingSite));
        return repository.save(buildingEdit);
    }

    private Renovation updateRenovation(Renovation buildingEdit, Renovation renovation) {
        buildingEdit.setAddress(renovation.getAddress());
        buildingEdit.setArea(renovation.getArea());
        buildingEdit.setAreaName(renovation.getAreaName());
        buildingEdit.setComment(renovation.getComment());
        buildingEdit.setDistrict(renovation.getDistrict());
        buildingEdit.setDistrictNameShort(renovation.getDistrictNameShort());
        buildingEdit.setDocuments(renovation.getDocuments());
        buildingEdit.setLastChangeDate(renovation.getLastChangeDate());
        buildingEdit.setOkrug(renovation.getOkrug());
        buildingEdit.setStatus(renovation.getStatus());
        buildingEdit.setUniqueId(renovation.getUniqueId());
        buildingEdit.setRenovationInfrastructures(renovation.getRenovationInfrastructures());

        return buildingEdit;
    }


    @Override
    public Building postBuilding(Building building) {
        final UserEntity user = userService.getCurrentUser();
        if (!user.isXXXXXXXXX() && !user.isAdministrator()) {
            building.setStatus(DictionaryElementShort.builder().name("В работе").id(71086031).build());
        } else {
            building.setStatus(DictionaryElementShort.builder().name("Проверено").id(71085991).build());
        }

        building.setCompletion(calculateCompletness(building));
        building.setUniqueId(generateUniqueId(building.getOkrug().getId()));

        return repository.saveAndFlush(building);
    }

    @Override
    public BuildingSite postBuildingSite(BuildingSite buildingSite) {
        final UserEntity user = userService.getCurrentUser();
        if (!user.isXXXXXXXXX() && !user.isAdministrator()) {
            buildingSite.setStatus(DictionaryElementShort.builder().name("В работе").id(71086031).build());
        } else {
            buildingSite.setStatus(DictionaryElementShort.builder().name("Проверено").id(71085991).build());
        }

        buildingSite.setCompletion(calculateCompletness(buildingSite));
        buildingSite.setUniqueId(generateUniqueId(buildingSite.getOkrug().getId()));

        return repository.saveAndFlush(buildingSite);
    }

    @Override
    public Renovation findOne(int id) {
        return repository.findOne(id);
    }


    private Float calculateCompletness(Building building) {
        Float result = 0f;

        result += fieldNotNull(building.getPostCode());
        result += fieldNotNull(building.getCadastralNumber());
        result += fieldNotNull(building.getManagementCompany());
        result += fieldNotNull(building.getState());
        result += fieldNotNull(building.getOverhaulFund());
        result += fieldNotNull(building.getTypicalSeries());
        result += fieldNotNull(building.getConstructionYear());
        result += fieldNotNull(building.getPurpose());
        result += fieldNotNull(building.getHomeType());
        result += fieldNotNull(building.getCategory());
        result += fieldNotNull(building.getVotePositive());
        result += fieldNotNull(building.getVoteNegative());
        result += fieldNotNull(building.getArea());
        result += fieldNotNull(building.getStatus());
        result += fieldNotNull(building.getAddress());
        result += fieldNotNull(building.getDistrict());
        result += fieldNotNull(building.getOkrug());
        return (float) round(result / 17);
    }

    private Float calculateCompletness(BuildingSite building) {
        Float result = 0f;

        result += fieldNotNull(building.getResettlement());
        result += fieldNotNull(building.getCauseOfConstructionCancellation());
        result += fieldNotNull(building.getArea());
        result += fieldNotNull(building.getStatus());
        result += fieldNotNull(building.getAddress());
        result += fieldNotNull(building.getDistrict());
        result += fieldNotNull(building.getOkrug());
        return (float) round(result / 7);
    }

    private static double round(double a) {
        return new BigDecimal(a).setScale(3, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private <T> Integer fieldNotNull(T string) {

        return string == null ? 0 : 1;
    }

    private String generateUniqueId(Integer id) {
        Map<Integer, String> shortAreaNames = AnalyseConstants.buildShortNameMap();
        String area = shortAreaNames.get(id);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 2000;

        String character = "Р";

        String number = repository.getNextSeriesId().toString();
        String result = area + "-" + character + "-" + number + "-" + year;
        return result;
    }

    private Building replaceEntity(Building buildingEdit, Building building) {
        buildingEdit.setPostCode(building.getPostCode());
        buildingEdit.setCadastralNumber(building.getCadastralNumber());
        buildingEdit.setManagementCompany(building.getManagementCompany());
        buildingEdit.setState(building.getState());
        buildingEdit.setOverhaulFund(building.getOverhaulFund());
        buildingEdit.setTypicalSeries(building.getTypicalSeries());
        buildingEdit.setConstructionYear(building.getConstructionYear());
        buildingEdit.setPurpose(building.getPurpose());
        buildingEdit.setHomeType(building.getHomeType());
        buildingEdit.setCategory(building.getCategory());
        buildingEdit.setVotePositive(building.getVotePositive());
        buildingEdit.setVoteNegative(building.getVoteNegative());
        buildingEdit.setBuildingMaterials(new BuildingMaterials(
                (building.getBuildingMaterials().getCeiling()),
                (building.getBuildingMaterials().getCarcass()),
                (building.getBuildingMaterials().getWalls()),
                (building.getBuildingMaterials().getFoundation())));

        buildingEdit.setBuildingAreas(new BuildingAreas(
                (building.getBuildingAreas().getTotalArea()),
                (building.getBuildingAreas().getLivingArea()),
                (building.getBuildingAreas().getNotALivingArea())));

        DictionaryElementShort garbageChute = building.getBuildingParameters().getGarbageChute() == null ? null : new DictionaryElementShort(
                (building.getBuildingParameters().getGarbageChute().getId()),
                (building.getBuildingParameters().getGarbageChute().getName()));
        DictionaryElementShort gasRemoval = building.getBuildingParameters().getGasRemoval() == null ? null : new DictionaryElementShort(
                (building.getBuildingParameters().getGasRemoval().getId()),
                (building.getBuildingParameters().getGasRemoval().getName()));
        DictionaryElementShort powerSupply = building.getBuildingParameters().getPowerSupply() == null ? null : new DictionaryElementShort(
                (building.getBuildingParameters().getPowerSupply().getId()),
                (building.getBuildingParameters().getPowerSupply().getName()));

        BuildingParameters buildingParameters = new BuildingParameters(
                (building.getBuildingParameters().getBasementFloors()),
                (building.getBuildingParameters().getHeightFloor()),
                (building.getBuildingParameters().getEntrances()),
                (building.getBuildingParameters().getApartments()),
                (building.getBuildingParameters().getNotALivingAreas()),
                garbageChute,
                gasRemoval,
                powerSupply);
        buildingEdit.setBuildingParameters(buildingParameters);
        return buildingEdit;
    }
}
