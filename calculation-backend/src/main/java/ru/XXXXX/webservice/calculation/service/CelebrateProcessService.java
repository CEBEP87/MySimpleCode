package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.CelebrateProcess;

import java.util.List;
import java.util.Set;

/**
 * Service for work with CelebrateProcesses
 *
 * @author samsonov
 * @since 22.09.2017
 */
@Service
public interface CelebrateProcessService {

    /**
     * find CelebrateProcess
     *
     * @param id - identificator by CelebrateProcess
     * @return -new CelebrateProcess
     */
    CelebrateProcess findOneById(String id);

    /**
     * find CelebrateProcess
     *
     * @param id - identificator by period
     * @return -new CelebrateProcess
     */
    List<CelebrateProcess> findOneByIdPeriod(String id);

    /**
     * post CelebrateProcess
     *
     * @param celebrateProcess - posting CelebrateProcess
     * @return -new CelebrateProcess
     */
    CelebrateProcess postCelebrateProcess(CelebrateProcess celebrateProcess);

    /**
     * post CelebrateProcess
     *
     * @param celebrateProcess - posting CelebrateProcess
     * @return -new CelebrateProcess
     */
    List<CelebrateProcess> postCelebrateProcess(List<CelebrateProcess> celebrateProcess);


    /**
     * put CelebrateProcess
     *
     * @param celebrateProcess - put CelebrateProcess
     * @return -new CelebrateProcess
     */
    CelebrateProcess putCelebrateProcess(CelebrateProcess celebrateProcess);


    /**
     * delete CelebrateProcess
     *
     * @param id - put CelebrateProcess
     */
    void deleteCelebrateProcess(String id);

    /**
     * delete  by idPeriod
     *
     * @param id - idPeriod
     */
    void deleteByIdPeriod(String id);

    /**
     * choose return: find by id, or find by period
     *
     * @param id       - get object by id
     * @param idPeriod - get all objects by period
     * @return - list objects or one object
     */
    ResponseEntity getCelebrateProcessService(String id, String idPeriod);

    /**
     * put CelebrateProcess
     *
     * @param celebrateProcess - put CelebrateProcess
     * @return -new CelebrateProcess
     */
    ResponseEntity putCelebrateProcessWithConfirm(CelebrateProcess celebrateProcess);


    /**
     * delete CelebrateProcess with confirm
     *
     * @param id - put CelebrateProcess
     * @return - list of last object and new object or error
     */
    ResponseEntity deleteCelebrateProcessWithConfirm(String id);


    /**
     * post CelebrateProcess with confirm
     *
     * @param celebrateProcess - posting CelebrateProcess
     * @return -new CelebrateProcess
     */
    ResponseEntity postCelebrateProcessWithConfirm(CelebrateProcess celebrateProcess);

    /**
     * SearchByRegex
     *@param idPeriod idPeriod
     * @param row - row
     * @return -new CelebrateProcess
     */
    Set<Object> searchByRegex (String row, String idPeriod);


}
