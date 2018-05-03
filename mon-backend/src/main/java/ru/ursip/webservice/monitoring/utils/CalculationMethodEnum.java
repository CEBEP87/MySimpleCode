package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Перечисление методов вычислений с их идентификаторами в БД
 *
 * @author samsonov
 * @since 16.06.2017
 */
public enum CalculationMethodEnum {

    /**
     * По минимальной цене
     */
    MIN_PRICE(1),

    /**
     * По средней цене
     */
    AVG_PRICE(2);

    /**
     * Соотношение значений к полям
     */
    private static Map<Integer, CalculationMethodEnum> map = new HashMap<Integer, CalculationMethodEnum>();

    /**
     * Соотношение значений к полям
     */
    static {
        for (CalculationMethodEnum cmEnum : CalculationMethodEnum.values()) {
            map.put(cmEnum.calculationMethodId, cmEnum);
        }
    }

    /**
     * Получение enum по его значению
     *
     * @param calculationMethodId - значение enum
     * @return - enum
     */
    public static CalculationMethodEnum valueOf(int calculationMethodId) {
        return map.get(calculationMethodId);
    }

    /**
     * Идентификатор метода вычислений
     */
    private Integer calculationMethodId;

    /**
     * Конструктор
     *
     * @param calculationMethodId -  Идентификатор метода вычислений
     */
    CalculationMethodEnum(Integer calculationMethodId) {
        this.calculationMethodId = calculationMethodId;
    }

    /**
     * Получение идентифкатора метода вычислений
     *
     * @return -  Идентификатор метода вычислений
     */
    public Integer getCalculationMethodId() {
        return calculationMethodId;
    }
}
