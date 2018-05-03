package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service;

import org.springframework.stereotype.Service;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.*;

import java.util.List;

/**
 * * Service for calculate CostItems
 * <p>
 * Created by samsonov_ky on 26.09.2017.
 */
@Service
public interface CalculationsService {
    /**
     * Rounding value
     *
     * @param val base value
     * @return - result rounding
     */

    Double rounding(Double val);
    /**
     * Rounding value with custom scale
     *
     * @param val base value
     * @param scale scale
     * @return - result rounding
     */
    Double rounding(Double val,int scale);
    /**
     * Get calculated Estimated Cost By Material
     *
     * @param rate          - rate
     * @param sellingPrice  -selling price
     * @param transportCost -transport Cost
     * @param weightBrutto  - weight brutto
     * @param trBool        - transport cost bool
     * @return - map with calculated values
     */
    Double calculateEstimatedPriceForMaterial(Double rate, Double sellingPrice, Double transportCost, Double weightBrutto, Boolean trBool);

    /**
     * Get calculated Estimated Cost By Equipment
     *
     * @param rate          - rate
     * @param sellingPrice  -selling price
     * @param transportCost -transport Cost
     * @param weightBrutto  - weight brutto
     * @param trBool        - transport cost bool
     * @return - map with calculated values
     */

    Double calculateEstimatedPriceForEquipment(Double rate, Double sellingPrice, Double transportCost, Double weightBrutto, Boolean trBool);

    /**
     * Get calculate coefficient by material
     *
     * @param estimatedCurrentPrice - estimated current price
     * @param costResourceBase      - cost Resource Base
     * @return - map with calculated values
     */

    Double calculateRateMaterial(Double estimatedCurrentPrice, Double costResourceBase);

    /**
     * Get calculate coefficient by Equipment
     *
     * @param estimatedCurrentPrice - estimated current price
     * @param costResourceBase      - cost Resource Base
     * @return - map with calculated values
     */

    Double calculateRateEquipment(Double estimatedCurrentPrice, Double costResourceBase);

    /**
     * Get calculated Estimated Cost By Equipment
     *
     * @param listMaterial           - list of material
     * @param listCollectionMaterial - index other resources
     * @param costResourceBase       - cost resources base
     * @param indexOtherResource     - indexOtherResource
     * @return - map with calculated values
     */
    Double calculateRateMaterialProcess(List<Materials> listMaterial, List<Material> listCollectionMaterial
            , Double costResourceBase, Double indexOtherResource);

    /**
     * Get calculated Estimated Cost By Equipment
     *
     * @param listMachines            - list of machines
     * @param indexOtherMachine       - index other Machines
     * @param listCollectionMachines  - listCollectionMachines
     * @param operationOfMachinesBase - operation of machine base
     * @param indexSalary             - index salary
     * @param salaryDriversBase       - salary driver base current collection
     * @return - map with calculated values
     */
    Double calculateRateMachineProcess(List<Machines> listMachines, List<Machine> listCollectionMachines,
                                       Double indexOtherMachine, Double operationOfMachinesBase,
                                       Double indexSalary, Double salaryDriversBase);


    /**
     * Get calculated Rate Machine For Transport
     *
     * @param tr                    - Transport
     * @param period                - Period
     * @param overheadProfitCurrent - OverheadProfitCurrent
     * @param parametrsOfTransport  - parametrsOfTransport
     * @param machine               - machine
     * @return - Transport
     */
    Transport calculateRateMachineForTransport(Transport tr, Period period, OverheadProfitCurrent overheadProfitCurrent, ParametrsOfTransport parametrsOfTransport, Machine machine);

    /**
     * Get calculated rate Salary large Process
     *
     * @param salaryBase          - salary driver base of machine
     * @param rateSalaryPrevious  - salary driver base of machine
     * @param IndexSalaryOfPeriod - salary driver base of machine
     * @return - calculated values
     */
    Double calculateRateSalaryForLargeProcess(Double salaryBase, Double rateSalaryPrevious, Double IndexSalaryOfPeriod);

    /**
     * Get calculated rate Machine large Process
     *
     * @param operationOfMachineBase - salary driver base of machine
     * @param rateMachinePrevious    - salary driver base of machine
     * @param IndexMachineOfPeriod   - salary driver base of machine
     * @return - calculated values
     */
    Double calculateRateMachineForLargeProcess(Double operationOfMachineBase, Double rateMachinePrevious, Double IndexMachineOfPeriod);

    /**
     * Get calculated rate Material large Process
     *
     * @param costResourceBase      - salary driver base of machine
     * @param rateMaterialPrevious  - salary driver base of machine
     * @param IndexMaterialOfPeriod - salary driver base of machine
     * @return - calculated values
     */
    Double calculateRateMaterialForLargeProcess(Double costResourceBase, Double rateMaterialPrevious, Double IndexMaterialOfPeriod);

    /**
     * Get calculated rate Other work large Process
     *
     * @param costOtherWorkBase     - salary driver base of machine
     * @param rateMaterialPrevious  - salary driver base of machine
     * @param IndexOtherWork - salary driver base of machine
     * @return - calculated values
     */
    Double calculateRateOtherWorkForLargeProcess(Double costOtherWorkBase, Double rateMaterialPrevious, Double IndexOtherWork);

    /**
     * Get calculated calculateRateCommonForLargeProcess  large Process
     *
     * @param rateMaterial           - rateMaterial
     * @param costResourceBase       - costResourceBase
     * @param rateMachine            - rateMachine
     * @param operationOfMachineBase - operationOfMachineBase
     * @param rateSalary             - rateSalary
     * @param costOtherWorkBase      - costOtherWorkBase
     * @param rateOtherWork          - rateOtherWork
     * @param salaryBase             - salaryBase
     * @return - calculated values
     */
    Double calculateRateCommonForLargeProcess(Double rateMaterial, Double costResourceBase,
                                              Double rateMachine, Double operationOfMachineBase,
                                              Double rateSalary, Double salaryBase, Double costOtherWorkBase,
                                              Double rateOtherWork);
}