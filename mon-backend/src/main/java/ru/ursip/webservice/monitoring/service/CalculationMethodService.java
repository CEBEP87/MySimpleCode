package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.service;


import org.springframework.http.ResponseEntity;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.CalculationMethod;

/**
 * Сервис для работы с методами вычислений
 *
 * @author samsonov
 * @since 02.03.2017
 */
public interface CalculationMethodService extends BaseService<CalculationMethod> {

    /**
     * Получение действющего метода вычислений
     *
     * @return - действующий метод вычислений
     */
    CalculationMethod getActiveCalculationMethod();

    /**
     * Вычисление отклонения текущего значения от начального
     *
     * @param baseValue    - начальное значение
     * @param currentValue - текущее значение
     * @return - отклонение
     */
    Integer calculateDeviation(Long baseValue, Long currentValue);


    /**
     * Сервис Контроллера
     *
     * @return - list of calculation methods
     */
    ResponseEntity getAll();

    /**
     * Сервис Контроллера
     * @param id - identifier of Calculation Method
     * @return - calculation method
     */
    ResponseEntity getById(Integer id);

    /**
     * Сервис Контроллера
     * @param calculationMethod - Calculation Method
     * @return - calculation method
     */
    ResponseEntity updateCalculationMethod(CalculationMethod calculationMethod);
}
