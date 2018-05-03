package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;


/**
 * * This is domain for database Mongo, Period document
 *
 * Created by samsonov_ky on 13.09.2017.
 */
@Data
@Document(collection = "Category_drivers")
@AllArgsConstructor
public class CategoryDrivers {

    /**
     * identificator
     */
    @Id
    private String id;


    /**
     * category
     */
    @Field("category")
    private Double category;

    /**
     * sections
     */
    @Field("Index_category")
    private Double indexCategory;

}
