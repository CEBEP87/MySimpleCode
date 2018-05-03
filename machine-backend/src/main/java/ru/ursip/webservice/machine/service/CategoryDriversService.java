package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.CategoryDrivers;

import java.util.List;

/**
 * Service for work with category-drivers
 *
 * @author samsonov
 * @since 22.09.2017
 */
@Service
public interface CategoryDriversService {
    /**
     * Сервис Контроллера
     *
     * @param id -id
     * @return - CategoryDrivers
     */
    CategoryDrivers get(String id);

    /**
     * Сервис Контроллера
     *
     * @return - CategoryDrivers
     */
    List<CategoryDrivers> getList();
    /**
     * Сервис Контроллера
     *
     * @param id -id
     * @return - CategoryDrivers
     */
    ResponseEntity getCategoryDrivers(String id);
}
