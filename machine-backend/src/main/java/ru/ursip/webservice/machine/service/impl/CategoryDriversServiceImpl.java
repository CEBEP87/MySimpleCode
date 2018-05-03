package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.CategoryDrivers;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.repository.CategoryDriversRepository;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service.CategoryDriversService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;

import java.util.List;

/**
 * Period Service
 *
 * @author Samsonov_KY
 * @since 21.09.2017
 */
@Service
public class CategoryDriversServiceImpl implements CategoryDriversService {

    /**
     * Logger object
     */
    private Logger logger = LoggerFactory.getLogger(CategoryDriversServiceImpl.class);

    /**
     * Репозиторий для работы с типами ресурсов
     */
    @Autowired
    private CategoryDriversRepository categoryDriversRepository;

    /**
     * getall
     *
     * @return - List CategoryDrivers
     */
    @Override
    public List<CategoryDrivers> getList(){
        try {
            return categoryDriversRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * getByID
     *
     * @param id -id
     * @return -  CategoryDrivers
     */
    @Override
    public CategoryDrivers get(String id) {
        try {
            return categoryDriversRepository.findOne(id);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * create
     *
     * @param entity -entity
     * @return - List CategoryDrivers
     */

    public CategoryDrivers create(CategoryDrivers entity) throws Exception {
        return categoryDriversRepository.save(entity);
    }

    /**
     * update
     *
     * @param entity -entity
     * @return - List CategoryDrivers
     */

    public CategoryDrivers update(CategoryDrivers entity) throws Exception {
        return categoryDriversRepository.save(entity);
    }

    /**
     * remove
     *
     * @param entity -entity
     *               <p>
     *               return - List CategoryDrivers
     */

    public void remove(CategoryDrivers entity) throws Exception {
        categoryDriversRepository.delete(entity);
    }

    /**
     * getCategoryDrivers service for controller
     *
     * @param id id
     * @return - List CategoryDrivers
     */

    public ResponseEntity getCategoryDrivers(String id) {
        try {
            return (id == null) ?
                    new ResponseEntity(getList(), HttpStatus.OK) :
                    new ResponseEntity(get(id), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Внутренняя ошибка сервера", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
