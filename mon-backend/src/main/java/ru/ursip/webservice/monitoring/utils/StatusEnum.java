package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils;

/**
 * Перечисление статусов с их идентификаторами в БД
 *
 * @author samsonov
 * @since 08.06.2017
 */
public enum StatusEnum {

    /**
     * Обновление
     */
    REFRESH(1),

    /**
     * На проверке
     */
    ON_VERIFICATION(2),

    /**
     * На утверждении
     */
    ON_APPROVAL(3),

    /**
     * Утвержден
     */
    APPROVED(4);

    /**
     * Идентификатор статуса
     */
    private Integer statusId;

    /**
     * Конструктор
     *
     * @param statusId - идентификатор статуса
     */
    StatusEnum(Integer statusId) {
        this.statusId = statusId;
    }

    /**
     * Получение идентификатора статуса
     *
     * @return - идентификатор статуса
     */
    public Integer getStatusId() {
        return statusId;
    }
}
