package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.CalculationsService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.CategoryDriversService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.PeriodService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by samsonov_ky on 26.09.2017.
 */
@Service
public class CalculationsServiceImpl implements CalculationsService {
    /**
     * resource
     */
    private Resource resource;
    /**
     * Fields of period
     */
    private PeriodFields periodFields;
    /**
     * Logger object
     */
    private Logger logger = LoggerFactory.getLogger(PeriodServiceImpl.class);
    /**
     * Output Errors list
     */
    private Set<OutputErrors> outputErrorsList = new HashSet<>();

    /**
     * Output Errors list
     */
    @Autowired
    private CategoryDriversService categoryDriversService;

    /**
     * Output Errors list
     */
    @Autowired
    private PeriodService periodService;


    /**
     * Nan situation to Null
     *
     * @param val base value
     */
    public void nonNan(Double val) {
        if (Double.isNaN(val)) {
            throw new NullPointerException("can't be Nan");
        }
        if (Double.isInfinite(val)) {
            throw new NullPointerException("can't be Infinity");
        }

    }


    @Override
    public Double rounding(Double val) {
        nonNan(val);
        return new BigDecimal(val).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Arounding value
     *
     * @param val   base value
     * @param scale scalse value
     * @return - result rounding
     */
    public Double rounding(Double val, Integer scale) {
        nonNan(val);
        return new BigDecimal(val).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * calculation deviation
     *
     * @param baseValue    base value
     * @param currentValue current value
     * @return - result calculate
     */
    public static Double calculateDeviation(Double baseValue, Double currentValue) {
        Double currentDelta = null;
        if (baseValue != null && baseValue != 0 && currentValue != null)
            currentDelta = new BigDecimal((((currentValue - baseValue) / baseValue) * 100)).setScale(0, RoundingMode.HALF_UP).doubleValue();
        return currentDelta;

    }

    /**
     * Calculate manual Input Double Type
     * <p>
     * * @param auto auto value
     *
     * @param hand manual value
     * @return - result calculate
     */
    public static Double manualInputValidation(Double auto, Double hand) {
        if (hand == null) if (auto != null) return auto;
        if (hand != null) return hand;
        return null;
    }

    /**
     * Calculate manual Input Long Type
     *
     * @param auto auto value
     * @param hand manual value
     * @return - result calculate
     */
    public static Long manualInputValidation(Long auto, Long hand) {
        if (hand == null) if (auto != null) return auto;
        if (hand != null) return hand;
        return null;
    }

    @Override
    public OutputCalculation calculateCostItems(Resource inputResource, PeriodFields inputPeriodFields) {
        resource = inputResource;
        periodFields = inputPeriodFields;
        calculateCoefRecountCurent();
        OutputCalculation outputCalculation = new OutputCalculation(resource, outputErrorsList);
        return outputCalculation;
    }

    @Override
    public ResponseEntity quickCalculateResource(String idPeriod, String idMachine, Double estimatedCurrentPriceH) {
        try {
            Period period = periodService.findOne(idPeriod);
            Resource res = period.getResource().stream().filter(s -> s.getId().equals(idMachine)).findFirst().get();
            HashMap<String, Double> result = new HashMap<>();
            result.put("indexSalaryH", rounding(period.getFields().getIndexSalary() * res.getFields().getEstimatedBasePriceIncludeTaxByDriver()));
            result.put("coefRecountCurrentH", estimatedCurrentPriceH / res.getFields().getEstimatedBasePrice());
            return new ResponseEntity(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Внутренняя ошибка сервера", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * calculate coefficient recount current
     */
    public void calculateCoefRecountCurent() {
        calculateEstimatedCurrentPrice();
        try {
            if (manualInputValidation(resource.getFields().getEstimatedCurrentPriceSubMachine(), resource.getFields().getEstimatedCurrentPriceSubMachineH()) < 0d) {
                resource.getFields().setCoefRecountCurrentSubMachine(null);
                outputErrorsList.add(new OutputErrors("negative value", "CoefRecountCurrentSubMachine"));
            } else
                resource.getFields().setCoefRecountCurrentSubMachine(manualInputValidation(resource.getFields().getEstimatedCurrentPriceSubMachine(), resource.getFields().getEstimatedCurrentPriceSubMachineH()) / resource.getFields().getEstimatedBasePrice());

            nonNan(resource.getFields().getCoefRecountCurrentSubMachine());
        } catch (NullPointerException e) {
            resource.getFields().setCoefRecountCurrentSubMachine(null);
            outputErrorsList.add(new OutputErrors("not enough data", "CoefRecountCurrentSubMachine"));
        } catch (ArithmeticException e) {
            resource.getFields().setCoefRecountCurrentSubMachine(null);
            outputErrorsList.add(new OutputErrors("division by zero", "CoefRecountCurrentSubMachine"));
        }

        try {
            if (manualInputValidation(resource.getFields().getEstimatedCurrentPriceSubInstrument(), resource.getFields().getEstimatedCurrentPriceSubInstrumentH()) < 0d) {
                resource.getFields().setCoefRecountCurrentSubInstrument(null);
                outputErrorsList.add(new OutputErrors("negative value", "CoefRecountCurrentSubInstrument"));
            } else
                resource.getFields().setCoefRecountCurrentSubInstrument(manualInputValidation(resource.getFields().getEstimatedCurrentPriceSubInstrument(), resource.getFields().getEstimatedCurrentPriceSubInstrumentH()) / resource.getFields().getEstimatedBasePrice());
            nonNan(resource.getFields().getCoefRecountCurrentSubInstrument());
        } catch (NullPointerException e) {
            resource.getFields().setCoefRecountCurrentSubInstrument(null);
            outputErrorsList.add(new OutputErrors("not enough data", "CoefRecountCurrentSubInstrument"));
        } catch (ArithmeticException e) {
            resource.getFields().setCoefRecountCurrentSubInstrument(null);
            outputErrorsList.add(new OutputErrors("division by zero", "CoefRecountCurrentSubInstrument"));
        }

        try {
            if (manualInputValidation(resource.getFields().getEstimatedCurrentPriceSubMechanizm(), resource.getFields().getEstimatedCurrentPriceSubMechanizmH()) < 0d) {
                resource.getFields().setCoefRecountCurrentSubMechanizm(null);
                outputErrorsList.add(new OutputErrors("negative value", "CoefRecountCurrentSubMechanizm"));
            } else
                resource.getFields().setCoefRecountCurrentSubMechanizm(manualInputValidation(resource.getFields().getEstimatedCurrentPriceSubMechanizm(), resource.getFields().getEstimatedCurrentPriceSubMechanizmH()) / resource.getFields().getEstimatedBasePrice());
            nonNan(resource.getFields().getCoefRecountCurrentSubMechanizm());
        } catch (NullPointerException e) {
            resource.getFields().setCoefRecountCurrentSubMechanizm(null);
            outputErrorsList.add(new OutputErrors("not enough data", "CoefRecountCurrentSubMechanizm"));
        } catch (ArithmeticException e) {
            resource.getFields().setCoefRecountCurrentSubMechanizm(null);
            outputErrorsList.add(new OutputErrors("division by zero", "CoefRecountCurrentSubMechanizm"));
        }

        try {
            if (manualInputValidation(resource.getFields().getEstimatedCurrentPrice(), resource.getFields().getEstimatedCurrentPriceH()) < 0d) {
                resource.getFields().setCoefRecountCurrent(null);
                outputErrorsList.add(new OutputErrors("negative value", "CoefRecountCurrent"));
            } else
                resource.getFields().setCoefRecountCurrent(manualInputValidation(resource.getFields().getEstimatedCurrentPrice(), resource.getFields().getEstimatedCurrentPriceH()) / resource.getFields().getEstimatedBasePrice());
            nonNan(resource.getFields().getCoefRecountCurrent());
        } catch (NullPointerException e) {
            resource.getFields().setCoefRecountCurrent(null);
            outputErrorsList.add(new OutputErrors("not enough data", "CoefRecountCurrent"));
        } catch (ArithmeticException e) {
            resource.getFields().setCoefRecountCurrent(null);
            outputErrorsList.add(new OutputErrors("division by zero", "CoefRecountCurrent"));
        }

        try {
            if (resource.getFields().getCoefRecountOfIncludeTaxByDriver() < 0d) {
                resource.getFields().setCoefRecountOfIncludeTaxByDriver(null);
                outputErrorsList.add(new OutputErrors("negative value", "CoefRecountOfIncludeTaxByDriver"));
            } else
                resource.getFields().setCoefRecountOfIncludeTaxByDriver(rounding(calculateIndexSalary() / resource.getFields().getEstimatedBasePriceIncludeTaxByDriver()));
            nonNan(resource.getFields().getCoefRecountOfIncludeTaxByDriver());
        } catch (NullPointerException e) {
            resource.getFields().setCoefRecountOfIncludeTaxByDriver(null);
            outputErrorsList.add(new OutputErrors("not enough data", "CoefRecountOfIncludeTaxByDriver"));
        } catch (ArithmeticException e) {
            resource.getFields().setCoefRecountOfIncludeTaxByDriver(null);
            outputErrorsList.add(new OutputErrors("division by zero", "CoefRecountOfIncludeTaxByDriver"));
        }

    }

    /**
     * calculate Estimated CurrentPrice
     * * @return - result calculate
     */
    public Double calculateEstimatedCurrentPrice() {
        try {
            calculateIndexService();
        } catch (Exception e) {
        }
        try {
            calculateIndexRelocationMachines();
        } catch (Exception e) {
        }
        try {
            calculateIndexWearableParts();
        } catch (Exception e) {
        }
        try {
            calculateIndexPetrol();
        } catch (Exception e) {
        }
        try {
            calculateIndexLubricants();
        } catch (Exception e) {
        }
        try {
            calculateIndexSalary();
        } catch (Exception e) {
        }
        try {
            calculateIndexHydraulicLiquid();
        } catch (Exception e) {
        }
        try {
            resource.getFields().setEstimatedCurrentPriceSubMachine(rounding(calculateIndexAmortization() + calculateIndexService() + calculateIndexRelocationMachines() +
                    calculateIndexWearableParts() + calculateIndexPetrol() + calculateIndexLubricants() +
                    calculateIndexSalary() + calculateIndexHydraulicLiquid()));
            nonNan(resource.getFields().getEstimatedCurrentPriceSubMachine());
        } catch (NullPointerException e) {
            resource.getFields().setEstimatedCurrentPriceSubMachine(null);
            outputErrorsList.add(new OutputErrors("not enough data", "EstimatedCurrentPriceSubMachine"));
        }

        try {
            resource.getFields().setEstimatedCurrentPriceSubInstrument(rounding(calculateIndexAmortization() + calculateIndexService() + calculateIndexRelocationMachines() +
                    calculateIndexWearableParts() + calculateIndexPetrol() + calculateIndexLubricants()));
            nonNan(resource.getFields().getEstimatedCurrentPriceSubInstrument());
        } catch (NullPointerException e) {
            resource.getFields().setEstimatedCurrentPriceSubInstrument(null);
            outputErrorsList.add(new OutputErrors("not enough data", "EstimatedCurrentPriceSubInstrument"));
        }
        try {
            resource.getFields().setEstimatedCurrentPriceSubMechanizm(rounding(calculateIndexAmortization() + calculateIndexService() + calculateIndexRelocationMachines()));
            nonNan(resource.getFields().getEstimatedCurrentPriceSubMechanizm());
        } catch (NullPointerException e) {
            resource.getFields().setEstimatedCurrentPriceSubMechanizm(null);
            outputErrorsList.add(new OutputErrors("not enough data", "EstimatedCurrentPriceSubMechanizm"));
        }
        try {
            if (resource.getFields().getTypeResource().equals("Строительная машина") || resource.getFields().getTypeResource().equals("Автотранспортное средство"))
                resource.getFields().setEstimatedCurrentPrice(rounding(manualInputValidation(resource.getFields().getEstimatedCurrentPriceSubMachine(), resource.getFields().getEstimatedCurrentPriceSubMachineH())));
            if (resource.getFields().getTypeResource().equals("Механизированный инструмент"))
                resource.getFields().setEstimatedCurrentPrice(rounding(manualInputValidation(resource.getFields().getEstimatedCurrentPriceSubInstrument(), resource.getFields().getEstimatedCurrentPriceSubInstrumentH())));
            if (resource.getFields().getTypeResource().equals("Механизм"))
                resource.getFields().setEstimatedCurrentPrice(rounding(manualInputValidation(resource.getFields().getEstimatedCurrentPriceSubMechanizm(), resource.getFields().getEstimatedCurrentPriceSubMechanizmH())));
        } catch (NullPointerException e) {
            resource.getFields().setEstimatedCurrentPrice(null);
            outputErrorsList.add(new OutputErrors("not enough data", "EstimatedCurrentPrice"));
        }

        return manualInputValidation(resource.getFields().getEstimatedCurrentPrice(), resource.getFields().getEstimatedCurrentPriceH());
    }

    /**
     * calculate Index Amortization
     *
     * @return - result calculate
     */

    public Double calculateIndexAmortization() {
        try {
            if (resource.getCostItems().getAmortization().getAvePrice() < 0d) {
                resource.getCostItems().getAmortization().setIndexAmortizationSubOther(null);
                outputErrorsList.add(new OutputErrors("negative value", "IndexAmortizationSubOther"));
            } else
                resource.getCostItems().getAmortization().setIndexAmortizationSubOther(rounding(
                        resource.getCostItems().getAmortization().getAvePrice() / calculateTimeUsefulMachine()));
            nonNan(resource.getCostItems().getAmortization().getIndexAmortizationSubOther());
            resource.getCostItems().getAmortization().setIndexAmortizationSubOther(rounding(resource.getCostItems().getAmortization().getIndexAmortizationSubOther()));

        } catch (NullPointerException e) {
            resource.getCostItems().getAmortization().setIndexAmortizationSubOther(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexAmortizationSubOther"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getAmortization().setIndexAmortizationSubOther(null);
            outputErrorsList.add(new OutputErrors("division by zero", "IndexAmortizationSubOther"));
        }

        try {
            if (resource.getCostItems().getAmortization().getAvePrice() < 0d) {
                resource.getCostItems().getAmortization().setIndexAmortizationSubAuto(null);
                outputErrorsList.add(new OutputErrors("negative value", "IndexAmortizationSubAuto"));
            } else resource.getCostItems().getAmortization().setIndexAmortizationSubAuto(rounding(
                    resource.getCostItems().getAmortization().getAvePrice() / calculateTimeUsefulAutoMachine()));
            nonNan(resource.getCostItems().getAmortization().getIndexAmortizationSubAuto());

            resource.getCostItems().getAmortization().setTimeUsefulMachine(rounding(resource.getCostItems().getAmortization().getTimeUsefulMachine()));
        } catch (NullPointerException e) {
            resource.getCostItems().getAmortization().setIndexAmortizationSubAuto(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexAmortizationSubAuto"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getAmortization().setIndexAmortizationSubAuto(null);
            outputErrorsList.add(new OutputErrors("division by zero", "IndexAmortizationSubAuto"));
        }
        try {
            if (resource.getFields().getTypeResource().equals("Строительная машина") ||
                    resource.getFields().getTypeResource().equals("Механизированный инструмент") ||
                    resource.getFields().getTypeResource().equals("Механизм"))
                resource.getCostItems().getAmortization().setIndexAmortization(rounding(manualInputValidation(resource.getCostItems().getAmortization().getIndexAmortizationSubOther(), resource.getCostItems().getAmortization().getIndexAmortizationSubOtherH())));
            if (resource.getFields().getTypeResource().equals("Автотранспортное средство"))
                resource.getCostItems().getAmortization().setIndexAmortization(rounding(manualInputValidation(resource.getCostItems().getAmortization().getIndexAmortizationSubAuto(), resource.getCostItems().getAmortization().getIndexAmortizationSubAutoH())));
        } catch (NullPointerException e) {
            resource.getCostItems().getAmortization().setIndexAmortization(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexAmortization"));
        }

        return manualInputValidation(resource.getCostItems().getAmortization().getIndexAmortization(), resource.getCostItems().getAmortization().getIndexAmortizationH());
    }

    /**
     * calculate Time Useful Machine
     *
     * @return - result calculate
     */
    public Double calculateTimeUsefulMachine() {
        try {
            if (calculateYearlyModeWork() *
                    resource.getCostItems().getAmortization().getRefineCoefYearlyModeWork() * 100 < 0d) {
                resource.getCostItems().getAmortization().setTimeUsefulMachine(null);
                outputErrorsList.add(new OutputErrors("negative value", "TimeUsefulMachine"));
            } else
                resource.getCostItems().getAmortization().setTimeUsefulMachine(rounding(
                        calculateYearlyModeWork() *
                                resource.getCostItems().getAmortization().getRefineCoefYearlyModeWork() * 100 /
                                resource.getCostItems().getAmortization().getNormaAmortizationOnReduction(), 3));
            nonNan(resource.getCostItems().getAmortization().getTimeUsefulMachine());
            resource.getCostItems().getAmortization().setTimeUsefulMachine(rounding(
                    resource.getCostItems().getAmortization().getTimeUsefulMachine(), 3));

        } catch (NullPointerException e) {
            resource.getCostItems().getAmortization().setTimeUsefulMachine(null);
            outputErrorsList.add(new OutputErrors("not enough data", "TimeUsefulMachine"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getAmortization().setTimeUsefulMachine(null);
            outputErrorsList.add(new OutputErrors("division by zero", "TimeUsefulMachine"));
        }


        return manualInputValidation(resource.getCostItems().getAmortization().getTimeUsefulMachine(), resource.getCostItems().getAmortization().getTimeUsefulMachineH());
    }

    /**
     * calculate Yearly Mode Work
     *
     * @return - result calculate
     */
    public Double calculateYearlyModeWork() {
        try {
            resource.getCostItems().getAmortization().setYearlyModeWorkSubTechnologicalBreakOn(rounding((periodFields.getAmountOfDays() - (periodFields.getAmountWeekend() + periodFields.getNumberOfCelebratedDays() +
                    resource.getCostItems().getAmortization().getDaysDowntimeWeather() +
                    resource.getCostItems().getAmortization().getDaysDowntimeService() +
                    resource.getCostItems().getAmortization().getDaysDowntimeRelocation())) *
                    periodFields.getDurationOfWorkingChange() *
                    resource.getCostItems().getAmortization().getCoefChangeWorkingMachine(), 3));
        } catch (NullPointerException e) {
            resource.getCostItems().getAmortization().setYearlyModeWorkSubTechnologicalBreakOn(null);
            outputErrorsList.add(new OutputErrors("not enough data", "YearlyModeWorkSubTechnologicalBreakOn"));
        }

        try {
            resource.getCostItems().getAmortization().setYearlyModeWorkSubTechnologicalBreakOff(rounding((periodFields.getAmountOfDays() - (
                    resource.getCostItems().getAmortization().getDaysDowntimeWeather() +
                            resource.getCostItems().getAmortization().getDaysDowntimeService() +
                            resource.getCostItems().getAmortization().getDaysDowntimeRelocation())) *
                    periodFields.getDurationOfWorkingChange() *
                    resource.getCostItems().getAmortization().getCoefChangeWorkingMachine(), 3));
            nonNan(resource.getCostItems().getAmortization().getYearlyModeWorkSubTechnologicalBreakOff());
        } catch (NullPointerException e) {
            resource.getCostItems().getAmortization().setYearlyModeWorkSubTechnologicalBreakOff(null);
            outputErrorsList.add(new OutputErrors("not enough data", "YearlyModeWorkSubTechnologicalBreakOff"));
        }
        try {
            if (resource.getCostItems().getAmortization().getIsTechnicalBreak())
                resource.getCostItems().getAmortization().setYearlyModeWork(rounding(manualInputValidation(resource.getCostItems().getAmortization().getYearlyModeWorkSubTechnologicalBreakOn(), resource.getCostItems().getAmortization().getYearlyModeWorkSubTechnologicalBreakOnH()), 3));
            else
                resource.getCostItems().getAmortization().setYearlyModeWork(rounding(manualInputValidation(resource.getCostItems().getAmortization().getYearlyModeWorkSubTechnologicalBreakOff(), resource.getCostItems().getAmortization().getYearlyModeWorkSubTechnologicalBreakOffH()), 3));
        } catch (NullPointerException e) {
            resource.getCostItems().getAmortization().setYearlyModeWork(null);
            outputErrorsList.add(new OutputErrors("not enough data", "YearlyModeWork"));
        }

        return manualInputValidation(resource.getCostItems().getAmortization().getYearlyModeWork(), resource.getCostItems().getAmortization().getYearlyModeWorkH());
    }

    /**
     * calculate Time Useful AutoMachine
     *
     * @return - result calculate
     */
    public Double calculateTimeUsefulAutoMachine() {
        try {
            if (calculateYearlyModeWork() *
                    resource.getCostItems().getAmortization().getRefineCoefYearlyModeWork() * 100 < 0d) {
                resource.getCostItems().getAmortization().setTimeUsefulAutoMachine(null);
                outputErrorsList.add(new OutputErrors("negative value", "TimeUsefulAutoMachine"));
            } else resource.getCostItems().getAmortization().setTimeUsefulAutoMachine(rounding(
                    calculateYearlyModeWork() *
                            resource.getCostItems().getAmortization().getRefineCoefYearlyModeWork() * 100 /
                            (resource.getCostItems().getAmortization().getNormaAmortizationAutoMachine() *
                                    resource.getCostItems().getAmortization().getMiddleYearMileage()), 3));
            nonNan(resource.getCostItems().getAmortization().getTimeUsefulAutoMachine());
            resource.getCostItems().getAmortization().setTimeUsefulAutoMachine(rounding(resource.getCostItems().getAmortization().getTimeUsefulAutoMachine(), 3));

        } catch (NullPointerException e) {
            resource.getCostItems().getAmortization().setTimeUsefulAutoMachine(null);
            outputErrorsList.add(new OutputErrors("not enough data", "TimeUsefulAutoMachine"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getAmortization().setTimeUsefulAutoMachine(null);
            outputErrorsList.add(new OutputErrors("division by zero", "TimeUsefulAutoMachine"));
        }
        return manualInputValidation(resource.getCostItems().getAmortization().getTimeUsefulAutoMachine(), resource.getCostItems().getAmortization().getTimeUsefulAutoMachineH());
    }

    /**
     * calculate Index Service
     *
     * @return - result calculate
     */
    public Double calculateIndexService() {

        try {
            if (resource.getCostItems().getAmortization().getAvePrice() *
                    resource.getCostItems().getRepair().getNormaYearCostService() < 0d) {
                resource.getCostItems().getRepair().setIndexServiceSubImport(null);
                outputErrorsList.add(new OutputErrors("negative value", "IndexServiceSubImport"));
            } else resource.getCostItems().getRepair().setIndexServiceSubImport(rounding(
                    (resource.getCostItems().getAmortization().getAvePrice() *
                            resource.getCostItems().getRepair().getNormaYearCostService()) /
                            (calculateYearlyModeWork() * 100) * periodFields.getCoefCorrectionService()));
            nonNan(resource.getCostItems().getRepair().getIndexServiceSubImport());
            resource.getCostItems().getRepair().setIndexServiceSubImport(rounding(resource.getCostItems().getRepair().getIndexServiceSubImport()));

        } catch (NullPointerException e) {
            resource.getCostItems().getRepair().setIndexServiceSubImport(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexServiceSubImport"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getRepair().setIndexServiceSubImport(null);
            outputErrorsList.add(new OutputErrors("division by zero", "IndexServiceSubImport"));
        }

        try {
            if (resource.getCostItems().getAmortization().getAvePrice() *
                    resource.getCostItems().getRepair().getNormaYearCostService() < 0d) {
                resource.getCostItems().getRepair().setIndexServiceSubNational(null);
                outputErrorsList.add(new OutputErrors("negative value", "IndexServiceSubNational"));
            } else resource.getCostItems().getRepair().setIndexServiceSubNational(rounding(
                    (resource.getCostItems().getAmortization().getAvePrice() *
                            resource.getCostItems().getRepair().getNormaYearCostService()) /
                            (calculateYearlyModeWork() * 100)));
            nonNan(resource.getCostItems().getRepair().getIndexServiceSubNational());
            resource.getCostItems().getRepair().setIndexServiceSubNational(rounding(resource.getCostItems().getRepair().getIndexServiceSubNational()));

        } catch (NullPointerException e) {
            resource.getCostItems().getRepair().setIndexServiceSubNational(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexServiceSubNational"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getRepair().setIndexServiceSubNational(null);
            outputErrorsList.add(new OutputErrors("division by zero", "IndexServiceSubNational"));
        }
        try {
            if (!resource.getCostItems().getRepair().getIndexServiceF())
                resource.getCostItems().getRepair().setIndexService(rounding(manualInputValidation(resource.getCostItems().getRepair().getIndexServiceSubNational(), resource.getCostItems().getRepair().getIndexServiceSubNationalH())));
            else if (!periodFields.getCoefCorrectionService().equals(0d))
                resource.getCostItems().getRepair().setIndexService(rounding(manualInputValidation(resource.getCostItems().getRepair().getIndexServiceSubImport(), resource.getCostItems().getRepair().getIndexServiceSubImportH())));
        } catch (NullPointerException e) {
            resource.getCostItems().getRepair().setIndexService(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexService"));
        }
        return manualInputValidation(resource.getCostItems().getRepair().getIndexService(), resource.getCostItems().getRepair().getIndexServiceH());
    }

    /**
     * calculate Index Wearable Parts
     *
     * @return - result calculate
     */
    public Double calculateIndexWearableParts() {

        try {
            resource.getCostItems().getWearable().setIndexWearableParts(rounding(calculateIndexService() * resource.getCostItems().getWearable().getCoefWearableParts()));
            nonNan(resource.getCostItems().getWearable().getIndexWearableParts());
        } catch (NullPointerException e) {
            resource.getCostItems().getWearable().setIndexWearableParts(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexWearableParts"));
        }
        return manualInputValidation(resource.getCostItems().getWearable().getIndexWearableParts(), resource.getCostItems().getWearable().getIndexWearablePartsH());
    }

    /**
     * calculate Index Petrol
     *
     * @return - result calculate
     */
    public Double calculateIndexPetrol() {

        try {
            resource.getCostItems().getPower().setIndexPetrolSubInstrumentPetrol(rounding(resource.getCostItems().getPower().getNormaConsumptionPetrolTechn() * periodFields.getEstimatedPricePetrol()));
            nonNan(resource.getCostItems().getPower().getIndexPetrolSubInstrumentPetrol());
        } catch (NullPointerException e) {
            resource.getCostItems().getPower().setIndexPetrolSubInstrumentPetrol(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexPetrolSubInstrumentPetrol"));
        }

        try {
            resource.getCostItems().getPower().setIndexPetrolSubInstrumentDiesel(rounding(resource.getCostItems().getPower().getLinageNormConsumptionTechnological() * periodFields.getEstimatedPriceFuel()));
            nonNan(resource.getCostItems().getPower().getIndexPetrolSubInstrumentDiesel());
        } catch (NullPointerException e) {
            resource.getCostItems().getPower().setIndexPetrolSubInstrumentDiesel(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexPetrolSubInstrumentDiesel"));
        }

        try {
            if (resource.getCostItems().getPower().getNormaConsumptionPetrolExploitation() *
                    periodFields.getDensityPetrol() *
                    resource.getCostItems().getAmortization().getMiddleYearMileage() < 0d) {
                resource.getCostItems().getPower().setIndexPetrolSubAutoPetrol(null);
                outputErrorsList.add(new OutputErrors("negative value", "IndexPetrolSubAutoPetrol"));
            } else resource.getCostItems().getPower().setIndexPetrolSubAutoPetrol(rounding(
                    (resource.getCostItems().getPower().getNormaConsumptionPetrolExploitation() *
                            periodFields.getDensityPetrol() *
                            resource.getCostItems().getAmortization().getMiddleYearMileage() /
                            calculateYearlyModeWork()) * periodFields.getEstimatedPricePetrol()));
            nonNan(resource.getCostItems().getPower().getIndexPetrolSubAutoPetrol());
            resource.getCostItems().getPower().setIndexPetrolSubAutoPetrol(rounding(resource.getCostItems().getPower().getIndexPetrolSubAutoPetrol()));

        } catch (NullPointerException e) {
            resource.getCostItems().getPower().setIndexPetrolSubAutoPetrol(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexPetrolSubAutoPetrol"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getPower().setIndexPetrolSubAutoPetrol(null);
            outputErrorsList.add(new OutputErrors("division by zero", "IndexPetrolSubAutoPetrol"));
        }

        try {
            if (resource.getCostItems().getPower().getNormaConsumptionPetrolExploitation() *
                    periodFields.getDensityFuel() *
                    resource.getCostItems().getAmortization().getMiddleYearMileage() < 0d) {
                resource.getCostItems().getPower().setIndexPetrolSubAutoDiesel(null);
                outputErrorsList.add(new OutputErrors("negative value", "IndexPetrolSubAutoDiesel"));
            } else resource.getCostItems().getPower().setIndexPetrolSubAutoDiesel(rounding(
                    (resource.getCostItems().getPower().getNormaConsumptionPetrolExploitation() *
                            periodFields.getDensityFuel() *
                            resource.getCostItems().getAmortization().getMiddleYearMileage() /
                            calculateYearlyModeWork()) * periodFields.getEstimatedPriceFuel())
            );
            nonNan(resource.getCostItems().getPower().getIndexPetrolSubAutoDiesel());
            resource.getCostItems().getPower().setIndexPetrolSubAutoDiesel(rounding(resource.getCostItems().getPower().getIndexPetrolSubAutoDiesel()));

        } catch (NullPointerException e) {
            resource.getCostItems().getPower().setIndexPetrolSubAutoDiesel(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexPetrolSubAutoDiesel"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getPower().setIndexPetrolSubAutoDiesel(null);
            outputErrorsList.add(new OutputErrors("division by zero", "IndexPetrolSubAutoDiesel"));
        }

        try {
            resource.getCostItems().getPower().setIndexPetrolSubAllElectricy(rounding(
                    1.1 * resource.getCostItems().getPower().getPowerMotor() *
                            resource.getCostItems().getPower().getCoefPowerMotor() *
                            resource.getCostItems().getPower().getCoefTimeMotor() *
                            periodFields.getEstimatedPriceElectricity()
            ));
            nonNan(resource.getCostItems().getPower().getIndexPetrolSubAllElectricy());
        } catch (NullPointerException e) {
            resource.getCostItems().getPower().setIndexPetrolSubAllElectricy(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexPetrolSubAllElectricy"));
        }

        try {
            resource.getCostItems().getPower().setIndexPetrolSubAllCompressionAir(rounding(calculateIndexPetrolSubAllCompressionAir()));
            nonNan(resource.getCostItems().getPower().getIndexPetrolSubAllCompressionAir());
        } catch (NullPointerException e) {
            resource.getCostItems().getPower().setIndexPetrolSubAllCompressionAir(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexPetrolSubAllCompressionAir"));
        }
        boolean petrolBool = false;
        boolean deaselBool = false;
        boolean airBool = false;
        boolean electricyBool = false;
        if (resource.getFields().getPowers() != null)
            for (String pow : resource.getFields().getPowers()) {
                if (pow.equals("Бензин")) petrolBool = true;
                if (pow.equals("Дизельное топливо")) deaselBool = true;
                if (pow.equals("Электроэнергия")) electricyBool = true;
                if (pow.equals("Сжатый воздух")) airBool = true;
            }
        resource.getCostItems().getPower().setIndexPetrol(0d);
        if (resource.getFields().getTypeResource().equals("Строительная машина") || resource.getFields().getTypeResource().equals("Механизированный инструмент")) {
            if (petrolBool)
                resource.getCostItems().getPower().setIndexPetrol(rounding(resource.getCostItems().getPower().getIndexPetrol() + manualInputValidation(resource.getCostItems().getPower().getIndexPetrolSubInstrumentPetrol(), resource.getCostItems().getPower().getIndexPetrolSubInstrumentPetrolH())));
            if (deaselBool)
                resource.getCostItems().getPower().setIndexPetrol(rounding(resource.getCostItems().getPower().getIndexPetrol() + manualInputValidation(resource.getCostItems().getPower().getIndexPetrolSubInstrumentDiesel(), resource.getCostItems().getPower().getIndexPetrolSubInstrumentDieselH())));
        }

        if (resource.getFields().getTypeResource().equals("Автотранспортное средство")) {
            if (petrolBool)
                resource.getCostItems().getPower().setIndexPetrol(rounding(resource.getCostItems().getPower().getIndexPetrol() + manualInputValidation(resource.getCostItems().getPower().getIndexPetrolSubAutoPetrol(), resource.getCostItems().getPower().getIndexPetrolSubAutoPetrolH())));
            if (deaselBool)
                resource.getCostItems().getPower().setIndexPetrol(rounding(resource.getCostItems().getPower().getIndexPetrol() + manualInputValidation(resource.getCostItems().getPower().getIndexPetrolSubAutoDiesel(), resource.getCostItems().getPower().getIndexPetrolSubAutoDieselH())));

        }
        Double sum = resource.getCostItems().getPower().getIndexPetrol();
        try {
            if (resource.getFields().getTypeResource().equals("Строительная машина") || resource.getFields().getTypeResource().equals("Механизированный инструмент")
                    || resource.getFields().getTypeResource().equals("Автотранспортное средство"))
                if (electricyBool)
                    resource.getCostItems().getPower().setIndexPetrol(rounding(sum + manualInputValidation(resource.getCostItems().getPower().getIndexPetrolSubAllElectricy(), resource.getCostItems().getPower().getIndexPetrolSubAllElectricyH())));
            sum = resource.getCostItems().getPower().getIndexPetrol();
            if (resource.getFields().getTypeResource().equals("Строительная машина") || resource.getFields().getTypeResource().equals("Механизированный инструмент")
                    || resource.getFields().getTypeResource().equals("Автотранспортное средство"))
                if (airBool)
                    resource.getCostItems().getPower().setIndexPetrol(rounding(sum + manualInputValidation(resource.getCostItems().getPower().getIndexPetrolSubAllCompressionAir(), resource.getCostItems().getPower().getIndexPetrolSubAllCompressionAirH())));
        } catch (NullPointerException e) {
            resource.getCostItems().getPower().setIndexPetrol(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexPetrol"));
        }
        return manualInputValidation(resource.getCostItems().getPower().getIndexPetrol(), resource.getCostItems().getPower().getIndexPetrolH());
    }

    /**
     * calculate Estimated Price Compressed Air
     *
     * @return - result calculate
     */
    public Double calculateIndexPetrolSubAllCompressionAir() {
        try {
            resource.getCostItems().getPower().setIndexPetrolSubAllCompressionAirCompressorExclude(rounding(
                    resource.getCostItems().getPower().getNormaConsumptionCompressedAir() *
                            periodFields.getEstimatedPriceCompressedAir()));
            nonNan(resource.getCostItems().getPower().getIndexPetrolSubAllCompressionAirCompressorExclude());
            resource.getCostItems().getPower().setIndexPetrolSubAllCompressionAirCompressorExclude(rounding(
                    resource.getCostItems().getPower().getIndexPetrolSubAllCompressionAirCompressorExclude()));

        } catch (NullPointerException e) {
            resource.getCostItems().getPower().setIndexPetrolSubAllCompressionAirCompressorExclude(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexPetrolSubAllCompressionAirCompressorExclude"));
        }

        try {
            if (resource.getCostItems().getPower().getNormaConsumptionCompressedAir() *
                    resource.getCostItems().getAmortization().getYearlyModeWork() < 0d) {
                resource.getCostItems().getPower().setIndexPetrolSubAllCompressionAirCompressorInclude(null);
                outputErrorsList.add(new OutputErrors("negative value", "IndexPetrolSubAllCompressionAirCompressorInclude"));
            } else resource.getCostItems().getPower().setIndexPetrolSubAllCompressionAirCompressorInclude(rounding(
                    resource.getCostItems().getPower().getNormaConsumptionCompressedAir() *
                            resource.getCostItems().getPower().getYearlyModeWork() /
                            (resource.getCostItems().getPower().getPassportPowerInstallation() *
                                    resource.getCostItems().getPower().getCoefPowerMotorCurrentGroupOfPower() *
                                    resource.getCostItems().getPower().getCoefPowerMotorCurrentGroupOfTime())));

            nonNan(resource.getCostItems().getPower().getIndexPetrolSubAllCompressionAirCompressorInclude());
            resource.getCostItems().getPower().setIndexPetrolSubAllCompressionAirCompressorInclude(rounding(
                    resource.getCostItems().getPower().getIndexPetrolSubAllCompressionAirCompressorInclude()));

        } catch (NullPointerException e) {
            resource.getCostItems().getPower().setIndexPetrolSubAllCompressionAirCompressorInclude(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexPetrolSubAllCompressionAirCompressorInclude"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getPower().setIndexPetrolSubAllCompressionAirCompressorInclude(null);
            outputErrorsList.add(new OutputErrors("division by zero", "IndexPetrolSubAllCompressionAirCompressorInclude"));
        }

        try {
            if (!resource.getCostItems().getPower().getIsCompressorEquipment())
                resource.getCostItems().getPower().setIndexPetrolSubAllCompressionAir(rounding(manualInputValidation(resource.getCostItems().getPower().getIndexPetrolSubAllCompressionAirCompressorExclude(), resource.getCostItems().getPower().getIndexPetrolSubAllCompressionAirCompressorExcludeH())));
            else
                resource.getCostItems().getPower().setIndexPetrolSubAllCompressionAir(rounding(manualInputValidation(resource.getCostItems().getPower().getIndexPetrolSubAllCompressionAirCompressorInclude(), resource.getCostItems().getPower().getIndexPetrolSubAllCompressionAirCompressorIncludeH())));
        } catch (NullPointerException e) {
            resource.getCostItems().getPower().setIndexPetrolSubAllCompressionAir(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexPetrolSubAllCompressionAir"));
        }

        return manualInputValidation(resource.getCostItems().getPower().getIndexPetrolSubAllCompressionAir(), resource.getCostItems().getPower().getIndexPetrolSubAllCompressionAirH());
    }

    /**
     * calculate Index Lubricants
     *
     * @return - result calculate
     */
    public Double calculateIndexLubricants() {
        try {
            resource.getCostItems().getLubricants().setIndexLubricantsSubInstrumentPetrol(rounding(
                    (0.035d * periodFields.getEstimatedPriceEngineOils() +
                            0.004d * periodFields.getEstimatedPriceLubricants() +
                            0.015d * periodFields.getEstimatedPriceTransmissionOils()) *
                            resource.getCostItems().getPower().getNormaConsumptionPetrolTechn())
            );
            nonNan(resource.getCostItems().getLubricants().getIndexLubricantsSubInstrumentPetrol());

        } catch (NullPointerException e) {
            resource.getCostItems().getLubricants().setIndexLubricantsSubInstrumentPetrol(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexLubricantsSubInstrumentPetrol"));
        }

        try {
            resource.getCostItems().getLubricants().setIndexLubricantsSubInstrumentDiesel(rounding(
                    (0.044d * periodFields.getEstimatedPriceEngineOils() +
                            0.004d * periodFields.getEstimatedPriceLubricants() +
                            0.015d * periodFields.getEstimatedPriceTransmissionOils()) *
                            resource.getCostItems().getPower().getLinageNormConsumptionTechnological()
            ));
            nonNan(resource.getCostItems().getLubricants().getIndexLubricantsSubInstrumentDiesel());

        } catch (NullPointerException e) {
            resource.getCostItems().getLubricants().setIndexLubricantsSubInstrumentDiesel(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexLubricantsSubInstrumentDiesel"));
        }

        try {
            if ((0.044 * periodFields.getEstimatedPriceEngineOils() +
                    0.004d * periodFields.getEstimatedPriceLubricants() +
                    0.015d * periodFields.getEstimatedPriceTransmissionOils()) *
                    resource.getCostItems().getPower().getNormaConsumptionPetrolExploitation() *
                    periodFields.getDensityPetrol() *
                    resource.getCostItems().getAmortization().getMiddleYearMileage() < 0d) {
                resource.getCostItems().getLubricants().setIndexLubricantsSubAutoPetrol(null);
                outputErrorsList.add(new OutputErrors("negative value", "IndexLubricantsSubAutoPetrol"));
            } else
                resource.getCostItems().getLubricants().setIndexLubricantsSubAutoPetrol(rounding(
                        (0.044 * periodFields.getEstimatedPriceEngineOils() +
                                0.004d * periodFields.getEstimatedPriceLubricants() +
                                0.015d * periodFields.getEstimatedPriceTransmissionOils()) *
                                resource.getCostItems().getPower().getNormaConsumptionPetrolExploitation() *
                                periodFields.getDensityPetrol() *
                                resource.getCostItems().getAmortization().getMiddleYearMileage() /
                                calculateYearlyModeWork()
                ));
            nonNan(resource.getCostItems().getLubricants().getIndexLubricantsSubAutoPetrol());

        } catch (NullPointerException e) {
            resource.getCostItems().getLubricants().setIndexLubricantsSubAutoPetrol(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexLubricantsSubAutoPetrol"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getLubricants().setIndexLubricantsSubAutoPetrol(null);
            outputErrorsList.add(new OutputErrors("division by zero", "IndexLubricantsSubAutoPetrol"));
        }

        try {
            if ((0.044d * periodFields.getEstimatedPriceEngineOils() +
                    0.004d * periodFields.getEstimatedPriceLubricants() +
                    0.015d * periodFields.getEstimatedPriceTransmissionOils()) *
                    resource.getCostItems().getPower().getLinageNormConsumptionExpluatation() *
                    periodFields.getDensityFuel() *
                    resource.getCostItems().getAmortization().getMiddleYearMileage() < 0d) {
                resource.getCostItems().getLubricants().setIndexLubricantsSubAutoDiesel(null);
                outputErrorsList.add(new OutputErrors("negative value", "IndexLubricantsSubAutoDiesel"));
            } else
                resource.getCostItems().getLubricants().setIndexLubricantsSubAutoDiesel(rounding((0.044d * periodFields.getEstimatedPriceEngineOils() +
                        0.004d * periodFields.getEstimatedPriceLubricants() +
                        0.015d * periodFields.getEstimatedPriceTransmissionOils()) *
                        resource.getCostItems().getPower().getLinageNormConsumptionExpluatation() *
                        periodFields.getDensityFuel() *
                        resource.getCostItems().getAmortization().getMiddleYearMileage() /
                        calculateYearlyModeWork()
                ));
            nonNan(resource.getCostItems().getLubricants().getIndexLubricantsSubAutoDiesel());

        } catch (NullPointerException e) {
            resource.getCostItems().getLubricants().setIndexLubricantsSubAutoDiesel(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexLubricantsSubAutoDiesel"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getLubricants().setIndexLubricantsSubAutoDiesel(null);
            outputErrorsList.add(new OutputErrors("division by zero", "IndexLubricantsSubAutoDiesel"));
        }

        try {
            resource.getCostItems().getLubricants().setIndexLubricantsSubElectricy(rounding(0.02d * manualInputValidation(resource.getCostItems().getPower().getIndexPetrolSubAllElectricy(), resource.getCostItems().getPower().getIndexPetrolSubAllElectricyH())));
            nonNan(resource.getCostItems().getLubricants().getIndexLubricantsSubElectricy());

        } catch (NullPointerException e) {
            resource.getCostItems().getLubricants().setIndexLubricantsSubElectricy(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexLubricantsSubElectricy"));
        }
        try {
            resource.getCostItems().getLubricants().setIndexLubricantsSubCompressionAir(rounding(0.02d * manualInputValidation(resource.getCostItems().getPower().getIndexPetrolSubAllCompressionAir(), resource.getCostItems().getPower().getIndexPetrolSubAllCompressionAirH())));
            nonNan(resource.getCostItems().getLubricants().getIndexLubricantsSubCompressionAir());

        } catch (NullPointerException e) {
            resource.getCostItems().getLubricants().setIndexLubricantsSubCompressionAir(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexLubricantsSubCompressionAir"));
        }
        boolean petrolBool = false;
        boolean deaselBool = false;
        boolean airBool = false;
        boolean electricyBool = false;

        if (resource.getFields().getPowers() != null)
            for (String pow : resource.getFields().getPowers()) {
                if (pow.equals("Бензин")) petrolBool = true;
                if (pow.equals("Дизельное топливо")) deaselBool = true;
                if (pow.equals("Электроэнергия")) airBool = true;
                if (pow.equals("Сжатый воздух")) electricyBool = true;
            }
        Double sum = 0d;
        resource.getCostItems().getLubricants().setIndexLubricants(0d);
        if (resource.getFields().getTypeResource().equals("Строительная машина") || resource.getFields().getTypeResource().equals("Механизированный инструмент")) {
            if (petrolBool)
                resource.getCostItems().getLubricants().setIndexLubricants(rounding(manualInputValidation(resource.getCostItems().getLubricants().getIndexLubricantsSubInstrumentPetrol(), resource.getCostItems().getLubricants().getIndexLubricantsSubInstrumentPetrolH())));
            sum = resource.getCostItems().getLubricants().getIndexLubricants();

            if (deaselBool)
                resource.getCostItems().getLubricants().setIndexLubricants(sum + manualInputValidation(resource.getCostItems().getLubricants().getIndexLubricantsSubInstrumentDiesel(), resource.getCostItems().getLubricants().getIndexLubricantsSubInstrumentDieselH()));
            sum = resource.getCostItems().getLubricants().getIndexLubricants();

        }

        if (resource.getFields().getTypeResource().equals("Автотранспортное средство")) {
            if (petrolBool)
                resource.getCostItems().getLubricants().setIndexLubricants(rounding(manualInputValidation(resource.getCostItems().getLubricants().getIndexLubricantsSubAutoPetrol(), resource.getCostItems().getLubricants().getIndexLubricantsSubAutoPetrolH())));
            sum = resource.getCostItems().getLubricants().getIndexLubricants();

            if (deaselBool)
                resource.getCostItems().getLubricants().setIndexLubricants(rounding(sum + manualInputValidation(resource.getCostItems().getLubricants().getIndexLubricantsSubAutoDiesel(), resource.getCostItems().getLubricants().getIndexLubricantsSubAutoDieselH())));
            sum = resource.getCostItems().getLubricants().getIndexLubricants();

        }

        try {
            if (electricyBool)
                resource.getCostItems().getLubricants().setIndexLubricants(rounding(sum + manualInputValidation(resource.getCostItems().getLubricants().getIndexLubricantsSubElectricy(), resource.getCostItems().getLubricants().getIndexLubricantsSubElectricyH())));
            sum = resource.getCostItems().getLubricants().getIndexLubricants();

            if (airBool)
                resource.getCostItems().getLubricants().setIndexLubricants(rounding(sum + manualInputValidation(resource.getCostItems().getLubricants().getIndexLubricantsSubCompressionAir(), resource.getCostItems().getLubricants().getIndexLubricantsSubCompressionAirH())));
        } catch (NullPointerException e) {
            resource.getCostItems().getLubricants().setIndexLubricants(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexLubricants"));
        }

        return manualInputValidation(resource.getCostItems().getLubricants().getIndexLubricants(), resource.getCostItems().getLubricants().getIndexLubricantsH());
    }

    /**
     * calculate Index Hydraulic Liquid
     *
     * @return - result calculate
     */
    public Double calculateIndexHydraulicLiquid() {

        try {
            resource.getCostItems().getLiquid().setIndexHydraulicLiquidSubSummer(rounding(resource.getCostItems().getLiquid().getNormaConsumptionHydraulicLiquid() * periodFields.getEstimatedPriceOfHydraulicLiquid()));
            nonNan(resource.getCostItems().getLiquid().getIndexHydraulicLiquidSubSummer());
        } catch (NullPointerException e) {
            resource.getCostItems().getLiquid().setIndexHydraulicLiquidSubSummer(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexHydraulicLiquidSubSummer"));
        }

        try {
            if (resource.getCostItems().getLiquid().getIndexHydraulicSystemCapacity() *
                    periodFields.getIndexHydraulicFluidDensity() *
                    periodFields.getIndexHydraulicFluidReplacement() < 0d) {
                resource.getCostItems().getLiquid().setIndexHydraulicLiquidSubNotsummer(null);
                outputErrorsList.add(new OutputErrors("negative value", "IndexHydraulicLiquidSubNotsummer"));
            } else
                resource.getCostItems().getLiquid().setIndexHydraulicLiquidSubNotsummer(rounding(
                        (resource.getCostItems().getLiquid().getIndexHydraulicSystemCapacity() *
                                periodFields.getIndexHydraulicFluidDensity() *
                                periodFields.getIndexHydraulicFluidReplacement() / calculateYearlyModeWork()) *
                                periodFields.getEstimatedPriceOfHydraulicLiquid()));

            nonNan(resource.getCostItems().getLiquid().getIndexHydraulicLiquidSubNotsummer());
            resource.getCostItems().getLiquid().setIndexHydraulicLiquidSubNotsummer(rounding(
                    resource.getCostItems().getLiquid().getIndexHydraulicLiquidSubNotsummer()));

        } catch (NullPointerException e) {
            resource.getCostItems().getLiquid().setIndexHydraulicLiquidSubNotsummer(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexHydraulicLiquidSubNotsummer"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getLiquid().setIndexHydraulicLiquidSubNotsummer(null);
            outputErrorsList.add(new OutputErrors("division by zero", "IndexHydraulicLiquidSubNotsummer"));
        }

        try {
            if (resource.getCostItems().getLiquid().getIndexHydraulicLiquidF() == false)
                resource.getCostItems().getLiquid().setIndexHydraulicLiquid(rounding(manualInputValidation(resource.getCostItems().getLiquid().getIndexHydraulicLiquidSubNotsummer(), resource.getCostItems().getLiquid().getIndexHydraulicLiquidSubNotsummerH())));
            else
                resource.getCostItems().getLiquid().setIndexHydraulicLiquid(rounding(manualInputValidation(resource.getCostItems().getLiquid().getIndexHydraulicLiquidSubSummer(), resource.getCostItems().getLiquid().getIndexHydraulicLiquidSubSummerH())));
        } catch (NullPointerException e) {
            resource.getCostItems().getLiquid().setIndexHydraulicLiquid(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexHydraulicLiquid"));
        }


        return manualInputValidation(resource.getCostItems().getLiquid().getIndexHydraulicLiquid(), resource.getCostItems().getLiquid().getIndexHydraulicLiquidH());
    }

    /**
     * calculate Index Relocation Machines
     *
     * @return - result calculate
     */
    public Double calculateIndexRelocationMachines() {
        Double tempCostOnEnegyItsOwnWhenChosePetrolAndDiesel = 0d;
        try {
            Double tempCostOnEnergyItsOwn = rounding(manualInputValidation(resource.getCostItems().getRelocation().getCostsOnEnergyItsOwnSubPetrol(), resource.getCostItems().getRelocation().getCostsOnEnergyItsOwnSubPetrolH()));
            calculateCostsOnEnergyItsOwn();
            if ((calculateIndexSalary() +
                    tempCostOnEnergyItsOwn +
                    calculateIndexLubricants()) *
                    resource.getCostItems().getRelocation().getIndexMiddleYearReplacementOfMachine() < 0d) {
                resource.getCostItems().getRelocation().setIndexRelocationMachinesSubPetrolSomeself(null);
                outputErrorsList.add(new OutputErrors("negative value", "IndexRelocationMachinesSubPetrolSomeself"));
            } else
                resource.getCostItems().getRelocation().setIndexRelocationMachinesSubPetrolSomeself(rounding((calculateIndexSalary() +
                        tempCostOnEnergyItsOwn +
                        calculateIndexLubricants()) *
                        resource.getCostItems().getRelocation().getIndexMiddleYearReplacementOfMachine() /
                        calculateIndexMiddleYearTimeOfWorkOfTheMachine()));
            nonNan(resource.getCostItems().getRelocation().getIndexRelocationMachinesSubPetrolSomeself());
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubPetrolSomeself(rounding(
                    resource.getCostItems().getRelocation().getIndexRelocationMachinesSubPetrolSomeself()));

        } catch (NullPointerException e) {
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubPetrolSomeself(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexRelocationMachinesSubPetrolSomeself"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubPetrolSomeself(null);
            outputErrorsList.add(new OutputErrors("division by zero", "IndexRelocationMachinesSubPetrolSomeself"));
        }
        try {
            Double tempCostOnEnergyItsOwn = rounding(manualInputValidation(resource.getCostItems().getRelocation().getCostsOnEnergyItsOwnSubDiesel(), resource.getCostItems().getRelocation().getCostsOnEnergyItsOwnSubDieselH()));
            if ((calculateIndexSalary() +
                    tempCostOnEnergyItsOwn +
                    calculateIndexLubricants()) *
                    resource.getCostItems().getRelocation().getIndexMiddleYearReplacementOfMachine() < 0d) {
                resource.getCostItems().getRelocation().setIndexRelocationMachinesSubDiesel(null);
                outputErrorsList.add(new OutputErrors("negative value", "setIndexRelocationMachinesSubDiesel"));
            } else
                resource.getCostItems().getRelocation().setIndexRelocationMachinesSubDiesel(rounding((calculateIndexSalary() +
                        tempCostOnEnergyItsOwn +
                        calculateIndexLubricants()) *
                        resource.getCostItems().getRelocation().getIndexMiddleYearReplacementOfMachine() /
                        calculateIndexMiddleYearTimeOfWorkOfTheMachine()));
            nonNan(resource.getCostItems().getRelocation().getIndexRelocationMachinesSubDiesel());
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubDiesel(rounding(
                    resource.getCostItems().getRelocation().getIndexRelocationMachinesSubDiesel()));

        } catch (NullPointerException e) {
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubDiesel(null);
            outputErrorsList.add(new OutputErrors("not enough data", "setIndexRelocationMachinesSubDiesel"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubDiesel(null);
            outputErrorsList.add(new OutputErrors("division by zero", "setIndexRelocationMachinesSubDiesel"));
        }
        try {

            if ((calculateIndexSalary() +
                    calculateCostsOnEnergyItsOwn() +
                    calculateIndexLubricants()) *
                    resource.getCostItems().getRelocation().getIndexMiddleYearReplacementOfMachine() < 0d) {
                tempCostOnEnegyItsOwnWhenChosePetrolAndDiesel = null;
                outputErrorsList.add(new OutputErrors("negative value", "IndexRelocationMachinesSubPetrolSomeself"));
            } else
                tempCostOnEnegyItsOwnWhenChosePetrolAndDiesel = rounding((calculateIndexSalary() +
                        calculateCostsOnEnergyItsOwn() +
                        calculateIndexLubricants()) *
                        resource.getCostItems().getRelocation().getIndexMiddleYearReplacementOfMachine() /
                        calculateIndexMiddleYearTimeOfWorkOfTheMachine());
            nonNan(tempCostOnEnegyItsOwnWhenChosePetrolAndDiesel);

        } catch (NullPointerException e) {
            tempCostOnEnegyItsOwnWhenChosePetrolAndDiesel = null;
            outputErrorsList.add(new OutputErrors("not enough data", "IndexRelocationMachinesSubPetrolSomeself"));
        } catch (ArithmeticException e) {
            tempCostOnEnegyItsOwnWhenChosePetrolAndDiesel = null;
            outputErrorsList.add(new OutputErrors("division by zero", "IndexRelocationMachinesSubPetrolSomeself"));
        }
        try {
            if ((resource.getCostItems().getRelocation().getEstimatedPriceOnTractor() +
                    resource.getCostItems().getRelocation().getEstimatedPriceMachineSupport() +
                    calculateIndexSalary()) *
                    resource.getCostItems().getRelocation().getIndexMiddleYearReplacementOfMachine() < 0d) {
                resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTow(null);
                outputErrorsList.add(new OutputErrors("negative value", "IndexRelocationMachinesSubTow"));
            } else
                resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTow(rounding((resource.getCostItems().getRelocation().getEstimatedPriceOnTractor() +
                        resource.getCostItems().getRelocation().getEstimatedPriceMachineSupport() +
                        calculateIndexSalary()) *
                        resource.getCostItems().getRelocation().getIndexMiddleYearReplacementOfMachine() /
                        (calculateYearlyModeWork() / resource.getCostItems().getRelocation().getAmountReplacement())));
            nonNan(resource.getCostItems().getRelocation().getIndexRelocationMachinesSubTow());
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTow(rounding(
                    resource.getCostItems().getRelocation().getIndexRelocationMachinesSubTow()));

        } catch (NullPointerException e) {
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTow(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexRelocationMachinesSubTow"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTow(null);
            outputErrorsList.add(new OutputErrors("division by zero", "IndexRelocationMachinesSubTow"));
        }
        try {
            if ((resource.getCostItems().getRelocation().getEstimatedPriceOnTractor() +
                    resource.getCostItems().getRelocation().getEstimatedPriceMachineSupport() +
                    resource.getCostItems().getRelocation().getEstimatedPriceOnTrailer() +
                    calculateIndexSalary()) *
                    resource.getCostItems().getRelocation().getIndexMiddleYearTimeOfWorkOfTheMachineOnTrailer() < 0d) {
                resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTrailerWithoutMount(null);
                outputErrorsList.add(new OutputErrors("negative value", "IndexRelocationMachinesSubTrailerWithoutMount"));
            } else
                resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTrailerWithoutMount(rounding((resource.getCostItems().getRelocation().getEstimatedPriceOnTractor() +
                        resource.getCostItems().getRelocation().getEstimatedPriceMachineSupport() +
                        resource.getCostItems().getRelocation().getEstimatedPriceOnTrailer() +
                        calculateIndexSalary()) *
                        resource.getCostItems().getRelocation().getIndexMiddleYearTimeOfWorkOfTheMachineOnTrailer()
                        / resource.getCostItems().getRelocation().getIndexYearTimeOfWorkOfTheMachineOnTrailer()));
            nonNan(resource.getCostItems().getRelocation().getIndexRelocationMachinesSubTrailerWithoutMount());
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTrailerWithoutMount(rounding(
                    resource.getCostItems().getRelocation().getIndexRelocationMachinesSubTrailerWithoutMount()));

        } catch (NullPointerException e) {
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTrailerWithoutMount(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexRelocationMachinesSubTrailerWithoutMount"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTrailerWithoutMount(null);
            outputErrorsList.add(new OutputErrors("division by zero", "IndexRelocationMachinesSubTrailerWithoutMount"));
        }

        try {
            if ((resource.getCostItems().getRelocation().getEstimatedPriceOnTractor() +
                    resource.getCostItems().getRelocation().getEstimatedPriceMachineSupport() +
                    resource.getCostItems().getSalary().getIndexSalary() +
                    resource.getCostItems().getRelocation().getIndexMiddleYearReplacementOfMachine()) < 0d) {
                resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTow(null);
                outputErrorsList.add(new OutputErrors("negative value", "IndexRelocationMachinesSubTow"));
            } else
                resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTow(rounding(((resource.getCostItems().getRelocation().getEstimatedPriceOnTractor() +
                        resource.getCostItems().getRelocation().getEstimatedPriceMachineSupport() +
                        resource.getCostItems().getSalary().getIndexSalary() +
                        resource.getCostItems().getRelocation().getIndexMiddleYearReplacementOfMachine())
                        / calculateIndexYearTimeOfWorkOfTheMachineOnTow())));
            nonNan(resource.getCostItems().getRelocation().getIndexRelocationMachinesSubTow());
        } catch (NullPointerException e) {
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTrailerWithoutMount(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexRelocationMachinesSubTow"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTrailerWithoutMount(null);
            outputErrorsList.add(new OutputErrors("division by zero", "IndexRelocationMachinesSubTow"));
        }

        try {
            calculateIndexSalaryWorkerForRelocating();
            Double sumPriceOnCrane = 0d;
            Double sumTimeExploitationCrane = 0d;
            if (resource.getCostItems().getRelocation().getCranes() != null)
                for (Crane cr : resource.getCostItems().getRelocation().getCranes()) {
                    sumPriceOnCrane += cr.getPriceOnCrane();
                    sumTimeExploitationCrane += cr.getTimeExploitationCrane();
                }
            if (((resource.getCostItems().getRelocation().getEstimatedPriceOnTractor() +
                    resource.getCostItems().getRelocation().getEstimatedPriceMachineSupport() +
                    resource.getCostItems().getRelocation().getEstimatedPriceOnTrailer()) *
                    resource.getCostItems().getRelocation().getIndexMiddleYearReplacementOfMachine() +
                    sumPriceOnCrane *
                            sumTimeExploitationCrane +
                    (calculateIndexSalary() *
                            resource.getCostItems().getRelocation().getTimeOfTheWorkingWorker() +
                            calculateIndexSalaryWorkerForRelocating() *
                                    resource.getCostItems().getRelocation().getTimeWorkerForRelocating())) < 0d) {
                resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTrailerWithMount(null);
                outputErrorsList.add(new OutputErrors("negative value", "IndexRelocationMachinesSubTrailerWithMount"));
            } else
                resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTrailerWithMount(rounding(((resource.getCostItems().getRelocation().getEstimatedPriceOnTractor() +
                        resource.getCostItems().getRelocation().getEstimatedPriceMachineSupport() +
                        resource.getCostItems().getRelocation().getEstimatedPriceOnTrailer()) *
                        resource.getCostItems().getRelocation().getIndexMiddleYearTimeOfWorkOfTheMachineOnTrailer() +
                        sumPriceOnCrane *
                                sumTimeExploitationCrane +
                        (calculateIndexSalary() *
                                resource.getCostItems().getRelocation().getTimeOfTheWorkingWorker() +
                                calculateIndexSalaryWorkerForRelocating() *
                                        resource.getCostItems().getRelocation().getTimeWorkerForRelocating())) /
                        (calculateYearlyModeWork() / resource.getCostItems().getRelocation().getAmountReplacement())));
            nonNan(resource.getCostItems().getRelocation().getIndexRelocationMachinesSubTrailerWithMount());
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTrailerWithMount(rounding(
                    resource.getCostItems().getRelocation().getIndexRelocationMachinesSubTrailerWithMount()));
        } catch (NullPointerException e) {
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTrailerWithMount(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexRelocationMachinesSubTrailerWithMount"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubTrailerWithMount(null);
            outputErrorsList.add(new OutputErrors("division by zero", "IndexRelocationMachinesSubTrailerWithMount"));
        }

        try {
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubShortFormula(rounding((resource.getCostItems().getAmortization().getIndexAmortization() +
                    resource.getCostItems().getRepair().getIndexService() +
                    resource.getCostItems().getWearable().getIndexWearableParts() +
                    calculateIndexSalary() +
                    resource.getCostItems().getPower().getIndexPetrol() +
                    resource.getCostItems().getLubricants().getIndexLubricants() +
                    resource.getCostItems().getLiquid().getIndexHydraulicLiquid()) *
                    resource.getCostItems().getRelocation().getCoefOnRelocating()));
            nonNan(resource.getCostItems().getRelocation().getIndexRelocationMachinesSubShortFormula());
        } catch (NullPointerException e) {
            resource.getCostItems().getRelocation().setIndexRelocationMachinesSubShortFormula(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexRelocationMachinesSubShortFormula"));
        }
        try {
            if (resource.getFields().getRelocation().equals("Своим ходом")) {
                Boolean petrolBool = false;
                Boolean deaselBool = false;
                for (String pow : resource.getFields().getPowers()) {
                    if (pow.equals("Бензин")) petrolBool = true;
                    if (pow.equals("Дизельное топливо")) deaselBool = true;
                }
                if (petrolBool)
                    resource.getCostItems().getRelocation().setIndexRelocationMachines(rounding(
                            manualInputValidation(resource.getCostItems().getRelocation().getIndexRelocationMachinesSubPetrolSomeself(), resource.getCostItems().getRelocation().getIndexRelocationMachinesSubPetrolSomeselfH())));

                if (deaselBool)
                    resource.getCostItems().getRelocation().setIndexRelocationMachines(rounding(
                            manualInputValidation(resource.getCostItems().getRelocation().getIndexRelocationMachinesSubDiesel(), resource.getCostItems().getRelocation().getIndexRelocationMachinesSubDieselH())));

                if (petrolBool & deaselBool)
                    resource.getCostItems().getRelocation().setIndexRelocationMachines(tempCostOnEnegyItsOwnWhenChosePetrolAndDiesel);
            }
            if (resource.getFields().getRelocation().equals("На буксире"))
                resource.getCostItems().getRelocation().setIndexRelocationMachines(rounding(
                        manualInputValidation(resource.getCostItems().getRelocation().getIndexRelocationMachinesSubTow(), resource.getCostItems().getRelocation().getIndexRelocationMachinesSubTowH())));
            if (resource.getFields().getRelocation().equals("На прицепе без предварительного монтажа"))
                resource.getCostItems().getRelocation().setIndexRelocationMachines(rounding(
                        manualInputValidation(resource.getCostItems().getRelocation().getIndexRelocationMachinesSubTrailerWithoutMount(), resource.getCostItems().getRelocation().getIndexRelocationMachinesSubTrailerWithoutMountH())));
            if (resource.getFields().getRelocation().equals("На прицепе с монтажом"))
                resource.getCostItems().getRelocation().setIndexRelocationMachines(rounding(
                        manualInputValidation(resource.getCostItems().getRelocation().getIndexRelocationMachinesSubTrailerWithMount(), resource.getCostItems().getRelocation().getIndexRelocationMachinesSubTrailerWithMountH())));
            if (resource.getFields().getRelocation().equals("Сокращенная формула"))
                resource.getCostItems().getRelocation().setIndexRelocationMachines(rounding(
                        manualInputValidation(resource.getCostItems().getRelocation().getIndexRelocationMachinesSubShortFormula(), resource.getCostItems().getRelocation().getIndexRelocationMachinesSubShortFormulaH())));
        } catch (NullPointerException e) {
            resource.getCostItems().getRelocation().setIndexRelocationMachines(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexRelocationMachines"));
        }


        return manualInputValidation(resource.getCostItems().getRelocation().getIndexRelocationMachines(), resource.getCostItems().getRelocation().getIndexRelocationMachinesH());
    }

    /**
     * calculate Costs On Energy Its Own
     *
     * @return - result calculate
     */
    public Double calculateIndexSalaryWorkerForRelocating() {
        try {
            Double sum = 0d;
            List<RelocationDriversSalary> mainList = resource.getCostItems().getRelocation().getRelocationDriversSalaries();
            for (RelocationDriversSalary m : mainList) {
                List<CategoryDrivers> categoryDriversList = categoryDriversService.getList();
                Double indexCategory = null;
                for (CategoryDrivers categoryDrivers : categoryDriversList)
                    if (categoryDrivers.getId().equals(m.getIdCategoryDrivers()))
                        indexCategory = categoryDrivers.getIndexCategory();
                sum = sum + m.getAmount() * indexCategory;
            }
            sum = sum * periodFields.getIndexSalary();
            resource.getCostItems().getRelocation().setIndexSalaryWorkerForRelocating(rounding(sum));
        } catch (NullPointerException e) {
            resource.getCostItems().getRelocation().setIndexSalaryWorkerForRelocating(null);
            outputErrorsList.add(new OutputErrors("not enough data", "setIndexSalaryWorkerForRelocating"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getRelocation().setIndexSalaryWorkerForRelocating(null);
            outputErrorsList.add(new OutputErrors("division by zero", "setIndexSalaryWorkerForRelocating"));
        }
        return manualInputValidation(resource.getCostItems().getRelocation().getIndexSalaryWorkerForRelocating(), resource.getCostItems().getRelocation().getIndexSalaryWorkerForRelocatingH());
    }

    /**
     * calculate Costs On Energy Its Own
     *
     * @return - result calculate
     */
    public Double calculateCostsOnEnergyItsOwn() {
        try {
            if (resource.getCostItems().getPower().getNormaConsumptionPetrolExploitation() *
                    periodFields.getDensityPetrol() * resource.getCostItems().getAmortization().getMiddleYearMileage() *
                    periodFields.getEstimatedPricePetrol() < 0d) {
                resource.getCostItems().getRelocation().setCostsOnEnergyItsOwnSubPetrol(null);
                outputErrorsList.add(new OutputErrors("negative value", "CostsOnEnergyItsOwnSubPetrol"));
            } else
                resource.getCostItems().getRelocation().setCostsOnEnergyItsOwnSubPetrol(rounding(
                        resource.getCostItems().getPower().getNormaConsumptionPetrolExploitation() *
                                periodFields.getDensityPetrol() * resource.getCostItems().getAmortization().getMiddleYearMileage() *
                                periodFields.getEstimatedPricePetrol() /
                                calculateYearlyModeWork()
                ));

            nonNan(resource.getCostItems().getRelocation().getCostsOnEnergyItsOwnSubPetrol());

        } catch (NullPointerException e) {
            resource.getCostItems().getRelocation().setCostsOnEnergyItsOwnSubPetrol(null);
            outputErrorsList.add(new OutputErrors("not enough data", "CostsOnEnergyItsOwnSubPetrol"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getRelocation().setCostsOnEnergyItsOwnSubPetrol(null);
            outputErrorsList.add(new OutputErrors("division by zero", "CostsOnEnergyItsOwnSubPetrol"));
        }

        try {
            if (resource.getCostItems().getPower().getLinageNormConsumptionExpluatation() *
                    periodFields.getDensityFuel() * resource.getCostItems().getAmortization().getMiddleYearMileage() *
                    periodFields.getEstimatedPriceFuel() < 0d) {
                resource.getCostItems().getRelocation().setCostsOnEnergyItsOwnSubDiesel(null);
                outputErrorsList.add(new OutputErrors("negative value", "CostsOnEnergyItsOwnSubDiesel"));
            } else
                resource.getCostItems().getRelocation().setCostsOnEnergyItsOwnSubDiesel(rounding(
                        resource.getCostItems().getPower().getLinageNormConsumptionExpluatation() *
                                periodFields.getDensityFuel() * resource.getCostItems().getAmortization().getMiddleYearMileage() *
                                periodFields.getEstimatedPriceFuel() /
                                calculateYearlyModeWork()
                ));

            nonNan(resource.getCostItems().getRelocation().getCostsOnEnergyItsOwnSubDiesel());

        } catch (NullPointerException e) {
            resource.getCostItems().getRelocation().setCostsOnEnergyItsOwnSubDiesel(null);
            outputErrorsList.add(new OutputErrors("not enough data", "CostsOnEnergyItsOwnSubDiesel"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getRelocation().setCostsOnEnergyItsOwnSubDiesel(null);
            outputErrorsList.add(new OutputErrors("division by zero", "CostsOnEnergyItsOwnSubDiesel"));
        }


        try {
            boolean petrolBool = false;
            boolean deaselBool = false;

            if (resource.getFields().getPowers() != null)
                for (String pow : resource.getFields().getPowers()) {
                    if (pow.equals("Бензин")) petrolBool = true;
                    if (pow.equals("Дизельное топливо")) deaselBool = true;
                }
            Double sum;
            resource.getCostItems().getRelocation().setCostsOnEnergyItsOwn(0d);
            if (petrolBool)
                resource.getCostItems().getRelocation().setCostsOnEnergyItsOwn(rounding(manualInputValidation(resource.getCostItems().getRelocation().getCostsOnEnergyItsOwnSubPetrol(), resource.getCostItems().getRelocation().getCostsOnEnergyItsOwnSubPetrolH())));
            sum = rounding(resource.getCostItems().getRelocation().getCostsOnEnergyItsOwn());
            if (deaselBool)
                resource.getCostItems().getRelocation().setCostsOnEnergyItsOwn(rounding(sum + manualInputValidation(resource.getCostItems().getRelocation().getCostsOnEnergyItsOwnSubDiesel(), resource.getCostItems().getRelocation().getCostsOnEnergyItsOwnSubDieselH())));

        } catch (NullPointerException e) {
            resource.getCostItems().getRelocation().setCostsOnEnergyItsOwn(null);
            outputErrorsList.add(new OutputErrors("not enough data", "CostsOnEnergyItsOwn"));
        }
        return manualInputValidation(resource.getCostItems().getRelocation().getCostsOnEnergyItsOwn(), resource.getCostItems().getRelocation().getCostsOnEnergyItsOwnH());
    }

    /**
     * calculate Index Middle Year Time Of Work Of The Machine
     *
     * @return - result calculate
     */
    public Double calculateIndexMiddleYearTimeOfWorkOfTheMachine() {
        try {
            resource.getCostItems().getRelocation().setIndexMiddleYearTimeOfWorkOfTheMachineSubRelocationMoreOneDay(rounding(periodFields.getDurationOfWorkingChange() * resource.getCostItems().getAmortization().getCoefChangeWorkingMachine(), 3));
            nonNan(resource.getCostItems().getRelocation().getIndexMiddleYearTimeOfWorkOfTheMachineSubRelocationMoreOneDay());

        } catch (NullPointerException e) {
            resource.getCostItems().getRelocation().setIndexMiddleYearTimeOfWorkOfTheMachineSubRelocationMoreOneDay(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexMiddleYearTimeOfWorkOfTheMachineSubRelocationMoreOneDay"));
        }

        if (resource.getCostItems().getRelocation().getIndexMiddleYearTimeOfWorkOfTheMachineF())
            resource.getCostItems().getRelocation().setIndexMiddleYearTimeOfWorkOfTheMachine(rounding(manualInputValidation(resource.getCostItems().getRelocation().getIndexMiddleYearTimeOfWorkOfTheMachineSubRelocationMoreOneDay(), resource.getCostItems().getRelocation().getIndexMiddleYearTimeOfWorkOfTheMachineSubRelocationMoreOneDayH()), 3));
        else
            resource.getCostItems().getRelocation().setIndexMiddleYearTimeOfWorkOfTheMachine(rounding(manualInputValidation(resource.getCostItems().getRelocation().getIndexMiddleYearTimeOfWorkOfTheMachineSubRelocationOneDay(), resource.getCostItems().getRelocation().getIndexMiddleYearTimeOfWorkOfTheMachineSubRelocationOneDayH()), 3));

        return manualInputValidation(resource.getCostItems().getRelocation().getIndexMiddleYearTimeOfWorkOfTheMachine(), resource.getCostItems().getRelocation().getIndexMiddleYearTimeOfWorkOfTheMachineH());
    }

    /**
     * calculate Index  Year Time Of Work Of The Machine on_tow
     *
     * @return - result calculate
     */
    public Double calculateIndexYearTimeOfWorkOfTheMachineOnTow() {
        try {
            if (calculateYearlyModeWork() < 0d)
                outputErrorsList.add(new OutputErrors("negative value", "IndexYearTimeOfWorkOfTheMachineOnTow"));
            else
                resource.getCostItems().getRelocation().setIndexYearTimeOfWorkOfTheMachineOnTow(rounding(calculateYearlyModeWork() / resource.getCostItems().getRelocation().getAmountReplacement(), 3));
            nonNan(resource.getCostItems().getRelocation().getIndexYearTimeOfWorkOfTheMachineOnTow());
        } catch (NullPointerException e) {
            resource.getCostItems().getRelocation().setIndexYearTimeOfWorkOfTheMachineOnTow(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexYearTimeOfWorkOfTheMachineOnTow"));
        } catch (ArithmeticException e) {
            resource.getCostItems().getRelocation().setCostsOnEnergyItsOwnSubPetrol(null);
            outputErrorsList.add(new OutputErrors("division by zero", "IndexYearTimeOfWorkOfTheMachineOnTow"));
        }


        resource.getCostItems().getRelocation().setIndexYearTimeOfWorkOfTheMachineOnTow(rounding(resource.getCostItems().getRelocation().getIndexYearTimeOfWorkOfTheMachineOnTow(), 3));

        return manualInputValidation(resource.getCostItems().getRelocation().getIndexYearTimeOfWorkOfTheMachineOnTow(), resource.getCostItems().getRelocation().getIndexYearTimeOfWorkOfTheMachineOnTow());
    }

    /**
     * calculate IcalculateIndexSalary
     *
     * @return - result calculate
     */

    public Double calculateIndexSalary() {
        try {
            Double sum = 0d;
            try {
                if (resource.getCostItems().getSalary().getMains() != null)
                    for (Main m : resource.getCostItems().getSalary().getMains()) {
                        sum += rounding(m.getAmount() * categoryDriversService.get(m.getIdCategoryDrivers()).getIndexCategory());
                    }
            } catch (NullPointerException e) {
            }
            resource.getCostItems().getSalary().setIndexSalaryMain(rounding(sum * periodFields.getIndexSalary()));
            sum = 0d;
            if (resource.getCostItems().getSalary().getExtras() != null)
                try {
                    for (Extra m : resource.getCostItems().getSalary().getExtras()) {
                        sum += rounding(m.getAmount() * categoryDriversService.get(m.getIdCategoryDrivers()).getIndexCategory());
                    }
                } catch (NullPointerException e) {
                }
            resource.getCostItems().getSalary().setIndexSalaryExtra(rounding(sum * periodFields.getIndexSalary()));

            resource.getCostItems().getSalary().setIndexSalary(rounding(
                    manualInputValidation(resource.getCostItems().getSalary().getIndexSalaryExtra(), resource.getCostItems().getSalary().getIndexSalaryExtraH()) +
                            manualInputValidation(resource.getCostItems().getSalary().getIndexSalaryMain(), resource.getCostItems().getSalary().getIndexSalaryMainH())));
            nonNan(resource.getCostItems().getSalary().getIndexSalary());
        } catch (NullPointerException e) {
            resource.getCostItems().getSalary().setIndexSalary(null);
            outputErrorsList.add(new OutputErrors("not enough data", "IndexMiddleYearTimeOfWorkOfTheMachineSubRelocationMoreOneDay"));
        }
        return manualInputValidation(resource.getCostItems().getSalary().getIndexSalary(), resource.getCostItems().getSalary().getIndexSalaryH());
    }

}
