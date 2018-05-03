package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils;

/**
 * Перечисление статусов периода с их идентификаторами в БД
 *
 * @author samsonov
 * @since 08.06.2017
 */
public enum PeriodStatusEnum {

    /**
     * Открытый
     */
    OPEN(1),

    /**
     * Предварительно закрытый
     */
    HALF_CLOSED(2),

    /**
     * Закрытый
     */
    CLOSED(3);

    /**
     * Идентификатор статуса периода
     */
    private Integer periodStatusId;


    /**
     * Конструктор
     *
     * @param periodStatusId - идентификатор статуса периода
     */
    PeriodStatusEnum(Integer periodStatusId) {
        this.periodStatusId = periodStatusId;
    }

    /**
     * Получение идентификатора статуса периода
     *
     * @return - идентификатор статуса периода
     */
    public Integer getPeriodStatusId() {
        return periodStatusId;
    }
}
