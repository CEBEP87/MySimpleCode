package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.machines.Resource;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.machines.Section;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.monitoring.Measure;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.monitoring.OutputCalculate;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.monitoring.ResourceSection;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.monitoring.TransportCostPeriod;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.repository.PeriodRepository;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.repository.TransportCostRepository;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.repository.WinterRiseListCurrentRepository;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Period Service
 *
 * @author Samsonov_KY
 * @since 21.09.2017
 */
@Service
public class PeriodServiceImpl implements PeriodService {
    /**
     * Initiating restTemplate
     */
    private RestTemplate restTemplate = new RestTemplate();

    /**
     * Initiating port
     */
    @Value("${XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX}")
    private String monitoringServer;
    /**
     * Initiating port
     */
    @Value("${XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX}")
    private String machineServer;

    /**
     * Logger object
     */
    private Logger logger = LoggerFactory.getLogger(PeriodServiceImpl.class);

    /**
     * Period Repository
     */
    @Autowired
    private PeriodRepository periodRepository;

    /**
     * Period Repository
     */
    @Autowired
    private UnitOfMeasureMaterialService unitOfMeasureMaterialService;

    /**
     * Data-library
     */
    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * Construction Service
     */
    @Autowired
    private ConstructionService constructionService;
    /**
     * Instalation Service
     */
    @Autowired
    private InstallationService installationService;

    /**
     * commissioning Service
     */
    @Autowired
    private CommissioningService commissioningService;

    /**
     * repair Service
     */
    @Autowired
    private RepairService repairService;

    /**
     * restoration Service
     */
    @Autowired
    private RestorationService restorationService;

    /**
     * overhead Service
     */
    @Autowired
    private OverheadService overheadService;

    /**
     * winter_rise Service
     */
    @Autowired
    private WinterRiseService winterRiseService;

    /**
     * temporary_building Service
     */
    @Autowired
    private TemporaryBuildingService temporaryBuildingService;

    /**
     * service Service
     */
    @Autowired
    private ServiceService serviceService;

    /**
     * material Service
     */
    @Autowired
    private MaterialService materialService;

    /**
     * equipment Service
     */
    @Autowired
    private EquipmentService equipmentService;

    /**
     * machine Service
     */
    @Autowired
    private MachineService machineService;
    /**
     * large_process Service
     */
    @Autowired
    private LargeProcessService largeProcessService;

    /**
     * celebrate_process Service
     */
    @Autowired
    private CelebrateProcessService celebrateProcessService;

    /**
     * calculation Service
     */
    @Autowired
    private CalculationsService calculationsService;
    /**
     * OverheadProfitCurrentRepository
     */

    @Autowired
    private OverheadProfitService overheadProfitService;
    /**
     * TransportCostRepository
     */
    @Autowired
    private TransportCostRepository transportCostRepository;

    /**
     * transportService
     */
    @Autowired
    private TransportService transportService;

    /**
     * WinterRiseListCurrentRepository
     */
    @Autowired
    private WinterRiseListCurrentRepository winterRiseListCurrentRepository;


    /**
     * parametrsOfTransportService
     */
    @Autowired
    private ParametrsOfTransportService parametrsOfTransportService;

