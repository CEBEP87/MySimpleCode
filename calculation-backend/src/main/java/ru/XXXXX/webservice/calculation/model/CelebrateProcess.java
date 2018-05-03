package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

/**
 * * This is domain for database Mongo, Celebrate_process collection
 * <p>
 * Created by samsonov_ky on 13.11.2017.
 */
@Data
@Document(collection = "Celebrate_process")
public class CelebrateProcess {

    /**
     * ObjectName
     */
    final private String _typeObject = "celebrate_process";
    /**
     * identificator
     */
    @Id
    private String id;
    /**
     * sotrudnik's agree
     */
    @Field("is_sotrudnik")
    private Boolean isSotrudnik;

    /**
     * last id
     */
    @Field("last_id")
    private String lastId;
    /**
     * chief's agree
     */
    @Field("is_chief")
    private Boolean isChief;

    /**
     * leader's agree
     */
    @Field("is_leader")
    private Boolean isLeader;

    /**
     * leader's remark
     */
    @Field("remark_leader")
    private String remarkLeader;

    /**
     * chief's remark
     */
    @Field("remark_chief")
    private String remarkChief;
    /**
     * identificator of period
     */
    @Field("id_period")
    private String idPeriod;

    /**
     * identificator of group
     */
    @Field("id_group")
    private String idGroup;

    /**
     * pressmark
     */
    @Field("pressmark")
    @TextIndexed
    private String pressmark;

    /**
     * Title
     */
    @Field("Title")
    @TextIndexed
    private String title;

    /**
     * Action
     */
    @Field("Action")
    private String action;

    /**
     * Status
     */
    @Field("Status")
    private String status;


    /**
     * Unit of measure
     */
    @Field("id_Unit_of_measure")
    private String idUnitOfMeasure;

    /**
     * Grounding
     */
    @Field("Grounding")
    private String grounding;

    /**
     * Fixed time
     */
    @Field("Fixed_time")
    private Double fixedTime;
    /**
     * Cost resource base
     */
    @Field("Cost_resource_base")
    private Double costResourceBase;

    /**
     * Rate machine previous
     */
    @Field("Rate_material_previous")
    private Double rateMaterialPrevious;

    /**
     * Rate machine
     */
    @Field("Rate_material")
    private Double rateMaterial;
    /**
     * Rate machine
     */
    @Field("Operation_of_machines_base")
    private Double operationOfMachinesBase;

    /**
     * Salary drivers base
     */
    @Field("Salary_drivers_base")
    private Double salaryDriversBase;

    /**
     * Salary  base
     */
    @Field("Salary_base")
    private Double salaryBase;
    /**
     * Rate machine previous
     */
    @Field("Rate_machine_previous")
    private Double rateMachinePrevious;

    /**
     * Rate machine
     */
    @Field("Rate_machine")
    private Double rateMachine;

    /**
     * List of activities
     */
    @Field("List_of_activities")
    private List<ListOfActivities> listOfActivities = new ArrayList<>();

    /**
     * List od material
     */
    @Field("Material")
    private List<Materials> materials = new ArrayList<>();

    /**
     * List od Machine
     */
    @Field("Machine")
    private List<Machines> machines = new ArrayList<>();

    /**
     * Unaccounteds
     */
    @Field("unaccounted")
    private List<Unaccounteds> unaccounteds = new ArrayList<>();


    /**
     * id_pressmark_Overhead_Profit
     */
    @Field("id_pressmark_Overhead_Profit")
    private String idPressmarkOverheadProfit;

    /**
     * id_pressmark_Winter_rise_list
     */
    @Field("id_pressmark_Winter_rise_list")
    private String idPressmarkWinterRiseList;

}
