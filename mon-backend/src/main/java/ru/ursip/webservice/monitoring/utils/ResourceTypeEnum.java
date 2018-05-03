package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils;

/**
 * Перечисление типов ресурсов с их идентификаторами в БД
 *
 * @author samsonov
 * @since 09.06.2017
 */
public enum ResourceTypeEnum {

    /**
     * Материалы
     */
    MATERIALS(4),

    /**
     * Оборудование
     */
    EQUIPMENTS(5);

    /**
     * Идентификатор типа ресурсов
     */
    private Integer resourceTypeId;


    /**
     * Конструктор
     *
     * @param resourceTypeId - идентификатор типа ресурсов
     */
    ResourceTypeEnum(Integer resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    /**
     * Получение идентификатора типа ресурса
     *
     * @return - идентификатор типа ресурса
     */
    public Integer getResourceTypeId() {
        return resourceTypeId;
    }
}
