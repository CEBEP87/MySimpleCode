package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ParserErrors
 *
 * @author Samsonov KY
 * @since 30.01.2018
 */

@Data
@AllArgsConstructor
public class AvePriceResponse implements Serializable {
    /**
     * id Period
     */
    private String id;

    /**
     * Change resources list
     */
    private List<Resource> resource;

    /**
     * size of list
     */
    private Integer sizeList;


}
