package ru.XXXXXXXXX.renovation.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import ru.XXXXXXXXX.data.service.history.HistoryField;
import ru.XXXXXXXXX.data.service.history.HistoryIgnore;
import ru.XXXXXXXXX.dictionary.domain.entity.DictionaryElementShort;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Audited
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "building_parametrs", schema = "renovation")
public class BuildingParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "renovation_building_parametrs_generator")
    @SequenceGenerator(name = "renovation_building_parametrs_generator", sequenceName = "renovation.renovation_building_parametrs_generator_seq", allocationSize = 1)
    @Column(name = "id")
    private Integer id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "building_id")
    @HistoryIgnore
    private Building building;

    /**
     * Подвальных этажей
     */

    @Audited
    @Column(name = "basement_floors")
    private Integer basementFloors;
    /**
     * Высота этажа
     */

    @Audited
    @Column(name = "height_floor")
    private Integer heightFloor;
    /**
     * Подъездов
     */

    @Audited
    @Column(name = "entrances")
    private Integer entrances;
    /**
     * Квартир
     */

    @Audited
    @Column(name = "apartments")
    private Integer apartments;

    /**
     * Нежилых помещений
     */

    @Audited
    @Column(name = "not_a_living_areas")
    private Integer notALivingAreas;

    /**
     * Мусоропровод
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "garbage_chute_id")
    @Fetch(FetchMode.JOIN)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @HistoryField(title = "Мусоропровод")
    private DictionaryElementShort garbageChute;

    /**
     * Газоотведение
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gas_removal_id")
    @Fetch(FetchMode.JOIN)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @HistoryField(title = "Газоотведение")
    private DictionaryElementShort gasRemoval;

    /**
     * Электроснабжение
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "power_supply_id")
    @Fetch(FetchMode.JOIN)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @HistoryField(title = "Электроснабжение")
    private DictionaryElementShort powerSupply;

    public BuildingParameters(Integer basementFloors, Integer heightFloor, Integer entrances, Integer apartments, Integer notALivingAreas, DictionaryElementShort garbageChute, DictionaryElementShort gasRemoval, DictionaryElementShort powerSupply) {
        this.basementFloors = basementFloors;
        this.heightFloor = heightFloor;
        this.entrances = entrances;
        this.apartments = apartments;
        this.notALivingAreas = notALivingAreas;
        this.garbageChute = garbageChute;
        this.gasRemoval = gasRemoval;
        this.powerSupply = powerSupply;
    }
}
