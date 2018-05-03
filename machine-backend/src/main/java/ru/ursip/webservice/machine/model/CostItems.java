package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * * This is domain for database Mongo, Cost_items document
 *
 * Created by samsonov_ky on 14.09.2017.
 */

@Data
@AllArgsConstructor
public class CostItems {
    /**
     * Amortization's properties
     */
    @Field("amortization")
    private Amortization amortization;

    /**
     * Repair's properties
     */
    @Field("repair")
    private Repair repair;

    /**
     * Wearable's properties
     */
    @Field("wearable")
    private Wearable wearable;

    /**
     * Salary'es properties
     */
    @Field("salary")
    private Salary salary;

    /**
     * Power's properties
     */
    @Field("power")
    private Power power;

    /**
     * Lubricants'es properties
     */
    @Field("lubricants")
    private Lubricants lubricants;

    /**
     * Liquid's properties
     */
    @Field("liquid")
    private Liquid liquid;

    /**
     * Relocation's properties
     */
    @Field("relocation")
    private Relocation relocation;



}
