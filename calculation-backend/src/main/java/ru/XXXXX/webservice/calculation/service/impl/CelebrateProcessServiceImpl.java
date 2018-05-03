package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.CelebrateProcess;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.repository.CelebrateProcessRepository;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.CelebrateProcessService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.ValidationService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * CelebrateProcess Service
 *
 * @author Samsonov_KY
 * @since 21.09.2017
 */
@Service
public class CelebrateProcessServiceImpl implements CelebrateProcessService {

    /**
     * Logger object
     */
    private Logger logger = LoggerFactory.getLogger(CelebrateProcessServiceImpl.class);

    /**
     * CelebrateProcess Repository
     */
    @Autowired
    private CelebrateProcessRepository celebrateProcessRepository;
    /**
     * validation Service
     */
    @Autowired
    private ValidationService validationService;


    /**
     * Data-library
     */
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public CelebrateProcess findOneById(String id) {
        return celebrateProcessRepository.findOne(id);
    }

    @Override
    public List<CelebrateProcess> findOneByIdPeriod(String id) {
        return celebrateProcessRepository.findOneByIdPeriod(id);
    }

    @Override
    public CelebrateProcess postCelebrateProcess(CelebrateProcess celebrateProcess) {
        return celebrateProcessRepository.save(celebrateProcess);
    }


    @Override
    public List<CelebrateProcess> postCelebrateProcess(List<CelebrateProcess> celebrateProcess) {
        return celebrateProcessRepository.save(celebrateProcess);
    }

    @Override
    public CelebrateProcess putCelebrateProcess(CelebrateProcess celebrateProcess) {
        return celebrateProcessRepository.save(celebrateProcess);
    }


    @Override
    public void deleteCelebrateProcess(String id) {
        celebrateProcessRepository.delete(id);
    }

    @Override
    public void deleteByIdPeriod(String id) {
        celebrateProcessRepository.deleteByIdPeriod(id);
    }

