package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils;

/**
 * Перечисление статусов действия с их идентификаторами в БД
 *
 * @author samsonov
 * @since 08.06.2017
 */
public enum StatusActionEnum {

    /**
     * Создание
     */
    CREATION(1),

    /**
     * Редактирование
     */
    UPDATE(2),

    /**
     * Без статуса
     */
    EMPTY(5);

    /**
     * Идентифкатор статуса действия
     */
    private Integer statusActionId;


    /**
     * Конструктор
     *
     * @param statusActionId - идентифкатор статуса действия
     */
    StatusActionEnum(Integer statusActionId) {
        this.statusActionId = statusActionId;
    }

    /**
     * Получение идентифкатора статуса действия
     *
     * @return - идентифкатор статуса действия
     */
    public Integer getStatusActionId() {
        return statusActionId;
    }
}
