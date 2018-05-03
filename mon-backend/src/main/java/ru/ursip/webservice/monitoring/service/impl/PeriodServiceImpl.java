package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.repository.PeriodRepository;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.repository.ResourcePeriodRepository;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.repository.ResourceRepository;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.repository.specification.PeriodSpecifications;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.service.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.CalculationMethodEnum;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.PeriodStatusEnum;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.StatusEnum;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.exceptions.ServiceErrorException;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ValidationError;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.utils.Validator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с периодами
 *
 * @author samsonov
 * @since 14.02.2017
 */
@Service
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class PeriodServiceImpl implements PeriodService {

    /**
     * Объект логирования
     */
    private Logger logger = LoggerFactory.getLogger(PeriodServiceImpl.class);

    /**
     * Репозиторий для работы с таблицей периодов
     */
    @Autowired
    private PeriodRepository periodRepository;


    /**
     * Репозиторий для работы с ресурсами
     */

    @Autowired
    private ResourceRepository resourceRepository;


    /**
     * Репозиторий для работы с ресурсами периода
     */

    @Autowired
    private ResourcePeriodRepository resourcePeriodRepository;


    /**
     * Сервис для работы с ресурсами
     */
    @Autowired
    private ResourceService resourceService;

    /**
     * Сервис для работы с ресурсами в периодах
     */
    @Autowired
    private ResourcePeriodService resourcePeriodService;

    /**
     * Сервис для работы со статусами ресурсов
     */
    @Autowired
    private ResourceStatusService resourceStatusService;

    /**
     * Сервис для работы со статусами действия над ресурсами
     */
    @Autowired
    private ResourceStatusActionService resourceStatusActionService;

    /**
     * Сервис для работы со статусами периодов
     */
    @Autowired
    private PeriodStatusService periodStatusService;

    /**
     * Сервис для работы с методами вычислений
     */
    @Autowired
    private CalculationMethodService calculationMethodService;

    /**
     * Сервис для манипуляций с transport cost
     */
    @Autowired
    private TransportCostPeriodService transportCostPeriodService;


    @Override
    public List<Period> get(Period period) throws Exception {
        if (ObjectUtils.nullSafeEquals(period.getId(), null) &&
                ObjectUtils.nullSafeEquals(period.getResourceType(), null) &&
                ObjectUtils.nullSafeEquals(period.getResourceType().getId(), null) &&
                ObjectUtils.nullSafeEquals(period.getStatus(), null) &&
                ObjectUtils.nullSafeEquals(period.getStatus().getId(), null)) {
            return periodRepository.findAll();
        }
        return periodRepository.findAll(PeriodSpecifications.withDynamicQuery(period));
    }

    @Override
    public List<Period> get() throws Exception {
        return periodRepository.findAll();
    }

    @Override
    public Period get(Integer id) throws Exception {
        return periodRepository.findOne(id);
    }

    @Override
    public Period create(Period entity) throws Exception {
        // Получение последнего периода с заданным типом ресурсов
        List<Period> prevPeriods = periodRepository.getPrevPeriodList(entity.getResourceType(), entity.getDateTill());
        Period lastPeriod = null;
        if (!prevPeriods.isEmpty()) {
            lastPeriod = prevPeriods.get(0);
            // Проверка статуса предидущего периода
            if (lastPeriod.getStatus().getId() != PeriodStatusEnum.CLOSED.getPeriodStatusId()) {
                throw new ServiceErrorException("Перед открытием нового периода необходимо закрыть прошлый");
            }
        }
        // Создание нового периода
        Period newPeriod = periodRepository.saveAndFlush(entity);
        if (lastPeriod != null) {
            ResourcePeriod resourcePeriodFilter = new ResourcePeriod();
            resourcePeriodFilter.setFkPeriod(new Period(lastPeriod.getId()));
            resourcePeriodFilter.setIsDeleted(false);
            // Результат полученный с применением фильтра для поиска ресурсов в периоде
            List<ResourcePeriod> prevResourcesPeriod = resourcePeriodService.getAllWithJF(lastPeriod, false);
            // Заполнение таблицы ресурс периодов информацией о ресурсах в новом периоде на основе данных из предидущего периода
            List<ResourcePeriod> newResourcesPeriod = fillResourcePeriod(lastPeriod, newPeriod, prevResourcesPeriod);
            resourcePeriodService.createAll(newResourcesPeriod);

            ///найти утвержденные ресурсы, которые необходимо отобразить в новом периоде
            ResourceType rs = newPeriod.getResourceType();
            ///Новые ресурсы
            addNewResources(newPeriod, rs);
            ///ресурсы которые были на редактировании
            handlingEditedResources(lastPeriod, newPeriod, prevResourcesPeriod, newResourcesPeriod, rs);
            transportCostPeriodService.copyToNewPeriod(entity.getId(), lastPeriod.getId());
        }
        return newPeriod;
    }

    /**
     * обработка отредактированных ресурсов
     *
     * @param lastPeriod          -lastPeriod
     * @param newPeriod           newPeriod
     * @param prevResourcesPeriod -prevResourcesPeriod
     * @param newResourcesPeriod  newResourcesPeriod
     * @param rs                  rs
     */
    private void handlingEditedResources(Period lastPeriod, Period newPeriod, List<ResourcePeriod> prevResourcesPeriod, List<ResourcePeriod> newResourcesPeriod, ResourceType rs) {
        List<Resource> listEditedResources = resourceRepository.getResourcesStatement(rs.getId(), 2);
        for (Resource res : listEditedResources) {
            if (lastPeriod != null) if (res.getLastId() != null) {
                List<ResourcePeriod> lastResourcePeriod = prevResourcesPeriod.stream().filter(x -> x.getFkResource().getId().equals(res.getLastId()))
                        .collect(Collectors.toList());
                for (ResourcePeriod lastRP : lastResourcePeriod) {
                    ResourcePeriod rp = new ResourcePeriod();
                    rp.setFkPeriod(newPeriod);
                    rp.setFkResource(res);
                    rp.setFkCompany(lastRP.getFkCompany());
                    rp.setPricePrimary(lastRP.getChangePrimary());
                    rp.setChangePrimary(rp.getPricePrimary());
                    rp.setManufacturer(lastRP.getManufacturer());
                    rp.setDiviationWithMainPrice(lastRP.getDiviationWithMainPrice());
                    rp.setRemarkLeader(lastRP.getRemarkLeader());
                    rp.setRemarkChief(lastRP.getRemarkChief());
                    rp.setMainPrice(newResourcesPeriod.stream().filter(x -> x.getFkResource().getId().equals(res.getLastId())).filter(x -> x.getFkCompany().equals(rp.getFkCompany())).findFirst().get().getMainPrice());
                    resourcePeriodRepository.saveAndFlush(rp);
                    resourcePeriodRepository.deleteByFkResource(res.getLastId(), newPeriod.getId());
                    // resourceRepository.deleteById(res.getLastId());
                }
                //поменять Status_Action у ресурса
                resourceRepository.updateResourceStatusAction(res.getId(), 5);
                //поменять Status у ресурса
                resourceRepository.updateResourceStatus(res.getId(), 1);
            }
        }
    }

    /**
     * добавление новых ресурсов
     *
     * @param newPeriod -newPeriod
     * @param rs        rs
     */
    private void addNewResources(Period newPeriod, ResourceType rs) {
        List<Resource> listNewResources = resourceRepository.getResourcesStatement(rs.getId(), 1);
        for (Resource res : listNewResources) {
            ResourcePeriod rp = new ResourcePeriod();
            rp.setFkPeriod(newPeriod);
            rp.setFkResource(res);
            rp.setFkCompany(2425);
            //поменять Status_Action у ресурса
            resourceRepository.updateResourceStatusAction(res.getId(), 5);
            //поменять Status у ресурса
            resourceRepository.updateResourceStatus(res.getId(), 1);
            resourcePeriodRepository.saveAndFlush(rp);
        }
    }

    @Override
    public Period update(Period entity) throws Exception {
        if (entity.getId() == null) {
            throw new ServiceErrorException("Изменение не возможно т.к. не удалось идентифицировать выбранный период");
        }
        // Получение периода из БД по его идентификатору
        Period currentPeriod = periodRepository.findOne(entity.getId());
        if (currentPeriod.getStatus().getId() == PeriodStatusEnum.CLOSED.getPeriodStatusId()) {
            throw new ServiceErrorException("С данным периодом нельзя осуществлять какие либо действия, так как период закрыт.");
        }
        Period updatePeriod = periodRepository.saveAndFlush(entity);
        return updatePeriod;
    }

    @Override
    public void remove(Period entity) throws Exception {
        if (entity.getId() == null) {
            throw new ServiceErrorException("Удаление не возможно т.к. не удалось идентифицировать выбранный период");
        }
        // Получение периода из БД по его идентификатору
        Period periodInDb = periodRepository.findOne(entity.getId());
        if (periodInDb.getId() != null) {
            periodRepository.delete(periodInDb);
        } else {
            throw new ServiceErrorException("Удаление не возможно т.к. не удалось найти выбранный период в системе");
        }
    }


    @Override
    public List<Period> getPrevPeriodList(ResourceType resourceType, Date dateTill) {
        return periodRepository.getPrevPeriodList(resourceType, dateTill);
    }

    @Override
    public ResponseEntity getAllPeriod(Integer periodId, Integer periodStatusId, Integer resourceTypeId) {
        try {
            Period periodFilter = new Period(periodId, new ResourceType(resourceTypeId), new PeriodStatus(periodStatusId));
            return new ResponseEntity(get(periodFilter), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Внутренняя ошибка сервера", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity getByIdPeriodResponse(Integer id) {
        try {
            return new ResponseEntity(get(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Внутренняя ошибка сервера", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Period getByIdPeriod(Integer id) {
        try {
            return get(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ResponseEntity getLastPeriod(Integer id) {
        try {
            Period pcurentPeriod=get(id);
            List<Period> list =getPrevPeriodList(pcurentPeriod.getResourceType(), pcurentPeriod.getDateTill());
            return new ResponseEntity(list.get(1),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Внутренняя ошибка сервера", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity createPeriod(Period period) {
        try {
            List<ValidationError> errors = Validator.processValidate(period);

            return (errors.size() == 0 ?
                    new ResponseEntity(create(period), HttpStatus.CREATED) :
                    new ResponseEntity(new ServiceError("Ошибка валидации данных", null, errors), HttpStatus.PRECONDITION_FAILED));

        } catch (ServiceErrorException se) {
            return new ResponseEntity(new ServiceError(se.getMessage(), null, null), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Внутренняя ошибка сервера", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity updatePeriod(Period period) {
        try {
            List<ValidationError> errors = Validator.processValidate(period);
            return (errors.size() == 0 ?
                    new ResponseEntity(update(period), HttpStatus.OK) :
                    new ResponseEntity(new ServiceError("Ошибка валидации данных", null, errors), HttpStatus.PRECONDITION_FAILED));

        } catch (ServiceErrorException se) {
            return new ResponseEntity(new ServiceError(se.getMessage(), null, null), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Внутренняя ошибка сервера", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity removePeriod(Period period) {
        try {
            remove(period);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (ServiceErrorException se) {
            return new ResponseEntity(new ServiceError(se.getMessage(), null, null), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Внутренняя ошибка сервера", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Заполнение таблицы ресурс периодов информацией о ресурсах в новом периоде
     *
     * @param lastPeriod          - объект последнего периода
     * @param newPeriod           - объект текущего периода
     * @param prevResourcesPeriod - список ресурсов в предидущем периоде
     * @return - список с информацией о ресурсах в периоде
     */
    private List<ResourcePeriod> fillResourcePeriod(Period lastPeriod, Period newPeriod,
                                                    List<ResourcePeriod> prevResourcesPeriod) throws Exception {
        // Список новых ресурсов в периоде
        List<ResourcePeriod> resourcesPeriodForCreate = new ArrayList<>();
        // Получение записи статуса над ресурсом "Обновление"
        ResourceStatus refreshResourceStatus = resourceStatusService.get(StatusEnum.REFRESH.getStatusId());
        // Формирование Map, где ключ ссылка на заменяемый ресурс, значение - новый ресурс
        Map<Integer, Resource> replacementResourcesMap = resourceService.getResourcesReplacement().
                stream().filter((r) -> !r.getIsDeleted() && r.getIsActive()).collect(
                Collectors.toMap(Resource::getResourceRoot, Function.identity()));
        //Фомирование Map,  где ключ - ресурс , значение - список поставщиков ресурса с ценами
        Map<Resource, List<ResourcePeriod>> companyPricesForResourcesMap = prevResourcesPeriod.stream().collect(Collectors.groupingBy(ResourcePeriod::getFkResource));
        Map<Resource, Double> resourcesMainPrice = new HashMap<>();
        // Формирвоание основных цен ресурсов на основе списка цен поставщиков предидущего периода и метода вычислений периода
        long summ = 0l;
        int resourcePeriodCount = 0;
        if (lastPeriod.getCalculationMethod().getId().equals(CalculationMethodEnum.AVG_PRICE.getCalculationMethodId()))
            resourcesMainPrice = calculateByAvgPrice(companyPricesForResourcesMap, resourcesMainPrice);
        else resourcesMainPrice = calculateByMinPrice(companyPricesForResourcesMap, resourcesMainPrice);

        // В случае если какой-либо поставщик ресурса был выбран как поставщик с основной ценой
        resourcesMainPrice = putProviderWithMainPrice(companyPricesForResourcesMap, resourcesMainPrice);

        Map<Resource, Double> finalResourcesMainPrice = resourcesMainPrice;
        prevResourcesPeriod.forEach(resourcePeriod -> {
            // Объект цены поставщика на ресурс в новом периоде
            ResourcePeriod newResourcePeriod = new ResourcePeriod();
            newResourcePeriod.setFkPeriod(newPeriod);
            newResourcePeriod.setFkCompany(resourcePeriod.getFkCompany());
            newResourcePeriod.setResourceStatus(refreshResourceStatus);
            newResourcePeriod.setIsReturn(false);
            newResourcePeriod.setIsDeleted(false);
            newResourcePeriod.setIsTransportCosts(resourcePeriod.getIsTransportCosts());
            newResourcePeriod.setIsMainPrice(resourcePeriod.getIsMainPrice());
            Double mainPrice = finalResourcesMainPrice.get(resourcePeriod.getFkResource());
            if (mainPrice != null) newResourcePeriod.setMainPrice(mainPrice.longValue());
            // Установка начальной цены для поставщика ресурса в новом периоде (такая же как в прошлом)
            newResourcePeriod.setPricePrimary(resourcePeriod.getPricePrimary());
            // Установка ресурса как в прошлом периоде для поставщика в новом периоде
            newResourcePeriod.setFkResource(resourcePeriod.getFkResource());
            // Получение ресурса, который является заменой для ресурса поставляемого поствщиком в прошлом периоде
            if (replacementResourcesMap.containsKey(resourcePeriod.getId())) {
                Resource replacementResource = replacementResourcesMap.get(resourcePeriod.getId());
                // Ресурс, который является заменой для ресурса поставляемого поствщиком в прошлом периоде был найден
                // и прошел подтверждение на замену
                if (replacementResource != null) {
                    newResourcePeriod.setFkResource(replacementResource);
                    newResourcePeriod.setPricePrimary(replacementResource.getBasicPrice());
                }
            }
            // Измененная цена не пустая - установить начальную цену ресурса поставщика в новом периоде =
            // = измененная цена в прошлом периоде
            if (resourcePeriod.getChangePrimary() != null) {
                newResourcePeriod.setPricePrimary(resourcePeriod.getChangePrimary());
                newResourcePeriod.setChangePrimary(newResourcePeriod.getPricePrimary());
            } else newResourcePeriod.setPricePrimary(0l);
            // Добавляем запись цены поставщика на ресурс для нового перида в список на создание
            if (newResourcePeriod.getChangePrimary() != null && newResourcePeriod.getMainPrice() != null) {
                newResourcePeriod.setDiviationWithMainPrice(calculationMethodService.calculateDeviation(newResourcePeriod.getMainPrice(),
                        newResourcePeriod.getChangePrimary()));
            }
            resourcesPeriodForCreate.add(newResourcePeriod);
        });
        return resourcesPeriodForCreate;
    }

    /**
     * Заполняем поставщика с основной ценой
     *
     * @param companyPricesForResourcesMap - companyPricesForResourcesMap
     * @param resourcesMainPrice           - resourcesMainPrice
     * @return - resourcesMainPrice
     */

    private Map<Resource, Double> putProviderWithMainPrice(Map<Resource, List<ResourcePeriod>> companyPricesForResourcesMap, Map<Resource, Double> resourcesMainPrice) {
        for (Resource key : companyPricesForResourcesMap.keySet()) {
            List<ResourcePeriod> currentResourcePeriods = companyPricesForResourcesMap.get(key);
            for (ResourcePeriod rp : currentResourcePeriods) {
                if (rp.getIsMainPrice()) {
                    resourcesMainPrice.put(key, rp.getChangePrimary() == null ? null : rp.getChangePrimary().doubleValue());
                }
            }
        }
        return resourcesMainPrice;
    }


    /**
     * расчет по средневзвешенной цене
     *
     * @param companyPricesForResourcesMap - companyPricesForResourcesMap
     * @param resourcesMainPrice           - resourcesMainPrice
     * @return - resourcesMainPrice
     */
    private Map<Resource, Double> calculateByAvgPrice(Map<Resource, List<ResourcePeriod>> companyPricesForResourcesMap, Map<Resource, Double> resourcesMainPrice) {
        int resourcePeriodCount;
        for (Resource key : companyPricesForResourcesMap.keySet()) {
            List<ResourcePeriod> keyValue = companyPricesForResourcesMap.get(key);
            long summ = 0l;
            resourcePeriodCount = 0;
            for (ResourcePeriod currentRP : keyValue) {
                if (currentRP.getChangePrimary() != null) {
                    summ = summ + currentRP.getChangePrimary();
                } else {
                    if (currentRP.getPricePrimary() != null) {
                        summ = summ + currentRP.getPricePrimary();
                    }
                }
                resourcePeriodCount++;
            }
            long avg = summ / resourcePeriodCount;

            resourcesMainPrice.put(key, (double) avg);
        }
        return resourcesMainPrice;
    }

    /**
     * расчет по минимальной цене
     *
     * @param companyPricesForResourcesMap - companyPricesForResourcesMap
     * @param resourcesMainPrice           - resourcesMainPrice
     * @return - resourcesMainPrice
     */
    private Map<Resource, Double> calculateByMinPrice(Map<Resource, List<ResourcePeriod>> companyPricesForResourcesMap, Map<Resource, Double> resourcesMainPrice) {
        for (Resource key : companyPricesForResourcesMap.keySet()) {
            List<ResourcePeriod> currentResourcePeriods = companyPricesForResourcesMap.get(key);
            Long minPrice = Long.MAX_VALUE;
            for (ResourcePeriod rp : currentResourcePeriods) {
                if (rp.getChangePrimary() != null && minPrice > rp.getChangePrimary())
                    minPrice = rp.getChangePrimary();
            }
            if (minPrice.equals(Long.MAX_VALUE)) {
                minPrice = currentResourcePeriods.get(0) != null ? currentResourcePeriods.get(0).getMainPrice() : null;
            }
            if (minPrice != null) {
                resourcesMainPrice.put(key, minPrice.doubleValue());
            } else
                resourcesMainPrice.put(key, null);
        }
        return resourcesMainPrice;
    }

}
