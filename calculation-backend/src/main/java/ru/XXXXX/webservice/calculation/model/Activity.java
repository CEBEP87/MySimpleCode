package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * * This is domain for database Mongo, Activity collection
 * <p>
 * Created by samsonov_ky on 13.11.2017.
 */
@Data
@Document(collection = "Activity")
@AllArgsConstructor
public class Activity {

    /**
     * ObjectName
     */
    final private String _typeObject = "activity";
    /**
     * identificator
     */
    @Id
    private String id;

    /**
     * Code
     */
    @Field("Code")
    private String code;

    /**
     * Title
     */
    @Field("Title")
    private String title;

    /**
     * Grounding
     */
    @Field("Grounding")
    private String grounding;

}
