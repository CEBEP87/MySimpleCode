package ru.XXXXXXXXX.renovation.utils;


import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.XXXXXXXXX.notification.utils.message.MessageDescription;

@AllArgsConstructor
@Getter
public enum MessageRenovationDescription implements MessageDescription {

    TO_AGREEMENT(1,71086011,71080432,71411400,"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",null),
    TO_CHECK(2,71086031,71080432,71411410,"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",null),
    CONFIRMED(3,71085991,71080432,71411420,"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",null);

    Integer id;
    Integer status;
    Integer messageType;
    Integer contentType;
    String contentForMail;
    Integer result;

    // 71086031, В работе
    // 71086011, На проверке
    // 71085991, Проверено

}