    @Override
    public ResponseEntity getCelebrateProcessService(String id, String idPeriod) {
        try {
            if (id != null) return new ResponseEntity(findOneById(id), HttpStatus.OK);
            else if (idPeriod != null)
                return new ResponseEntity(findOneByIdPeriod(idPeriod), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity("Error inner Server", HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity("Please check id or idPeriod", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity postCelebrateProcessWithConfirm(CelebrateProcess celebrateProcess) {
        //TODO VALIDATION Pressmark, Title, id_Group Не заполнены обязательные поля».
        if ((celebrateProcess.getPressmark() == null) || (celebrateProcess.getPressmark().equals("")))
            return new ResponseEntity("Не заполнены обязательные поля", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
        if ((celebrateProcess.getTitle() == null) || (celebrateProcess.getTitle().equals("")))
            return new ResponseEntity("Не заполнены обязательные поля", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
        if ((celebrateProcess.getIdGroup() == null) || (celebrateProcess.getIdGroup().equals("")))
            return new ResponseEntity("Не заполнены обязательные поля", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
        //TODO VALIDATION PRESSMARK Расценка с таким шифром ТСН уже существует».
        ///выдернуть c базы обьекты с этим ТСН
        if (!validationService.uniqueTSN(celebrateProcess.getPressmark(), celebrateProcess.getId()))
            new ResponseEntity(" Расценка с таким шифром ТСН уже существует", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);

        return new ResponseEntity(celebrateProcessRepository.save(celebrateProcess), HttpStatus.OK);
    }

    @Override
    public Set<Object> searchByRegex(String row, String idPeriod) {
        Set<Object> result = new HashSet<>();
        result.addAll(naturalSorter(celebrateProcessRepository.findGroundingByRegex(row, idPeriod)));
        result.addAll(naturalSorter(celebrateProcessRepository.findOKPByRegex(row, idPeriod)));
        result.addAll(naturalSorter(celebrateProcessRepository.findPressmarkByRegex(row, idPeriod)));
        result.addAll(naturalSorter(celebrateProcessRepository.findTitleByRegex(row, idPeriod)));
        return result;
    }

    /**
     * naturalSorter
     *
     * @param collection collection
     * @return sorted collection
     */

    private List<CelebrateProcess> naturalSorter(List<CelebrateProcess> collection) {

        List<CelebrateProcess> sortedList = collection.stream()
                .sorted(
                        (e1, e2) -> {
                            try {
                                String[] s1 = e1.getPressmark().split("\\.");
                                String[] s3 = e2.getPressmark().split("\\.");
                                if ((e1.getPressmark().contains(".")) &
                                        (e2.getPressmark().contains("."))) {
                                    String[] s2 = s1[1].split("\\-");
                                    String[] s4 = s3[1].split("\\-");
                                    int result;
                                    result = s1[0].compareTo(s3[0]);
                                    if (result != 0) return result;
                                    result = s2[0].compareTo(s4[0]);
                                    if (result != 0) return result;
                                    result = s2[1].compareTo(s4[1]);
                                    if (result != 0) return result;
                                    return Integer.valueOf(s2[2]).compareTo(Integer.valueOf(s4[2]));
                                } else {
                                    return Integer.valueOf(s1[0]).compareTo(Integer.valueOf(s3[0]));
                                }
                            } catch (Exception e) {
                                return 1;
                            }
                        }
                )
                .collect(Collectors.toList());
        return sortedList;
    }

    @Override
    public ResponseEntity deleteCelebrateProcessWithConfirm(String id) {
        CelebrateProcess celebrateProcess = findOneById(id);
        if (celebrateProcess.getStatus().equals("действующий")) {
            celebrateProcess.setIsSotrudnik(false);
            celebrateProcess.setIsChief(false);
            celebrateProcess.setIsLeader(false);
            celebrateProcess.setRemarkChief("");
            celebrateProcess.setRemarkLeader("");
            celebrateProcess.setLastId(celebrateProcess.getId());
            celebrateProcess.setId(null);
            celebrateProcess.setStatus("Проект");
            celebrateProcess.setAction("на удаление");
            List<CelebrateProcess> listResponse = new ArrayList<>();
            listResponse.add(postCelebrateProcess(celebrateProcess));
            listResponse.add(findOneById(id));
            return new ResponseEntity(listResponse, HttpStatus.OK);
        }
        return new ResponseEntity("Расценка не может быть удалена, т.к. статус «Проект/В сборник/Отклонен/Скрыт", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
    }

    @Override
    public ResponseEntity putCelebrateProcessWithConfirm(CelebrateProcess celebrateProcess) {
        List<CelebrateProcess> listResponse = new ArrayList<>();

        //TODO VALIDATION Pressmark, Title, id_Group Не заполнены обязательные поля».
        if ((celebrateProcess.getPressmark() == null) || (celebrateProcess.getPressmark().equals("")))
            return new ResponseEntity("Не заполнены обязательные поля", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
        if ((celebrateProcess.getTitle() == null) || (celebrateProcess.getTitle().equals("")))
            return new ResponseEntity("Не заполнены обязательные поля", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
        if ((celebrateProcess.getIdGroup() == null) || (celebrateProcess.getIdGroup().equals("")))
            return new ResponseEntity("Не заполнены обязательные поля", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
        //TODO VALIDATION PRESSMARK Расценка с таким шифром ТСН уже существует».
        ///выдернуть c базы обьекты с этим ТСН
        if (!validationService.uniqueTSN(celebrateProcess.getPressmark(), celebrateProcess.getId()))
            new ResponseEntity(" Расценка с таким шифром ТСН уже существует", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);

        if (celebrateProcess.getStatus().equals("действующий")) {
            //TODO ///если не поменялись поля кроме : Grounding,
            // Объем грунта, Масса грунта, Масса оборудования, Масса материалов,
            // id_Overhead, id_Winter_rise_price_mat то сохранить
            CelebrateProcess old = findOneById(celebrateProcess.getId());

            if (!validateFields(celebrateProcess, old)) {
                putCelebrateProcess(celebrateProcess);
                listResponse.add(putCelebrateProcess(celebrateProcess));
                return new ResponseEntity(listResponse, HttpStatus.OK);
            }
            //TODO если поменялись поля кроме : Grounding, Объем грунта, Масса грунта, Масса оборудования, Масса материалов, id_Overhead, id_Winter_rise_price_mat то создать копию
            //TODO и полям Проверено», «Замечания Начальник/Руководителя» присваивается значение «false «last_id» записывается id расценки - «Status» = «Проект», «Action» = «на изменение».
            else {
                celebrateProcess.setId(null);
                celebrateProcess.setIsSotrudnik(false);
                celebrateProcess.setIsChief(false);
                celebrateProcess.setIsLeader(false);
                celebrateProcess.setRemarkChief("");
                celebrateProcess.setRemarkLeader("");
                celebrateProcess.setLastId(celebrateProcess.getId());
                celebrateProcess.setStatus("Проект");
                celebrateProcess.setAction("На изменение");
                listResponse.add(postCelebrateProcess(celebrateProcess));
                listResponse.add(old);
                return new ResponseEntity(listResponse, HttpStatus.OK);
            }
        }
        if (celebrateProcess.getStatus().equals("Проект")) {
            CelebrateProcess old = findOneById(celebrateProcess.getId());
            if (old.getStatus().equals(celebrateProcess.getStatus())) {
                listResponse.add(putCelebrateProcess(celebrateProcess));
                return new ResponseEntity(listResponse, HttpStatus.OK);
            } else {
                if ((old.getStatus().equals("Утвержден")) & (celebrateProcess.getStatus().equals("Проект"))) {
                    CelebrateProcess lastProcess = findOneById(celebrateProcess.getLastId());
                    lastProcess.setStatus("действующий");
                    putCelebrateProcess(lastProcess);
                    listResponse.add(putCelebrateProcess(celebrateProcess));
                    return new ResponseEntity(listResponse, HttpStatus.OK);
                }
                if ((old.getStatus().equals("Отклонен")) & (celebrateProcess.getStatus().equals("Проект"))) {
                    listResponse.add(putCelebrateProcess(celebrateProcess));
                    return new ResponseEntity(listResponse, HttpStatus.OK);
                }

            }
        }
        if ((celebrateProcess.getStatus().equals("В сборник")) | ((celebrateProcess.getStatus().equals("Отклонен")))) {
            CelebrateProcess old = findOneById(celebrateProcess.getId());
            if (old.getStatus().equals(celebrateProcess.getStatus()))
                return new ResponseEntity("В сборник/Отклонен». Верните расценку в статус «Проект» и повторите попытку", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
            else {
                if ((old.getStatus().equals("Проект")) & (celebrateProcess.getStatus().equals("Отклонен"))) {
                    listResponse.add(putCelebrateProcess(celebrateProcess));
                    return new ResponseEntity(listResponse, HttpStatus.OK);
                }
                if ((old.getStatus().equals("Проект")) & (celebrateProcess.getStatus().equals("В Сборник"))) {
                    CelebrateProcess lastProcess = findOneById(celebrateProcess.getLastId());
                    lastProcess.setStatus("Скрыт");
                    putCelebrateProcess(lastProcess);
                    listResponse.add(putCelebrateProcess(celebrateProcess));
                    return new ResponseEntity(listResponse, HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity("Не учтенный исход событий", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
    }

    /**
     * validate unique Fields
     *
     * @param newCelebrateProcess  - new object
     * @param oldCelebratetProcess - old object
     * @return - bool validate
     */
    private Boolean validateFields(CelebrateProcess newCelebrateProcess, CelebrateProcess oldCelebratetProcess) {
        Boolean changeFields = false;
        if (!oldCelebratetProcess.getAction().equals(newCelebrateProcess.getAction())) changeFields = true;
        if (!oldCelebratetProcess.getFixedTime().equals(newCelebrateProcess.getFixedTime())) changeFields = true;
        if (!oldCelebratetProcess.getIdGroup().equals(newCelebrateProcess.getIdGroup())) changeFields = true;
        if (!oldCelebratetProcess.getIdPeriod().equals(newCelebrateProcess.getIdPeriod())) changeFields = true;
        if (!oldCelebratetProcess.getIdPressmarkOverheadProfit().equals(newCelebrateProcess.getIdPressmarkOverheadProfit()))
            changeFields = true;
        if (!oldCelebratetProcess.getIdUnitOfMeasure().equals(newCelebrateProcess.getIdUnitOfMeasure()))
            changeFields = true;
        if (!oldCelebratetProcess.getIdPressmarkWinterRiseList().equals(newCelebrateProcess.getIdPressmarkWinterRiseList()))
            changeFields = true;
        if (!oldCelebratetProcess.getOperationOfMachinesBase().equals(newCelebrateProcess.getOperationOfMachinesBase()))
            changeFields = true;
        if (!oldCelebratetProcess.getPressmark().equals(newCelebrateProcess.getPressmark())) changeFields = true;
        if (!oldCelebratetProcess.getRateMachine().equals(newCelebrateProcess.getRateMachine())) changeFields = true;
        if (!oldCelebratetProcess.getRateMaterial().equals(newCelebrateProcess.getRateMaterial())) changeFields = true;
        if (!oldCelebratetProcess.getSalaryBase().equals(newCelebrateProcess.getSalaryBase())) changeFields = true;
        if (!oldCelebratetProcess.getSalaryDriversBase().equals(newCelebrateProcess.getSalaryDriversBase()))
            changeFields = true;
        if (!oldCelebratetProcess.getStatus().equals(newCelebrateProcess.getStatus())) changeFields = true;
        if (!oldCelebratetProcess.getTitle().equals(newCelebrateProcess.getTitle())) changeFields = true;
        if (!oldCelebratetProcess.getIsSotrudnik().equals(newCelebrateProcess.getIsSotrudnik())) changeFields = true;
        if (!oldCelebratetProcess.getRemarkChief().equals(newCelebrateProcess.getRemarkChief())) changeFields = true;
        if (!oldCelebratetProcess.getIsChief().equals(newCelebrateProcess.getIsChief())) changeFields = true;
        if (!oldCelebratetProcess.getIsLeader().equals(newCelebrateProcess.getIsLeader())) changeFields = true;
        if (!oldCelebratetProcess.getRemarkLeader().equals(newCelebrateProcess.getRemarkLeader())) changeFields = true;
        return changeFields;
    }
}
