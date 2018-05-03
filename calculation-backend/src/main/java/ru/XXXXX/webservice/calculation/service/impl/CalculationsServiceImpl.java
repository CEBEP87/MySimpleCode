package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.impl;

import org.springframework.stereotype.Service;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.CalculationsService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by samsonov_ky on 26.09.2017.
 */
@Service
public class CalculationsServiceImpl implements CalculationsService {

    @Override
    public Double rounding(Double val) {
        nonNan(val);
        return new BigDecimal(val).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public Double rounding(Double val, int scale) {
        nonNan(val);
        return new BigDecimal(val).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Nan situation to Null
     *
     * @param val base value
     */
    public void nonNan(Double val) {
        if (Double.isNaN(val)) throw new NullPointerException("can't be isNan");
        if (val.isInfinite()) throw new NullPointerException("can't be Infinity");
    }

    /**
     * calculation deviation
     *
     * @param baseValue    base value
     * @param currentValue current value
     *                     * @return - result calculate
     */
    public static Double calculateDeviation(Double baseValue, Double currentValue) {
        Double currentDelta = null;
        if (baseValue != null && baseValue != 0 && currentValue != null)
            currentDelta = new BigDecimal((((currentValue - baseValue) / baseValue) * 100)).setScale(0, RoundingMode.HALF_UP).doubleValue();

        return nanToZero(currentDelta);

    }

    /**
     * Calculate manual Input Double Type
     * * @param auto auto value
     *
     * @param hand manual value
     *             * @return - result calculate
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
     *             * @return - result calculate
     */
    public static Long manualInputValidation(Long auto, Long hand) {
        if (hand == null) if (auto != null) return auto;
        if (hand != null) return hand;
        return null;
    }

    @Override
    public Double calculateEstimatedPriceForMaterial(Double rate, Double sellingPrice, Double transportCost, Double weightBrutto, Boolean trBool) {
        return calculateEstimatedPriceForEquipment(rate, sellingPrice, transportCost, weightBrutto, trBool);
    }

    @Override
    public Double calculateEstimatedPriceForEquipment(Double rate, Double sellingPrice, Double transportCost, Double weightBrutto, Boolean trBool) {
        if (rate == null) rate = 0d;
        if (sellingPrice == null) sellingPrice = 0d;
        if (transportCost == null) transportCost = 0d;
        if (weightBrutto == null) weightBrutto = 0d;
        if (trBool == null) trBool = false;
        Double tr = 0d;
        if (trBool) {
            tr = rounding(transportCost) * (weightBrutto / 100);
        }
        Double zsr = (sellingPrice + tr) * (rounding(rate) / 100);
        return nanToZero(sellingPrice + rounding(tr) + rounding(zsr));
    }

    @Override
    public Double calculateRateMaterial(Double estimatedCurrentPrice, Double costResourceBase) {
        return calculateRateEquipment(estimatedCurrentPrice, costResourceBase);
    }


    @Override
    public Double calculateRateEquipment(Double estimatedCurrentPrice, Double costResourceBase) {
        if (estimatedCurrentPrice == null) estimatedCurrentPrice = 0d;
        if (costResourceBase == null) return 0d;
        return nanToZero(estimatedCurrentPrice / costResourceBase);
    }

    @Override
    public Double calculateRateMaterialProcess(List<Materials> listMaterial, List<Material> listCollectionMaterial
            , Double costResourceBase, Double indexOtherResource) {
        if (costResourceBase == null) costResourceBase = 0d;
        if (indexOtherResource == null) indexOtherResource = 0d;
        Double sum1 = 0d;
        Double sum2 = 0d;
        try {
            Material material = null;
            for (Materials mat : listMaterial) {
                // restore when will bugfix statuses in sql base
                //      material=listCollectionMaterial.stream().filter(s -> s.getPressmark().equals(mat.getId())&&(s.getStatus().equals("действующий"))).findFirst().get();
                material = listCollectionMaterial.stream().filter(s -> s.getPressmark().equals(mat.getId())).findFirst().get();
                Double costBase = material.getCostBase();
                sum1 = sum1 + rounding(costBase * mat.getRate());
                Double rateMaterial = rounding(material.getRate());
                sum2 = sum2 + rounding(rateMaterial * costBase * mat.getRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Double currentCostOtherMaterials = (costResourceBase - sum1) * indexOtherResource;
        return nanToZero((sum2 + currentCostOtherMaterials) / costResourceBase);
    }

    @Override
    public Double calculateRateMachineProcess(List<Machines> listMachines, List<Machine> listCollectionMachines, Double indexOtherMachine, Double operationOfMachinesBase,
                                              Double indexSalary, Double salaryDriversBase) {
        if (indexOtherMachine == null) indexOtherMachine = 0d;
        if (operationOfMachinesBase == null) operationOfMachinesBase = 0d;
        if (indexSalary == null) indexSalary = 0d;
        if (salaryDriversBase == null) salaryDriversBase = 0d;

        Double sum1 = 0d;
        Double sum2 = 0d;
        Double sum3 = 0d;
        Machine machine = null;
        try {
            for (Machines mat : listMachines) {
                Double salaryDriversBaseOfMachineCollection = 0d;
                machine = listCollectionMachines.stream().filter(s -> s.getPressmark().equals(mat.getId()) && (s.getStatus().equals("Действующий"))).findFirst().get();
                salaryDriversBaseOfMachineCollection = machine.getSalaryDriversBase();
                sum1 = sum1 + rounding(salaryDriversBaseOfMachineCollection * mat.getRate());
                Double costBase = machine.getCostBase();
                sum2 = sum2 + rounding(costBase - salaryDriversBaseOfMachineCollection) * mat.getRate();
                Double rate = rounding(machine.getRate());
                sum3 = sum3 + rounding(rate * costBase * mat.getRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        return nanToZero((sum3 + currentCostOtherMachines) / operationOfMachinesBase);
    }

    @Override
    public Transport calculateRateMachineForTransport(Transport tr, Period period, OverheadProfitCurrent overheadProfitCurrentList, ParametrsOfTransport parametrsOfTransport, Machine machine) {
        tr.setRatePrevious(tr.getRate());
        try {
            Double timeW = parametrsOfTransport.getTimeWorkDay() * 60;
            XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXtr.setRate(operationMachineCurent / tr.getCostBase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tr;

    }

    @Override
    public Double calculateRateSalaryForLargeProcess(Double salaryBase, Double rateSalaryPrevious, Double IndexSalaryOfPeriod) {
        if ((salaryBase != null) && (rateSalaryPrevious != null) && (IndexSalaryOfPeriod != null))
            return (nanToZero(rounding(rounding(salaryBase * rateSalaryPrevious) * IndexSalaryOfPeriod) / salaryBase));
        return 0d;
    }

    @Override
    public Double calculateRateMachineForLargeProcess(Double operationOfMachineBase, Double rateMachinePrevious, Double IndexMachineOfPeriod) {
        if ((operationOfMachineBase != null) && (rateMachinePrevious != null) && (IndexMachineOfPeriod != null))
            return (nanToZero(rounding(rounding(operationOfMachineBase * rateMachinePrevious) * IndexMachineOfPeriod) / operationOfMachineBase));
        return 0d;
    }

    @Override
    public Double calculateRateMaterialForLargeProcess(Double costResourceBase, Double rateMaterialPrevious, Double IndexMaterialOfPeriod) {
        if ((costResourceBase != null) && (rateMaterialPrevious != null) && (IndexMaterialOfPeriod != null))
            return (nanToZero(rounding(rounding(costResourceBase * rateMaterialPrevious) * IndexMaterialOfPeriod) / costResourceBase));
        return 0d;
    }

    @Override
    public Double calculateRateOtherWorkForLargeProcess(Double costOtherWorkBase, Double rateMaterialPrevious, Double IndexOtherWork) {
        if ((costOtherWorkBase != null) && (rateMaterialPrevious != null) && (IndexOtherWork != null))
            return (nanToZero(rounding(rounding(costOtherWorkBase * rateMaterialPrevious) * IndexOtherWork) / costOtherWorkBase));
        return 0d;
    }

    @Override
    public Double calculateRateCommonForLargeProcess(Double rateMaterial, Double costResourceBase,
                                                     Double rateMachine, Double operationOfMachineBase,
                                                     Double rateSalary, Double salaryBase, Double costOtherWorkBase,
                                                     Double rateOtherWork) {
        if ((rateMaterial != null) && (costResourceBase != null) && (rateMachine != null) && (operationOfMachineBase != null)
                && (rateSalary != null) && (salaryBase != null) && (costOtherWorkBase != null) && (rateOtherWork != null))
            return nanToZero((rounding(rateMaterial * costResourceBase) + rounding(rateMachine * operationOfMachineBase) + rounding(rateSalary * salaryBase) + rounding(rateOtherWork * costOtherWorkBase)) / (costResourceBase + operationOfMachineBase + salaryBase + costOtherWorkBase));
        return 0d;
    }

    /**
     * Nan situation to Null
     *
     * @param val base value
     * @return zero or val
     */
    public static Double nanToZero(Double val) {
        if (Double.isNaN(val)) return 0d;
        if (val.isInfinite()) return 0d;
        return val;

    }
}
