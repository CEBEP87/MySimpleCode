package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.*;

/**
 * * Service for calculate CostItems
 *
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
     * Get calculated CostItems
     *
     * @param input - input costItems in period
     * @param periodFields - input fields of period
     * @return - calculated costItems
     */
    OutputCalculation calculateCostItems(Resource input, PeriodFields periodFields);

    /**
     * quick calculated CostItems
     *
     * @param idPeriod - idPeriod
     * @param idMachine - idMachine -
     * @param estimatedCurrentPriceH - estimatedCurrentPriceH
     * @return - calculated costItems
     */
    ResponseEntity quickCalculateResource(String idPeriod, String idMachine, Double estimatedCurrentPriceH);
}