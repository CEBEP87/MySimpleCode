package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.service.ResourceService;

/**
 * Инструмент для работы с сотрудниками
 *
 * @author samsonov
 * @since 08.06.2017
 */
@Component
public class EmployeeUtil {

    /**
     * Сервися для работы с ресурсами
     */
    @Autowired
    private ResourceService resourceService;

    /**
     * Проверка назначены ли ресурсы на заданного сотрудника
     *
     * @param employeeId - идентификатор сотрудника
     * @return - true - назначены, иначе - false
     */
    public Boolean isResourcesForEmployeeExist(Integer employeeId) {
        Boolean result = false;
        Integer countResources = resourceService.countResourcesForEmployee(employeeId);
        if (countResources > 0) result = true;
        return result;
    }

}
