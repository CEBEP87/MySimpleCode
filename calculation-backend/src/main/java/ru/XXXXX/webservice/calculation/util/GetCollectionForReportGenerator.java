package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.*;

import java.util.ArrayList;
import java.util.List;

import static ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.util.utilsForReportGenerator.*;

/**
 * Get Collection For Report Generator
 *
 * @author Samsonov_KY
 * @since 26.02.2018
 */
@Component
public class GetCollectionForReportGenerator {

    /**
     * PeriodService
     */
    @Autowired
    private PeriodService periodService;
    /**
     * overheadProfitService
     */
    @Autowired
    private OverheadProfitService overheadProfitService;

    /**
     * unitOfMeasureService
     */
    @Autowired
    private UnitOfMeasureService unitOfMeasureService;
    /**
     * installationService
     */
    @Autowired
    private InstallationService installationService;
    /**
     * winterRiseListService
     */
    @Autowired
    private WinterRiseListService winterRiseListService;
    /**
     * commissioningService
     */
    @Autowired
    private CommissioningService commissioningService;
    /**
     * repairService
     */
    @Autowired
    private RepairService repairService;
    /**
     * restorationService
     */
    @Autowired
    private RestorationService restorationService;
    /**
     * overheadService
     */
    @Autowired
    private OverheadService overheadService;
    /**
     * winterRiseService
     */
    @Autowired
    private WinterRiseService winterRiseService;
    /**
     * temporaryBuildingService
     */
    @Autowired
    private TemporaryBuildingService temporaryBuildingService;
    /**
     * serviceService
     */
    @Autowired
    private ServiceService serviceService;
    /**
     * transportService
     */
    @Autowired
    private TransportService transportService;
    /**
     * largeProcessService
     */
    @Autowired
    private LargeProcessService largeProcessService;
    /**
     * celebrateProcessService
     */
    @Autowired
    private CelebrateProcessService celebrateProcessService;

    /**
     * calculationsService
     */
    @Autowired
    private CalculationsService calculationsService;
    /**
     * materialService
     */
    @Autowired
    private MaterialService materialService;
    /**
     * machineService
     */
    @Autowired
    private MachineService machineService;
    /**
     * constructionService
     */
    @Autowired
    private ConstructionService constructionService;

    /**
     * Transport Cost Service
     */
    @Autowired
    private TransportCostService transportCostService;

    /**
     * create Data for print report
     *
     * @param collectionName collection Name
     * @param period         report period
     * @param chapterProcess Chapter name
     * @param base           base or not
     * @return output data
     **/