    @Override
    public ResponseEntity getPeriodService(String id) {
        try {
            if (id == null)
                return new ResponseEntity(findOne(getIdLastPeriod()), HttpStatus.OK);
            if (id.equals("date")) {
                List<Period> list = findAll();
                list.forEach(s -> s.setGroupList(null));
                return new ResponseEntity(list, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity("Error inner Server", HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity(findOne(id), HttpStatus.OK);
    }

    @Override
    public List<Period> findAll() {
        List<Period> resuilt = periodRepository.findAll();
        resuilt.forEach(a -> naturalSorter(a));
        return resuilt;
    }

    /**
     * naturalSorter
     *
     * @param period - period
     * @return sorted period
     */
    private Period naturalSorter(Period period) {
        List<Group> sortedList = period.getGroupList().stream().sorted((o1, o2) -> {
            String[] leftO1 = o1.getTitle().split("\\.");
            String[] leftO2 = o2.getTitle().split("\\.");
            return extractInt(leftO1[0]) - extractInt(leftO2[0]);
        }).collect(Collectors.toList());
        period.setGroupList(sortedList);
        return period;
    }

    /**
     * extract Int values
     *
     * @param s - String
     * @return int value
     */
    int extractInt(String s) {
        try {
            String num = s.replaceAll("\\D", "");
            // return 0 if no digits found
            return num.isEmpty() ? 0 : Integer.parseInt(num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public Period findOne(String id) {
        return naturalSorter(periodRepository.findOne(id));
    }


    @Override
    public void deletePeriod(String id) {///удаление периода и всех его связей
        periodRepository.delete(id);
        celebrateProcessService.deleteByIdPeriod(id);
        commissioningService.deleteByIdPeriod(id);
        constructionService.deleteByIdPeriod(id);
        equipmentService.deleteByIdPeriod(id);
        installationService.deleteByIdPeriod(id);
        largeProcessService.deleteByIdPeriod(id);
        machineService.deleteByIdPeriod(id);
        materialService.deleteByIdPeriod(id);
        overheadService.deleteByIdPeriod(id);
        repairService.deleteByIdPeriod(id);
        restorationService.deleteByIdPeriod(id);
        serviceService.deleteByIdPeriod(id);
        temporaryBuildingService.deleteByIdPeriod(id);
        winterRiseService.deleteByIdPeriod(id);
    }

    /**
     * celebrate_process Service
     *
     * @param period     new period
     * @param lastPeriod lastPeriod
     * @return ListOfActivities
     */
    public Period recalculate(Period period, Period lastPeriod) {
        XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        return newPeriod;
    }


    /**
     * createNewPeriod
     *
     * @param period - period data
     * @return - new period
     */
    private Period createNewPeriod(Period period) {
        return new Period(
                UUID.randomUUID().toString(),
                period.getTitle(),
                period.getDateFrom(),
                period.getDateTo(),
                "открытый",
                period.getIdPeriodMachine(),
                period.getIdPeriodMaterial(),
                period.getIdPeriodEquipment(),
                period.getIndexSalary(),
                period.getIndexOtherResource(),
                period.getIndexOtherMachine(),
                period.getIndexSalaryPrevious(),
                period.getIndexOtherResourcePrevious(),
                period.getIndexMachine(),
                period.getIndexMaterial(),
                period.getIndexOtherWork(),
                new ArrayList<>()
        );
    }


    @Override
    public Period putPeriod(Period period) {
        Period old = periodRepository.findOne(period.getId());
        old.setIndexSalary(old.getIndexSalaryPrevious());
        period.setGroupList(old.getGroupList());
        Period newPeriod = recalculate(period, old);         deletePeriod(old.getId());
        String periodId = newPeriod.getId();

        List<WinterRiseListCurrent> list = winterRiseListCurrentRepository.findAll();
        list.forEach(s -> {
            if (s.getPeriod() != null) if (s.getPeriod().equals(period.getId())) s.setPeriod(periodId);
        });
        winterRiseListCurrentRepository.save(list);

        List<OverheadProfitCurrent> list2 = overheadProfitService.findAllCurrent();
        list2.forEach(s -> {
            if (s.getIdPeriod() != null) if (s.getIdPeriod().equals(period.getId())) s.setIdPeriod(periodId);
        });
        overheadProfitService.putOverheadProfitCurrent(list2);
        newPeriod.setGroupList(null);
        return newPeriod;

    }

    @Override
    public ResponseEntity putPeriodWithoutCalculate(Period period) {
        if (period.getGroupList() == null)
            period.setGroupList(periodRepository.findOne(period.getId()).getGroupList());
        return new ResponseEntity(periodRepository.save(period), HttpStatus.OK);
    }


    @Override
    public Period postPeriod(Period period) {
        String idLastPeriod = getIdLastPeriod();
        Period result = recalculate(period, null);

        List<WinterRiseListCurrent> winterRiseListCurrentList = winterRiseListCurrentRepository.findAllByPeriod(idLastPeriod);
        winterRiseListCurrentList = copyToNewPeriodWinterRiseListCurrent(winterRiseListCurrentList, result.getId());
        winterRiseListCurrentRepository.save(winterRiseListCurrentList);

        List<OverheadProfitCurrent> overheadProfitCurrentList = overheadProfitService.findAllByIdPeriod(idLastPeriod);
        overheadProfitCurrentList = copyToNewPeriodOverheadProfitCurrent(overheadProfitCurrentList, result.getId());
        overheadProfitService.putOverheadProfitCurrent(overheadProfitCurrentList);
        return result;
    }

    /**
     * copy WinterRiseListCurrent ToNewPeriod
     *
     * @param list        - WinterRiseListCurrent
     * @param idNewPeriod - idNewPeriod
     * @return - new list
     */
    private List<WinterRiseListCurrent> copyToNewPeriodWinterRiseListCurrent(List<WinterRiseListCurrent> list, String idNewPeriod) {
        List<WinterRiseListCurrent> result = new ArrayList<>();
        list.forEach(s -> result.add(new WinterRiseListCurrent(null, s.getIdPressmark(), idNewPeriod, s.getWinterRise(), s.getWinterRiseExtra(), s.getWinterRiseMat(), s.getWinterRiseMatExtra())));
        return result;
    }

    /**
     * copy OverheadProfitCurrent ToNewPeriod
     *
     * @param list        -OverheadProfitCurrent
     * @param idNewPeriod - idNewPeriod
     * @return - new list
     */
    private List<OverheadProfitCurrent> copyToNewPeriodOverheadProfitCurrent(List<OverheadProfitCurrent> list, String idNewPeriod) {
        List<OverheadProfitCurrent> result = new ArrayList<>();
        list.forEach(s -> result.add(new OverheadProfitCurrent(null, s.getIdPressmark(), idNewPeriod, s.getOverhead(), s.getOverheadExtra(), s.getProfit(), s.getProfitExtra())));
        return result;
    }

    @Override
    public String getIdLastPeriod() {
        List<Period> periods = periodRepository.findAll();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -15);
        Date dep1 = cal.getTime();
        Date maxDate = dep1;
        String idMaxPeriod = new String();

        for (Period element : periods) {
            if (element.getDateTo().getTime() > maxDate.getTime()) maxDate = element.getDateTo();
        }
        for (Period element : periods) {
            if (element.getDateTo().equals(maxDate)) idMaxPeriod = element.getId();
        }
        if (maxDate == null) idMaxPeriod = periods.get(0).getId();
        return idMaxPeriod;
    }


}
