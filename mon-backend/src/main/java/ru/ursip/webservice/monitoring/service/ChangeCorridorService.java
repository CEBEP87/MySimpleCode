package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.service;


import org.springframework.http.ResponseEntity;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.ChangeCorridor;

/**
 * Сервис для работы с коридором значений
 *
 * @author samsonov
 * @since 03.03.2017
 */
public interface ChangeCorridorService extends BaseService<ChangeCorridor> {
    /**
     * Сервис Контроллера
     *
     * @return - list of ChangeCorridor
     */
    ResponseEntity getAll();

    /**
     * Сервис Контроллера
     * @param changeCorridor - changeCorridor
     * @return - changeCorridor
     */
    ResponseEntity updateChangeCorridor(ChangeCorridor changeCorridor);
}
