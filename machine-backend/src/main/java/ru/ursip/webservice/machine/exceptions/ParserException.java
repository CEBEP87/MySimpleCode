package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.exceptions;

import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.ParserErrors;

import java.util.List;

/**
 * Исключение на случай не верно сформированного прайс листа
 *
 * @author samsonov KY
 * @since 30.01.2018
 */
public class ParserException extends Exception{
    /**
     * Лист ошибок
     */
    private  List<ParserErrors> errors;

        // Конструкторы, вызывающие конструкторы базового класса.

    /**
     * Конструктор
     * @param errors - ошибки
     */
    public ParserException(List<ParserErrors> errors) {
            this.errors=errors;
        }
    /**
     * Геттре
     * @return List<ParserErrors>
     */
    public  List<ParserErrors> getErrors(){return errors;}

}
