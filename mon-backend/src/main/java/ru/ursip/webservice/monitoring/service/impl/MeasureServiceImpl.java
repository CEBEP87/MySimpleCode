package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.Measure;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.repository.MeasureRepository;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.service.MeasureService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.exceptions.ServiceErrorException;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;

import java.util.List;

/**
 * Реализация сервиса для работы с мерами
 *
 * @author samsonov
 * @since 09.02.2017
 */
@Service
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class MeasureServiceImpl implements MeasureService {

    /**
     * Объект логирования
     */
    private Logger logger = LoggerFactory.getLogger(MeasureServiceImpl.class);

    /**
     * Репозиторий для работы с еденицами измерений
     */
    @Autowired
    private  MeasureRepository measureRepository;

    @Override
    public List<Measure> get() throws Exception {
        return measureRepository.findAll();
    }

    @Override
    public Measure get(Integer id) throws Exception {
        return measureRepository.findOne(id);
    }

    @Override
    public Measure create(Measure entity) throws Exception {
        return measureRepository.saveAndFlush(entity);
    }

    @Override
    public Measure update(Measure entity) throws Exception {
        if (entity.getId() == null) {
            throw new ServiceErrorException("Изменение не возможно т.к. не удалось идентифицировать выбранную меру");
        }
        return measureRepository.saveAndFlush(entity);
    }

    @Override
    public void remove(Measure entity) throws Exception {
        if (entity.getId() == null) {
            throw new ServiceErrorException("Удаление не возможно т.к. не удалось идентифицировать выбранную меру");
        }
        // Получение меры из БД по ее идентификатору
        Measure measureInDb = measureRepository.findOne(entity.getId());
        if (measureInDb.getId() !=null) {
            measureRepository.delete(entity);
        } else {
            throw new ServiceErrorException("Удаление не возможно т.к. не удалось найти выбранную меру в системе");
        }
    }
    /**
     * get по id записи
     *
     * @param id       - идентификатор записи
     * @return ResponseEntity
     */
    public ResponseEntity getById(Integer id){
        try {
            return new ResponseEntity(get(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Внутренняя ошибка сервера", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity createMeasure(Measure measure) {
        try {
            return new ResponseEntity(create(measure), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Внутренняя ошибка сервера", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity updateMeasure(Measure measure) {
        try {
            return new ResponseEntity(update(measure), HttpStatus.OK);
        } catch (ServiceErrorException se) {
            return new ResponseEntity(new ServiceError(se.getMessage(), null, null), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Внутренняя ошибка сервера", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity removeMeasure(Measure measure) {
        try {
            remove(measure);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (ServiceErrorException se) {
            return new ResponseEntity(new ServiceError(se.getMessage(), null, null), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Внутренняя ошибка сервера", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
