package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.service;

import org.springframework.stereotype.Service;

/**
 * * Service for calculate CostItems
 * <p>
 * Created by samsonov_ky on 26.09.2017.
 */
@Service
public interface CalculationsService {

    /**
     * Get calculated Estimated Cost By Equipment
     *
     * @param rate          - rate
     * @param sellingPrice  -selling price
     * @param transportCost -transport Cost
     * @param weightBrutto  - weight brutto
     * @param trBool        - transport cost bool
     * @param transportCostCostBase        - transportCostCostBase
     * @return - map with calculated values
     */

    Double calculateEstimatedPriceForEquipment(Double rate, Double sellingPrice, Double transportCost, Double weightBrutto, Boolean trBool, Double transportCostCostBase);

    /**
     * Get calculate coefficient by Equipment
     *
     * @param estimatedCurrentPrice - estimated current price
     * @param costResourceBase      - cost Resource Base
     * @return - map with calculated values
     */

    Double calculateRateEquipment(Double estimatedCurrentPrice, Double costResourceBase);

}