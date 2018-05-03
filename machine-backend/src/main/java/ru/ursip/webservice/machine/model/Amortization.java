package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * This is domain for database Mongo, Amortization document
 *
 * Created by samsonov_ky on 14.09.2017.
 */
@Data
@AllArgsConstructor
public class Amortization {
    /**
     * technological break
     */
    @Field("is_technical_break")
    private Boolean isTechnicalBreak;

    /**
     * Amortization's index auto
     */
    @Field("index_amortization")
    private Double indexAmortization;

    /**
     * Amortization's index manual
     */
    @Field("index_amortization_h")
    private Double indexAmortizationH;

    /**
     * weighted average repair price
     */
    @Field("ave_price")
    private Double avePrice;

    /**
     * time_useful of machines auto
     */
    @Field("time_useful_machine")
    private Double timeUsefulMachine;
    /**
     * time_useful of machines manual
     */
    @Field("time_useful_machine_h")
    private Double timeUsefulMachineH;

    /**
     * Yearly mode work auto,
     */
    @Field("yearly_mode_work")
    private Double yearlyModeWork;

    /**
     * Yearly mode work manual
     */
    @Field("yearly_mode_work_h")
    private Double yearlyModeWorkH;

    /**
     * Days downtime weather
     */
    @Field("days_downtime_weather")
    private Long daysDowntimeWeather;

    /**
     * Days downtime service
     */
    @Field("days_downtime_service")
    private Long daysDowntimeService;

    /**
     * Days downtime relocation
     */
    @Field("days_downtime_relocation")
    private Long daysDowntimeRelocation;

    /**
     * Coefficient of change working machines
     */
    @Field("coef_change_working_machine")
    private Double coefChangeWorkingMachine;

    /**
     * Refine coefficient of yearly mode work
     */
    @Field("refine_coef_yearly_mode_work")
    private Double refineCoefYearlyModeWork;

    /**
     * Norm of amortization on reduction
     */
    @Field("norma_amortization_on_reduction")
    private Double normaAmortizationOnReduction;

    /**
     * Time useful auto  checkbox (true:percent/year  false:percent/1000km)
     */
    @Field("time_useful_auto_machine_f")
    private Boolean timeUsefulAutoMachineF;

    /**
     * Time useful auto
     */
    @Field("time_useful_auto_machine")
    private Double timeUsefulAutoMachine;

    /**
     * Time useful auto manual
     */
    @Field("time_useful_auto_machine_h")
    private Double timeUsefulAutoMachineH;

    /**
     * Norm of auto's amortization
     */
    @Field("norma_amortization_auto_machine")
    private Double normaAmortizationAutoMachine;

    /**
     * average annual mileage
     */
    @Field("middle_year_mileage")
    private Double middleYearMileage;

    /**
     *  Amortization's index sub calculation
     */
    @Field("index_amortization_sub_auto")
    private Double indexAmortizationSubAuto;
    /**
     *  Amortization's index sub calculation
     */
    @Field("index_amortization_sub_other")
    private Double indexAmortizationSubOther;

    /**
     *  Amortization's index sub calculation manual
     */
    @Field("index_amortization_sub_auto_h")
    private Double indexAmortizationSubAutoH;

    /**
     *  Amortization's index sub calculation manual
     */
    @Field("index_amortization_sub_other_h")
    private Double indexAmortizationSubOtherH;

    /**
     * Yearly mode work sub calculation
     */
    @Field("yearly_mode_work_sub_technological_break_on")
    private Double yearlyModeWorkSubTechnologicalBreakOn;

    /**
     * Yearly mode work sub calculation
     */
    @Field("yearly_mode_work_sub_technological_break_off")
    private Double yearlyModeWorkSubTechnologicalBreakOff;

    /**
     * Yearly mode work sub calculation manual
     */
    @Field("yearly_mode_work_sub_technological_break_on_h")
    private Double yearlyModeWorkSubTechnologicalBreakOnH;

    /**
     * Yearly mode work sub calculation manual
     */
    @Field("yearly_mode_work_sub_technological_break_off_h")
    private Double yearlyModeWorkSubTechnologicalBreakOffH;


}