    public List<List<String>> getCollection(String collectionName, Period period, Group chapterProcess, boolean base) {
        List<List<String>> resultCollection = new ArrayList<>();
        String idPeriod = period.getId();

        if (collectionName.equals("Material by reference")) {
            List<Material> materialList = getSortedMaterialListByIdPeriod(period.getId(), materialService.findOneByIdPeriod(idPeriod));
            for (Material s : materialList) {
                List<String> list = new ArrayList<>();
                if (s.getPressmark() != null) list.add(s.getPressmark().toString());
                else list.add("-");
                if (s.getAction().equals("на удаление") && s.getStatus().equals("утвержден")) {
                    if (s.getTitle() != null) list.add(s.getTitle() + " (удален)");
                    else list.add("-");
                    list.add("-");
                    list.add("-");
                    continue;
                }
                if (s.getTitle() != null) list.add(s.getTitle());
                else list.add("-");
                if (s.getUnitOfMeasure() != null) list.add(s.getUnitOfMeasure());
                else list.add("-");
                if (s.getRate() != null && s.getCostBase() != null) {
                    if (list.get(0).equals("1.1-1-1920"))
                        list.add(calculationsService.rounding(s.getRate() * s.getCostBase(), 2).toString());
                    else {
                        String resultDouble = calculationsService.rounding(s.getRate() * s.getCostBase(), 0).toString();
                        String substring = resultDouble.substring(0, resultDouble.length() - 1);
                        list.add(substring.substring(0, substring.length() - 1));
                    }
                } else list.add("-");
                resultCollection.add(list);
            }
            return resultCollection;
        }

        if (collectionName.equals("Material")) {
            List<TransportCost> transportCosts = getSortedTransportCostListByIdPeriod(period.getId(), transportCostService.findOneByIdPeriod(idPeriod));
            transportCosts.forEach(s -> {
                List<String> list = new ArrayList<>();
                if (s.getId() != null) list.add(s.getIdGroup().toString());
                else list.add("-");
                if (s.getPressmark() != null) list.add(s.getPressmark().toString());
                else list.add("-");
                if (s.getRate() != null) list.add(calculationsService.rounding(s.getRate()).toString());
                else list.add("-");
                resultCollection.add(list);
            });

            List<Material> materialList = getSortedMaterialListByIdPeriod(period.getId(), materialService.findOneByIdPeriod(idPeriod));
            materialList.forEach(s -> {
                List<String> list = new ArrayList<>();
                if (s.getId() != null) list.add(s.getIdGroup().toString());
                else list.add("-");
                if (s.getPressmark() != null) list.add(s.getPressmark().toString());
                else list.add("-");
                if (s.getRate() != null) list.add(calculationsService.rounding(s.getRate()).toString());
                else list.add("-");
                resultCollection.add(list);
            });
            return resultCollection;
        }

        if (collectionName.equals("MaterialXml")) {
            List<Material> materialList = getSortedMaterialListByIdPeriod(period.getId(), materialService.findOneByIdPeriod(idPeriod));
            materialList.forEach(s -> {
                List<String> list = new ArrayList<>();
                if (s.getId() != null) list.add(s.getIdGroup().toString());
                else list.add("-");
                if (s.getPressmark() != null) list.add(s.getPressmark().toString());
                else list.add("-");
                list.add("-");//setRATETRANSPORT
                list.add(filterDoubleValue(s.getRate()));//setRATEMATERIAL
                list.add("-");//setRATEMACHINE
                list.add("-");//setOVERHEAD
                list.add("-");//setEXTRAOVERHEAD
                list.add("-");//setPROFIT
                list.add("-");//setEXTRAPROFIT
                list.add("-");//setWINTERRISEPRICE
                list.add("-");//setEXTRAWINTERRISEPRICE
                list.add("-");//setWINTERRISEPRICEMATa
                list.add("-");//setEXTRAWINTERRISEPRICEMAT
                list.add("-");//setRATETOTAL
                list.add("-");//setRATESALARY
                list.add("-");//setRATEEXTRA
                list.add("-");//setBASEOVERHEAD
                list.add("-");//setBASEEXTRAOVERHEAD
                list.add("-");//setBASEPROFIT
                list.add("-");//setBASEEXTRAPROFIT
                list.add("-");//setBASEWINTERRISEPRICE
                list.add("-");//setEXTRAWINTERRISEPRICE
                list.add("-");//setBASEWINTERRISEPRICEMAT
                list.add("-");//setEXTRAWINTERRISEPRICEMAT
                resultCollection.add(list);
            });
            return resultCollection;
        }


        if (collectionName.equals("LargeProcessXml")) {
            List<LargeProcess> materialList = getSortedLargeProcessListByIdPeriod(period.getId(), largeProcessService.findOneByIdPeriod(idPeriod));
            materialList.forEach(s -> {
                List<String> list = new ArrayList<>();
                if (s.getId() != null) list.add(s.getIdGroup().toString());
                else list.add("-");
                if (s.getPressmark() != null) list.add(s.getPressmark().toString());
                else list.add("-");
                list.add("-");//setRATETRANSPORT
                list.add(filterDoubleValue(s.getRateMaterial()));//setRATEMATERIAL
                list.add(filterDoubleValue(s.getRateMachine()));//setRATEMACHINE
                list.add("-");//setOVERHEAD
                list.add("-");//setEXTRAOVERHEAD
                list.add("-");//setPROFIT
                list.add("-");//setEXTRAPROFIT
                list.add("-");//setWINTERRISEPRICE
                list.add("-");//setEXTRAWINTERRISEPRICE
                list.add("-");//setWINTERRISEPRICEMATa
                list.add("-");//setEXTRAWINTERRISEPRICEMAT
                list.add(filterDoubleValue(s.getRateCommon()));//setRATETOTAL
                list.add(filterDoubleValue(period.getIndexSalary()));//setRATESALARY
                list.add(filterDoubleValue(s.getRateOtherWork()));//setRATEEXTRA
                list.add("-");//setBASEOVERHEAD
                list.add("-");//setBASEEXTRAOVERHEAD
                list.add("-");//setBASEPROFIT
                list.add("-");//setBASEEXTRAPROFIT
                list.add("-");//setBASEWINTERRISEPRICE
                list.add("-");//setEXTRAWINTERRISEPRICE
                list.add("-");//setBASEWINTERRISEPRICEMAT
                list.add("-");//setEXTRAWINTERRISEPRICEMAT
                resultCollection.add(list);
            });
            return resultCollection;
        }
       if (collectionName.equals("OverheadXml")) {
            List<Overhead> materialList = getSortedOverheadListByIdPeriod(period.getId(), overheadService.findOneByIdPeriod(idPeriod));
            List<OverheadProfitCurrent> overheadProfitCurrentList = overheadProfitService.findAllByIdPeriod(idPeriod);
            List<WinterRiseListCurrent> winterRiseListCurrentList = winterRiseListService.getAllWinterRiseListCurrentByPeriod(idPeriod);
            List<OverheadProfitBase> overheadProfitBases = overheadProfitService.findAllBase();
            List<WinterRiseListBase> winterRiseListBases = winterRiseListService.getAllWinterRiseListBase();

            materialList.forEach(s -> {
                List<String> list = new ArrayList<>();
                if (s.getId() != null) list.add(s.getIdGroup().toString());
                else list.add("-");
                if (s.getPressmark() != null) list.add(s.getPressmark().toString());
                else list.add("-");
                list.add("-");//setRATETRANSPORT
                list.add(filterDoubleValue(s.getRateMaterial()));//setRATEMATERIAL
                list.add(filterDoubleValue(s.getRateMachine())); //setRATEMACHINE
                OverheadProfitCurrent overheadProfitCurrent = null;
                WinterRiseListCurrent winterRiseListCurrent = null;
                OverheadProfitBase overheadProfitBase = null;
                WinterRiseListBase winterRiseListBase = null;
                try {
                    overheadProfitCurrent = overheadProfitCurrentList.stream().filter(ov -> ov.getIdPressmark().equals(s.getIdPressmarkOverheadProfit())).findFirst().get();
                    list.add(filterDoubleValue(overheadProfitCurrent.getOverhead()));//setOVERHEAD
                    list.add(filterDoubleValue(overheadProfitCurrent.getOverheadExtra() ));//setEXTRAOVERHEAD
                    list.add(filterDoubleValue(overheadProfitCurrent.getProfit() ));//setPROFIT
                    list.add(filterDoubleValue(overheadProfitCurrent.getProfitExtra() ));//setEXTRAPROFIT
                } catch (Exception e) {
                    list.add("-"); list.add("-"); list.add("-"); list.add("-");
                }
                try {
                    winterRiseListCurrent = winterRiseListCurrentList.stream().filter(wi -> wi.getIdPressmark().equals(s.getIdPressmarkWinterRiseList())).findFirst().get();
                    list.add(filterDoubleValue(winterRiseListCurrent.getWinterRise() ));//setWINTERRISEPRICE
                    list.add(filterDoubleValue(winterRiseListCurrent.getWinterRiseExtra() ));//setEXTRAWINTERRISEPRICE
                    list.add(filterDoubleValue(winterRiseListCurrent.getWinterRiseMat() ));//setEXTRAWINTERRISEPRICE
                    list.add(filterDoubleValue(winterRiseListCurrent.getWinterRiseMatExtra() ));//setEXTRAWINTERRISEPRICEMAT
                } catch (Exception e) {
                    list.add("-"); list.add("-"); list.add("-"); list.add("-");
                }

                list.add("1000");//setRATETOTAL
                list.add(filterDoubleValue(period.getIndexSalary() ));//setRATESALARY
                list.add("1000");//setRATEEXTRA
                try {
                    overheadProfitBase = overheadProfitBases.stream().filter(ov -> ov.getIdPressmark().equals(s.getIdPressmarkOverheadProfit())).findFirst().get();
                    list.add(filterDoubleValue(overheadProfitBase.getOverheadBase() ));//setBASEOVERHEAD
                    list.add(filterDoubleValue(overheadProfitBase.getOverheadBaseExtra() ));//setBASEEXTRAOVERHEAD
                    list.add(filterDoubleValue(overheadProfitBase.getProfitBase() ));//setBASEPROFIT
                    list.add(filterDoubleValue(overheadProfitBase.getProfitBaseExtra() ));//setBASEEXTRAPROFIT
                } catch (Exception e) {
                    list.add("-"); list.add("-"); list.add("-"); list.add("-");
                }
                try {
                    winterRiseListBase = winterRiseListBases.stream().filter(wi -> wi.getIdPressmark().equals(s.getIdPressmarkWinterRiseList())).findFirst().get();
                    list.add(filterDoubleValue(winterRiseListBase.getWinterRiseBase() ));//setBASEWINTERRISEPRICE
                    list.add(filterDoubleValue(winterRiseListBase.getWinterRiseBaseExtra() ));//setEXTRAWINTERRISEPRICE
                    list.add(filterDoubleValue(winterRiseListBase.getWinterRiseBaseMat() ));//setBASEWINTERRISEPRICEMAT
                    list.add(filterDoubleValue(winterRiseListBase.getWinterRiseBaseMatExtra() ));//setEXTRAWINTERRISEPRICEMAT
                } catch (Exception e) {
                    list.add("-"); list.add("-"); list.add("-"); list.add("-");
                }
                resultCollection.add(list);
            });
            return resultCollection;
        }


        if (collectionName.equals("Machine")) {
            List<Machine> materialList = getSortedMachineListByIdPeriod(period.getId(), machineService.findOneByIdPeriod(idPeriod));
            materialList.forEach(s -> {
                List<String> list = new ArrayList<>();
                if (s.getId() != null) list.add(s.getIdGroup().toString());
                else list.add("-");
                if (s.getPressmark() != null) list.add(s.getPressmark().toString());
                else list.add("-");
                if (s.getRate() != null) list.add(calculationsService.rounding(s.getRate()).toString());
                else list.add("-");
                resultCollection.add(list);
            });
            return resultCollection;
        }


        if (collectionName.equals("Machine")) {
            List<Machine> materialList = getSortedMachineListByIdPeriod(period.getId(), machineService.findOneByIdPeriod(idPeriod));
            materialList.forEach(s -> {
                List<String> list = new ArrayList<>();
                if (s.getId() != null) list.add(s.getIdGroup().toString());
                else list.add("-");
                if (s.getPressmark() != null) list.add(s.getPressmark().toString());
                else list.add("-");
                if (s.getRate() != null) list.add(calculationsService.rounding(s.getRate()).toString());
                else list.add("-");
                resultCollection.add(list);
            });
            return resultCollection;
        }



        if (collectionName.equals("MachineSection1")) {
            List<Machine> materialList = getSortedMachineListByIdPeriod(idPeriod, machineService.findOneByIdPeriod(idPeriod));
            materialList.forEach(s -> {
                List<String> list = new ArrayList<>();
                if (inGroup(period, s.getIdGroup(), "Раздел 1. Строительные машины, механизмы и инструменты")) {
                    if (s.getId() != null) list.add(s.getIdGroup().toString());
                    else list.add("-");
                    if (s.getPressmark() != null) list.add(s.getPressmark().toString());
                    else list.add("-");
                    if (s.getRate() != null) list.add(calculationsService.rounding(s.getRate()).toString());
                    else list.add("-");
                    resultCollection.add(list);
                }
            });
            return resultCollection;
        }

        if (collectionName.equals("MachineSection2")) {
            List<Machine> materialList = getSortedMachineListByIdPeriod(idPeriod, machineService.findOneByIdPeriod(idPeriod));
            materialList.forEach(s -> {
                List<String> list = new ArrayList<>();
                if (inGroup(period, s.getIdGroup(), "Раздел 2. Перебазировка строительных машин и механизмов")) {
                    if (s.getId() != null) list.add(s.getIdGroup().toString());
                    else list.add("-");
                    if (s.getPressmark() != null) list.add(s.getPressmark().toString());
                    else list.add("-");
                    if (s.getRate() != null) list.add(calculationsService.rounding(s.getRate()).toString());
                    else list.add("-");
                    if (period.getIndexSalary() != null) list.add(period.getIndexSalary().toString());
                    else list.add("-");
                    resultCollection.add(list);
                }
            });
            return resultCollection;
        }

        if (collectionName.equals("WinterRiseNorm")) {
            List<WinterRise> materialList = getSortedWinterRiseListByIdPeriod(idPeriod, winterRiseService.findOneByIdPeriod(idPeriod));
            List<OverheadProfitCurrent> overheadProfitCurrentList = overheadProfitService.findAllByIdPeriod(idPeriod);
            List<WinterRiseListCurrent> winterRiseListCurrentList = winterRiseListService.getAllWinterRiseListCurrentByPeriod(idPeriod);
            List<OverheadProfitBase> overheadProfitBases = null;
            List<WinterRiseListBase> winterRiseListBases = null;
            if (base) overheadProfitBases = overheadProfitService.findAllBase();
            if (base) winterRiseListBases = winterRiseListService.getAllWinterRiseListBase();
            List<OverheadProfitBase> finalOverheadProfitBases = overheadProfitBases;
            List<WinterRiseListBase> finalWinterRiseListBases = winterRiseListBases;
            materialList.forEach(s -> {
                try {
                    OverheadProfitCurrent overheadProfitCurrent = null;
                    WinterRiseListCurrent winterRiseListCurrent = null;
                    OverheadProfitBase overheadProfitBase = null;
                    WinterRiseListBase winterRiseListBase = null;
                    try {
                        if (base)
                            overheadProfitBase = finalOverheadProfitBases.stream().filter(ov -> ov.getIdPressmark().equals(s.getIdPressmarkOverheadProfit())).findFirst().get();
                        else
                            overheadProfitCurrent = overheadProfitCurrentList.stream().filter(ov -> ov.getIdPressmark().equals(s.getIdPressmarkOverheadProfit())).findFirst().get();
                    } catch (Exception e) {
                    }
                    try {
                        if (base)
                            winterRiseListBase = finalWinterRiseListBases.stream().filter(wi -> wi.getIdPressmark().equals(s.getIdPressmarkWinterRiseList())).findFirst().get();
                        else
                            winterRiseListCurrent = winterRiseListCurrentList.stream().filter(wi -> wi.getIdPressmark().equals(s.getIdPressmarkWinterRiseList())).findFirst().get();
                    } catch (Exception e) {
                    }
                    List<String> list = new ArrayList<>();
                    //переназначить родителя сборник
                    if (s.getId() != null) list.add(refactorGroup(chapterProcess, period, s.getIdGroup()));
                    else list.add("-");
                    if (s.getPressmark() != null) list.add(s.getPressmark().toString());
                    else list.add("-");
                    if (base) list = getBaseData(overheadProfitBase, winterRiseListBase, list);
                    else
                        list = getCurrentData(overheadProfitCurrent, winterRiseListCurrent, list);
                    resultCollection.add(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return resultCollection;
        }


        if (collectionName.equals("Service")) {
            List<ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Service> materialList = getSortedServiceListByIdPeriod(idPeriod, serviceService.findOneByIdPeriod(idPeriod));
            materialList.forEach(s -> {
                try {
                    List<String> list = new ArrayList<>();
                    //переназначить родителя сборник
                    if (s.getId() != null) list.add(refactorGroup(chapterProcess, period, s.getIdGroup()));
                    else list.add("-");
                    if (s.getPressmark() != null) list.add(s.getPressmark().toString());
                    else list.add("-");
                    if (s.getRateMachine() != null)
                        list.add(calculationsService.rounding(s.getRateMachine()).toString());
                    else list.add("-");
                    if (s.getRateMaterial() != null)
                        list.add(calculationsService.rounding(s.getRateMaterial()).toString());
                    else list.add("-");
                    resultCollection.add(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return resultCollection;
        }

    /**
     * get Current Data for Norm collections
     *
     * @param list                  exist list data
     * @param winterRiseListCurrent winterRiseListCurrent collection
     * @param overheadProfitCurrent overheadProfitCurrent collecion
     * @return output data
     **/
    private List<String> getCurrentData(OverheadProfitCurrent overheadProfitCurrent, WinterRiseListCurrent winterRiseListCurrent, List<String> list) {
        if (overheadProfitCurrent != null && overheadProfitCurrent.getOverhead() != null)
            list.add(cutValues(calculationsService.rounding(overheadProfitCurrent.getOverhead()).toString()));
        else list.add("-");
        if (overheadProfitCurrent != null && overheadProfitCurrent.getProfit() != null)
            list.add(cutValues(calculationsService.rounding(overheadProfitCurrent.getProfit()).toString()));
        else list.add("-");
        if (winterRiseListCurrent != null && winterRiseListCurrent.getWinterRise() != null)
            list.add(calculationsService.rounding(winterRiseListCurrent.getWinterRise()).toString());
        else list.add("-");
        if (winterRiseListCurrent != null && winterRiseListCurrent.getWinterRiseMat() != null)
            list.add(calculationsService.rounding(winterRiseListCurrent.getWinterRiseMat()).toString());
        else list.add("-");

        if (overheadProfitCurrent != null && overheadProfitCurrent.getOverheadExtra() != null)
            list.add(cutValues(calculationsService.rounding(overheadProfitCurrent.getOverheadExtra()).toString()));
        else list.add("-");
        if (overheadProfitCurrent != null && overheadProfitCurrent.getProfitExtra() != null)
            list.add(cutValues(calculationsService.rounding(overheadProfitCurrent.getProfitExtra()).toString()));
        else list.add("-");
        if (winterRiseListCurrent != null && winterRiseListCurrent.getWinterRiseExtra() != null)
            list.add(calculationsService.rounding(winterRiseListCurrent.getWinterRiseExtra()).toString());
        else list.add("-");
        if (winterRiseListCurrent != null && winterRiseListCurrent.getWinterRiseMatExtra() != null)
            list.add(calculationsService.rounding(winterRiseListCurrent.getWinterRiseMatExtra()).toString());
        else list.add("-");
        return list;
    }

    /**
     * get Base Data for Norm collections
     *
     * @param list               exist list data
     * @param winterRiseListBase winterRiseListBase collection
     * @param overheadProfitBase overheadProfitBase collecion
     * @return output data
     **/
    private List<String> getBaseData(OverheadProfitBase overheadProfitBase, WinterRiseListBase winterRiseListBase, List<String> list) {
        if (overheadProfitBase != null && overheadProfitBase.getOverheadBase() != null)
            list.add(cutValues(calculationsService.rounding(overheadProfitBase.getOverheadBase()).toString()));
        else list.add("-");
        if (overheadProfitBase != null && overheadProfitBase.getProfitBase() != null)
            list.add(cutValues(calculationsService.rounding(overheadProfitBase.getProfitBase()).toString()));
        else list.add("-");
        if (winterRiseListBase != null && winterRiseListBase.getWinterRiseBase() != null)
            list.add(calculationsService.rounding(winterRiseListBase.getWinterRiseBase()).toString());
        else list.add("-");
        if (winterRiseListBase != null && winterRiseListBase.getWinterRiseBaseMat() != null)
            list.add(calculationsService.rounding(winterRiseListBase.getWinterRiseBaseMat()).toString());
        else list.add("-");

        if (overheadProfitBase != null && overheadProfitBase.getOverheadBaseExtra() != null)
            list.add(cutValues(calculationsService.rounding(overheadProfitBase.getOverheadBaseExtra()).toString()));
        else list.add("-");
        if (overheadProfitBase != null && overheadProfitBase.getProfitBaseExtra() != null)
            list.add(cutValues(calculationsService.rounding(overheadProfitBase.getProfitBaseExtra()).toString()));
        else list.add("-");
        if (winterRiseListBase != null && winterRiseListBase.getWinterRiseBaseExtra() != null)
            list.add(calculationsService.rounding(winterRiseListBase.getWinterRiseBaseExtra()).toString());
        else list.add("-");
        if (winterRiseListBase != null && winterRiseListBase.getWinterRiseBaseMatExtra() != null)
            list.add(calculationsService.rounding(winterRiseListBase.getWinterRiseBaseMatExtra()).toString());
        else list.add("-");
        return list;
    }

    /**
     * cut point and zero characters
     *
     * @param value idGroup
     * @return value
     **/
    private String cutValues(String value) {
        if (value.startsWith(".0", value.length() - 2))
            return value.substring(0, value.length() - 2);
        return value;
    }

    /**
     * exist this part in group
     *
     * @param idGroup   idGroup
     * @param period    period
     * @param groupName groupName
     * @return boolean
     **/
    private boolean inGroup(Period period, String idGroup, String groupName) {
        Group part = period.getGroupList().stream().filter(s -> s.getId().equals(idGroup)).findFirst().get();
        Group section = period.getGroupList().stream().filter(s -> s.getId().equals(part.getIdParent())).findFirst().get();
        return section.getTitle().equals(groupName) ? true : false;
    }

    /**
     * if not null
     *
     * @param value   input value
     * @return output
     **/
    private String filterDoubleValue(Double value){
         if(value!= null) return calculationsService.rounding(value).toString();
                return "-";
}

}
