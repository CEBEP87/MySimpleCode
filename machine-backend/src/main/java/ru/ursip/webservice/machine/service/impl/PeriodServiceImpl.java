package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.monitoring.OutputCalculate;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.repository.PeriodRepository;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.CalculationsService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.PeriodService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.impl.CalculationsServiceImpl.calculateDeviation;
import static ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.impl.CalculationsServiceImpl.manualInputValidation;

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
    @Value("${aserver.address.monitoring}")
    private String monitoringServer;
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
     * Data-library
     */
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Calculation service
     */
    @Autowired
    private CalculationsService calculationsService;
    /**
     * Initiating a data source host
     */
    @Value("${spring.data.mongodb.host}")
    private String host;

    @Override
    public List<PeriodList> findPeriodsList() throws Exception {
        Aggregation agg = newAggregation(project("id").andExpression("fields.p_name").as("p_name").andExpression("fields.p_status").as("p_status"));
        List<PeriodList> responce = mongoTemplate.aggregate(agg, "period", PeriodList.class).getMappedResults();
        return responce;
    }

    @Override
    public List<PeriodListWithDate> findPeriodsListWithDate() throws Exception {
        List<PeriodListWithDate> responce = new ArrayList<>();


        try {
            MongoClient client = new MongoClient(host);
            MongoCollection<Document> collection = client.getDatabase("machine").getCollection("period");
            List<Document> taskInfo =
                    collection.aggregate(
                            Arrays.asList(
                                    Aggregates.project(
                                            Projections.fields(
                                                    Projections.include("fields.p_name"),
                                                    Projections.include("fields.date_from"),
                                                    Projections.include("fields.date_till"),
                                                    Projections.include("fields.p_status")
                                            )
                                    )
                            )
                    ).into(new ArrayList<>());

            for (Document d : taskInfo) {
                DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.ENGLISH);
                Document field = (Document) d.get("fields");
                Date date1 = format.parse(field.get("date_from").toString());
                Date date2 = format.parse(field.get("date_till").toString());
                PeriodListWithDate res = new PeriodListWithDate(d.get("_id").toString(), field.get("p_name").toString(), date1, date2, field.get("p_status").toString());
                responce.add(res);
            }
        } catch (Exception e) {
            logger.info("error Aggregation");
        }

        return responce;
    }

    @Override
    public Period findOne(String id) throws Exception {
        Period period = periodRepository.findOne(id);
        return period;
    }

    @Override
    public Period save(Period period) throws Exception {
        return periodRepository.save(period);
    }

    @Override
    public Boolean validation(PeriodFields newPeriodFields) throws Exception {
        if ((newPeriodFields.getPName().equals("") || (newPeriodFields.getPName() == null))) return false;
        if ((newPeriodFields.getDateTill().equals("") || (newPeriodFields.getDateTill() == null))) return false;
        if ((newPeriodFields.getDateFrom().equals("") || (newPeriodFields.getDateFrom() == null))) return false;
        if ((newPeriodFields.getPStatus().equals("") || (newPeriodFields.getPStatus() == null))) return false;
        if ((newPeriodFields.getAmountOfDays().equals("") || (newPeriodFields.getAmountOfDays() == null))) return false;
        if ((newPeriodFields.getAmountWeekend().equals("") || (newPeriodFields.getAmountWeekend() == null)))
            return false;
        if ((newPeriodFields.getNumberOfCelebratedDays().equals("") || (newPeriodFields.getNumberOfCelebratedDays() == null)))
            return false;
        if ((newPeriodFields.getDurationOfWorkingChange().equals("") || (newPeriodFields.getDurationOfWorkingChange() == null)))
            return false;
        if ((newPeriodFields.getCoefCorrectionService().equals("") || (newPeriodFields.getCoefCorrectionService() == null)))
            return false;
        if ((newPeriodFields.getEstimatedPricePetrol().equals("") || (newPeriodFields.getEstimatedPricePetrol() == null)))
            return false;
        if ((newPeriodFields.getEstimatedPriceFuel().equals("") || (newPeriodFields.getEstimatedPriceFuel() == null)))
            return false;
        if ((newPeriodFields.getEstimatedPriceElectricity().equals("") || (newPeriodFields.getEstimatedPriceElectricity() == null)))
            return false;
        if ((newPeriodFields.getEstimatedPriceEngineOils().equals("") || (newPeriodFields.getEstimatedPriceEngineOils() == null)))
            return false;
        if ((newPeriodFields.getEstimatedPriceLubricants().equals("") || (newPeriodFields.getEstimatedPriceLubricants() == null)))
            return false;
        if ((newPeriodFields.getEstimatedPriceTransmissionOils().equals("") || (newPeriodFields.getEstimatedPriceTransmissionOils() == null)))
            return false;
        if ((newPeriodFields.getEstimatedPriceOfHydraulicLiquid().equals("") || (newPeriodFields.getEstimatedPriceOfHydraulicLiquid() == null)))
            return false;
        if ((newPeriodFields.getDensityFuel().equals("") || (newPeriodFields.getDensityFuel() == null))) return false;
        if ((newPeriodFields.getDensityPetrol().equals("") || (newPeriodFields.getDensityPetrol() == null)))
            return false;
        if ((newPeriodFields.getIndexHydraulicFluidDensity().equals("") || (newPeriodFields.getIndexHydraulicFluidDensity() == null)))
            return false;
        if ((newPeriodFields.getCoefOfHydraulicLiquid().equals("") || (newPeriodFields.getCoefOfHydraulicLiquid() == null)))
            return false;
        if ((newPeriodFields.getIndexHydraulicFluidReplacement().equals("") || (newPeriodFields.getIndexHydraulicFluidReplacement() == null)))
            return false;
        return true;
    }

    @Override
    public Period sortByTSN(Period period) throws Exception {
        List<Resource> sortedList = period.getResource()
                .stream()
                .sorted((e1, e2) -> {
                    String[] s1 = e1.getFields().getCodeTsn().split("\\.");
                    String[] s3 = e2.getFields().getCodeTsn().split("\\.");
                    if ((e1.getFields().getCodeTsn().contains(".")) &
                            (e2.getFields().getCodeTsn().contains("."))) {
                        String[] s2 = s1[1].split("\\-");
                        String[] s4 = s3[1].split("\\-");
                        int result;
                        result = s1[0].compareTo(s3[0]);
                        if (result != 0) return result;
                        result = s2[0].compareTo(s4[0]);
                        if (result != 0) return result;
                        result = s2[1].compareTo(s4[1]);
                        if (result != 0) return result;
                        try {
                            return Integer.valueOf(s2[2]).compareTo(Integer.valueOf(s4[2]));
                        } catch (Exception e) {
                        }
                    } else {
                        try {
                            return Integer.valueOf(s1[0]).compareTo(Integer.valueOf(s3[0]));
                        } catch (Exception e) {
                        }
                    }
                    return s1[0].compareTo(s3[0]);
                })
                .collect(Collectors.toList());
        period.setResource(sortedList);
        return period;
    }

    @Override
    public Period lastPeriod() throws Exception {
        List<PeriodListWithDate> periods = findPeriodsListWithDate();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -15);
        Date dep1 = cal.getTime();
        Date maxDate = dep1;
        String idMaxPeriod = new String();
        for (PeriodListWithDate element : periods) {
            if (element.getDateTill().getTime() > maxDate.getTime()) maxDate = element.getDateTill();
        }
        for (PeriodListWithDate element : periods) {
            if (element.getDateTill().equals(maxDate)) idMaxPeriod = element.getId();
        }
        if (maxDate == null) idMaxPeriod = periods.get(0).getId();
        return findOne(idMaxPeriod);
    }

    @Override
    public Period previousPeriod(String periodId) throws Exception {
        List<PeriodListWithDate> periods = findPeriodsListWithDate();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -15);
        Date dep1 = cal.getTime();
        Date maxDate = findOne(periodId).getFields().getDateTill();
        String idPreviousPeriod = new String();
        Date previousDate = dep1;
        for (PeriodListWithDate element : periods) {
            if (element.getDateTill().getTime() > previousDate.getTime())
                if (previousDate.getTime() != maxDate.getTime()) previousDate = element.getDateTill();
        }
        for (PeriodListWithDate element : periods) {
            if (element.getDateTill().equals(previousDate)) idPreviousPeriod = element.getId();
        }
        return findOne(idPreviousPeriod);
    }

    @Override
    public ResponseEntity postPeriod(PeriodFields newPeriodFields) {
        try {
            if (!validation(newPeriodFields))
                return new ResponseEntity("Please input all fields", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
            List<PeriodListWithDate> periods = findPeriodsListWithDate();
            Date maxDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(maxDate);
            cal.add(Calendar.DATE, -360); //minus number would decrement the days
            maxDate = cal.getTime();
            String idMaxPeriod = new String();
            for (PeriodListWithDate element : periods) {
                if (element.getPName().equals(newPeriodFields.getPName()))
                    return new ResponseEntity(new ServiceError("Выбранное имя существует", new Exception(), null), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            for (PeriodListWithDate element : periods) {
                if (element.getDateTill().getTime() > maxDate.getTime()) maxDate = element.getDateTill();
            }
            for (PeriodListWithDate element : periods) {
                if (element.getDateTill().equals(maxDate)) idMaxPeriod = element.getId();
            }
            Period oldPeriod = findOne(idMaxPeriod);
            if (oldPeriod.getFields().getDateTill().getTime() > newPeriodFields.getDateFrom().getTime())
                return new ResponseEntity(new ServiceError("Дата начала периода меньше даты окончания предыдущего периода", new Exception(), null), HttpStatus.INTERNAL_SERVER_ERROR);
            oldPeriod.setFields(newPeriodFields);
            List<Resource> clearList = oldPeriod.getResource().stream().filter(
                    index -> (ObjectUtils.notEqual(index.getFields().getAgreement(), null) && (ObjectUtils.notEqual(index.getFields().getAction(), null))) &&
                            (!index.getFields().getAgreement().equals("Отклонен") || ((index.getFields().getAgreement().equals("утвержден")) & (!index.getFields().getAction().equals("Удаление"))))
            ).collect(Collectors.toList());
            oldPeriod.setResource(updateProperties(clearList, oldPeriod));

            oldPeriod = getEstimatedPriceByMonitoring(oldPeriod);
            oldPeriod.setId(null);
            for (Resource res : oldPeriod.getResource()) {
                if (res.getFields().getCoefRecountCurrentH() != null)
                    res.getFields().setCoefRecountPrevious(res.getFields().getCoefRecountCurrentH());
                else
                    res.getFields().setCoefRecountPrevious(res.getFields().getCoefRecountCurrent());

            }
            return new ResponseEntity(sortByTSN(save(oldPeriod)), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Inner server error", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * getEstimatedPrice
     *
     * @param id - id Period monitoring
     * @return - clear List
     */
    @Override
    public Map<String, String> getEstimatedPrice(String id) {
        String idperiod = id;
        OutputCalculate[] dataFuel = restTemplate.getForObject("http://" + monitoringServer + "/ws-mon/v1/resXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXerioda/zapros?period=" + idperiod + "&forCalculateModule=1" + "&tsn=1.1-1-1268&forCalculateModule=1", OutputCalculate[].class);
        OutputCalculate[] dataElectricity = restTemplate.getForObject("http://" + monitoringServer + "/ws-mon/v1/resXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXerioda/zapros?period=" + idperiod + "&forCalculateModule=1" + "&tsn=1.1-1-1920&forCalculateModule=1", OutputCalculate[].class);
        OutputCalculate[] dataPetrol = restTemplate.getForObject("http://" + monitoringServer + "/ws-mon/v1/resXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXerioda/zapros?period=" + idperiod + "&forCalculateModule=1" + "&tsn=1.1-1-41&forCalculateModule=1", OutputCalculate[].class);
        OutputCalculate[] dataLiquid = restTemplate.getForObject("http://" + monitoringServer + "/ws-mon/v1/resXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXerioda/zapros?period=" + idperiod + "&forCalculateModule=1" + "&tsn=1.1-1-1762&forCalculateModule=1", OutputCalculate[].class);
        OutputCalculate[] dataTransmissionOils = restTemplate.getForObject("http://" + monitoringServer + "/ws-mon/v1/resXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXerioda/zapros?period=" + idperiod + "&forCalculateModule=1" + "&tsn=1.1-1-3508&forCalculateModule=1", OutputCalculate[].class);
        OutputCalculate[] dataEngineOils = restTemplate.getForObject("http://" + monitoringServer + "/ws-mon/v1/resXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXerioda/zapros?period=" + idperiod + "&forCalculateModule=1" + "&tsn=1.26-2-17&forCalculateModule=1", OutputCalculate[].class);
        OutputCalculate[] dataLubricants = restTemplate.getForObject("http://" + monitoringServer + "/ws-mon/v1/resXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXerioda/zapros?period=" + idperiod + "&forCalculateModule=1" + "&tsn=1.1-1-2558&forCalculateModule=1", OutputCalculate[].class);

        List<OutputCalculate> estimatePricesFuel = new ArrayList<OutputCalculate>(Arrays.asList(dataFuel));
        List<OutputCalculate> estimatePricesElectricity = new ArrayList<OutputCalculate>(Arrays.asList(dataElectricity));
        List<OutputCalculate> estimatePricesPetrol = new ArrayList<OutputCalculate>(Arrays.asList(dataPetrol));
        List<OutputCalculate> estimatePricesLiquid = new ArrayList<OutputCalculate>(Arrays.asList(dataLiquid));
        List<OutputCalculate> estimatePricesTransmissionOils = new ArrayList<OutputCalculate>(Arrays.asList(dataTransmissionOils));
        List<OutputCalculate> estimatePricesEngineOils = new ArrayList<OutputCalculate>(Arrays.asList(dataEngineOils));
        List<OutputCalculate> estimatePricesLubricants = new ArrayList<OutputCalculate>(Arrays.asList(dataLubricants));

        Map<String, String> result = new HashMap();
        result.put("estimatedPriceFuel", calculationsService.rounding(estimatePricesFuel.get(0).getResourcePeriodComression().getFkResource().getBasicPrice() / 100d * estimatePricesFuel.get(0).getRate() / 1000d).toString());
        result.put("estimatedPriceElectricity", calculationsService.rounding(estimatePricesElectricity.get(0).getResourcePeriodComression().getFkResource().getBasicPrice() / 100d * estimatePricesElectricity.get(0).getRate()).toString());
        result.put("estimatedPricePetrol", calculationsService.rounding(estimatePricesPetrol.get(0).getResourcePeriodComression().getFkResource().getBasicPrice() / 100d * estimatePricesPetrol.get(0).getRate() / 1000d).toString());
        result.put("estimatedPriceOfHydraulicLiquid", calculationsService.rounding(estimatePricesLiquid.get(0).getResourcePeriodComression().getFkResource().getBasicPrice() / 100d * estimatePricesLiquid.get(0).getRate()).toString());
        result.put("estimatedPriceTransmissionOils", calculationsService.rounding(estimatePricesTransmissionOils.get(0).getResourcePeriodComression().getFkResource().getBasicPrice() / 100d * estimatePricesTransmissionOils.get(0).getRate()).toString());
        result.put("estimatedPriceEngineOils", calculationsService.rounding(estimatePricesEngineOils.get(0).getResourcePeriodComression().getFkResource().getBasicPrice() / 100d * estimatePricesEngineOils.get(0).getRate()).toString());
        result.put("estimatedPriceLubricants", calculationsService.rounding(estimatePricesLubricants.get(0).getResourcePeriodComression().getFkResource().getBasicPrice() / 100d * estimatePricesLubricants.get(0).getRate()).toString());
        return result;
    }

    /**
     * getEstimatedPriceByMonitoring
     *
     * @param period - period
     * @return - clear List
     */
    public Period getEstimatedPriceByMonitoring(Period period) {
        String idperiod = period.getFields().getIdPeriodMaterial();
        OutputCalculate[] dataFuel = restTemplate.getForObject("http://" + monitoringServer + "/ws-mon/v1/resXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXerioda/zapros?period=" + idperiod + "&forCalculateModule=1" + "&tsn=1.1-1-1268&forCalculateModule=1", OutputCalculate[].class);
        OutputCalculate[] dataElectricity = restTemplate.getForObject("http://" + monitoringServer + "/ws-mon/v1/resXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXerioda/zapros?period=" + idperiod + "&forCalculateModule=1" + "&tsn=1.1-1-1920&forCalculateModule=1", OutputCalculate[].class);
        OutputCalculate[] dataPetrol = restTemplate.getForObject("http://" + monitoringServer + "/ws-mon/v1/resXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXerioda/zapros?period=" + idperiod + "&forCalculateModule=1" + "&tsn=1.1-1-41&forCalculateModule=1", OutputCalculate[].class);
        OutputCalculate[] dataLiquid = restTemplate.getForObject("http://" + monitoringServer + "/ws-mon/v1/resXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXerioda/zapros?period=" + idperiod + "&forCalculateModule=1" + "&tsn=1.1-1-1762&forCalculateModule=1", OutputCalculate[].class);
        OutputCalculate[] dataTransmissionOils = restTemplate.getForObject("http://" + monitoringServer + "/ws-mon/v1/resXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXerioda/zapros?period=" + idperiod + "&forCalculateModule=1" + "&tsn=1.1-1-3508&forCalculateModule=1", OutputCalculate[].class);
        OutputCalculate[] dataEngineOils = restTemplate.getForObject("http://" + monitoringServer + "/ws-mon/v1/resXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXerioda/zapros?period=" + idperiod + "&forCalculateModule=1" + "&tsn=1.26-2-17&forCalculateModule=1", OutputCalculate[].class);
        OutputCalculate[] dataLubricants = restTemplate.getForObject("http://" + monitoringServer + "/ws-mon/v1/resXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXerioda/zapros?period=" + idperiod + "&forCalculateModule=1" + "&tsn=1.1-1-2558&forCalculateModule=1", OutputCalculate[].class);

        List<OutputCalculate> estimatePricesFuel = new ArrayList<OutputCalculate>(Arrays.asList(dataFuel));
        List<OutputCalculate> estimatePricesElectricity = new ArrayList<OutputCalculate>(Arrays.asList(dataElectricity));
        List<OutputCalculate> estimatePricesPetrol = new ArrayList<OutputCalculate>(Arrays.asList(dataPetrol));
        List<OutputCalculate> estimatePricesLiquid = new ArrayList<OutputCalculate>(Arrays.asList(dataLiquid));
        List<OutputCalculate> estimatePricesTransmissionOils = new ArrayList<OutputCalculate>(Arrays.asList(dataTransmissionOils));
        List<OutputCalculate> estimatePricesEngineOils = new ArrayList<OutputCalculate>(Arrays.asList(dataEngineOils));
        List<OutputCalculate> estimatePricesLubricants = new ArrayList<OutputCalculate>(Arrays.asList(dataLubricants));

        period.getFields().setEstimatedPriceFuel(calculationsService.rounding(estimatePricesFuel.get(0).getResourcePeriodComression().getFkResource().getBasicPrice() / 100d * estimatePricesFuel.get(0).getRate() / 1000d));
        period.getFields().setEstimatedPriceElectricity(calculationsService.rounding(estimatePricesElectricity.get(0).getResourcePeriodComression().getFkResource().getBasicPrice() / 100d * estimatePricesElectricity.get(0).getRate()));
        period.getFields().setEstimatedPricePetrol(calculationsService.rounding(estimatePricesPetrol.get(0).getResourcePeriodComression().getFkResource().getBasicPrice() / 100d * estimatePricesPetrol.get(0).getRate() / 1000d));
        period.getFields().setEstimatedPriceLubricants(calculationsService.rounding(estimatePricesLiquid.get(0).getResourcePeriodComression().getFkResource().getBasicPrice() / 100d * estimatePricesLiquid.get(0).getRate()));
        period.getFields().setEstimatedPriceTransmissionOils(calculationsService.rounding(estimatePricesTransmissionOils.get(0).getResourcePeriodComression().getFkResource().getBasicPrice() / 100d * estimatePricesTransmissionOils.get(0).getRate()));
        period.getFields().setEstimatedPriceEngineOils(calculationsService.rounding(estimatePricesEngineOils.get(0).getResourcePeriodComression().getFkResource().getBasicPrice() / 100d * estimatePricesEngineOils.get(0).getRate()));
        period.getFields().setEstimatedPriceLubricants(calculationsService.rounding(estimatePricesLubricants.get(0).getResourcePeriodComression().getFkResource().getBasicPrice() / 100d * estimatePricesLubricants.get(0).getRate()));

        return period;
    }

    /**
     * update Properties
     *
     * @param clearList input reource list
     * @param oldPeriod other values in period
     * @return - clear List
     */
    public List<Resource> updateProperties(List<Resource> clearList, Period oldPeriod) {
        clearList.stream().forEach((s) -> {
            s.getFields().setPreviousPrice(manualInputValidation(s.getFields().getEstimatedCurrentPrice(), s.getFields().getEstimatedCurrentPriceH()));
            Double actualPrice = manualInputValidation(s.getFields().getEstimatedCurrentPrice(), s.getFields().getEstimatedCurrentPriceH());
            s.getFields().setDeviation(calculateDeviation(actualPrice, s.getFields().getPreviousPrice()));
            OutputCalculation outputCalculation = calculationsService.calculateCostItems(s, oldPeriod.getFields());
            s.setFields(outputCalculation.getInputResource().getFields());
            s.setCostItems(outputCalculation.getInputResource().getCostItems());
            s.setActiveResourceIdentificator(outputCalculation.getInputResource().getActiveResourceIdentificator());
            s.setSectionIdentificator(outputCalculation.getInputResource().getSectionIdentificator());
            if (ObjectUtils.notEqual(outputCalculation.getInputResource().getId(), null))
                s.setId(outputCalculation.getInputResource().getId());
            else s.setId(UUID.randomUUID().toString());

        });
        clearList.stream().forEach(s -> {
            s.getFields().setIsChief(false);
            s.getFields().setIsLeader(false);
            s.getFields().setIsSotrudnik(false);
            s.getFields().setRemarkChief("");
            s.getFields().setRemarkLeader("");
            if (!s.getFields().getRStatus().equals("В работе")) s.setActiveResourceIdentificator(s.getId());
            s.setId(UUID.randomUUID().toString());
        });
        clearList.stream().forEach(s -> {
            s.getFields().setRemarkChief("");
            s.getFields().setRemarkLeader("");
            s.getFields().setIsSotrudnik(false);
            s.getFields().setIsLeader(false);
            s.getFields().setIsChief(false);
            if (s.getFields().getAgreement().equals("утвержден")) {
                if (s.getFields().getAction().equals("Добавление")) {
                    s.getFields().setRStatus("Действующий");
                    s.getFields().setAction("");
                    s.getFields().setAgreement("");
                }
                if (s.getFields().getAction().equals("Изменение")) {
                    s.getFields().setRStatus("Действующий");
                    s.getFields().setAction("");
                    s.getFields().setAgreement("");
                    oldPeriod.getResource().removeIf(f -> f.getId().equals(s.getActiveResourceIdentificator()));
                }
            }
        });
        return clearList;
    }

    @Override
    public ResponseEntity putPeriod(PutPeriod data) {
        try {
            List<PeriodListWithDate> periods = findPeriodsListWithDate();
            String idFinded = new String();
            for (PeriodListWithDate element : periods) {
                if (element.getId().equals(data.getPeriodId())) idFinded = element.getId();
            }
            Period oldPeriod = findOne(idFinded);
            Date maxDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(maxDate);
            cal.add(Calendar.DATE, -360); //minus number would decrement the days
            maxDate = cal.getTime();
            for (PeriodListWithDate element : periods) {
                if (element.getPName().equals(data.getInputPeriodFields().getPName()))
                    if (!element.getPName().equals(oldPeriod.getFields().getPName()))
                        return new ResponseEntity(new ServiceError("Выбранное имя существует", new Exception(), null), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            for (PeriodListWithDate element : periods) {
                if (element.getDateTill().getTime() > maxDate.getTime())
                    if (!element.getPName().equals(oldPeriod.getFields().getPName()))
                        maxDate = element.getDateTill();
            }
            if (maxDate.getTime() > data.getInputPeriodFields().getDateFrom().getTime())
                return new ResponseEntity(new ServiceError("Дата начала периода меньше даты окончания предыдущего периода", new Exception(), null), HttpStatus.INTERNAL_SERVER_ERROR);
            oldPeriod.setFields(data.getInputPeriodFields());
            oldPeriod.getResource().stream().forEach((s) -> {
                Double actualPrice = manualInputValidation(s.getFields().getEstimatedCurrentPrice(), s.getFields().getEstimatedCurrentPriceH());
                s.getFields().setDeviation(calculateDeviation(actualPrice, s.getFields().getPreviousPrice()));
                OutputCalculation outputCalculation = calculationsService.calculateCostItems(s, oldPeriod.getFields());
                s.setFields(outputCalculation.getInputResource().getFields());
                s.setCostItems(outputCalculation.getInputResource().getCostItems());
                s.setActiveResourceIdentificator(outputCalculation.getInputResource().getActiveResourceIdentificator());
                s.setSectionIdentificator(outputCalculation.getInputResource().getSectionIdentificator());
                s.setId(outputCalculation.getInputResource().getId());
            });
            return new ResponseEntity(sortByTSN(save(oldPeriod)), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Inner server error", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
