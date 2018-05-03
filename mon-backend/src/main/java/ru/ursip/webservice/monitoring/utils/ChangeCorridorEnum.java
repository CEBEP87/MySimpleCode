package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils;

/**
 * Перечисление коридоров допустимых значений с их идентификаторами в БД
 *
 * @author samsonov
 * @since 20.06.2017
 */
public enum ChangeCorridorEnum {

    /**
     * Основной коридор допустимых значений
     */
    MAIN_CORRIDOR(1);

    /**
     * Идентификатор коридора допустимых значений
     */
    private Integer changeCorridorId;

    /**
     * Конструктор
     *
     * @param changeCorridorId - идентификатор коридора допустимых значений
     */
    ChangeCorridorEnum(Integer changeCorridorId) {
        this.changeCorridorId = changeCorridorId;
    }

    /**
     * Получение идентификатора коридора допустимых значений
     *
     * @return - идентификатор коридора допустимых значений
     */
    public Integer getChangeCorridorId() {
        return changeCorridorId;
    }
}
