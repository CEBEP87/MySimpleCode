package ru.XXXXXXXXX.renovation.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import ru.XXXXXXXXX.data.service.history.HistoryIgnore;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Audited
@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "building_areas", schema = "renovation")
public class BuildingAreas {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "renovation_building_areas_generator")
    @SequenceGenerator(name = "renovation_building_areas_generator", sequenceName = "renovation.renovation_building_areas_generator_seq", allocationSize = 1)
    @Column(name = "id")
    private Integer id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "building_id")
    @HistoryIgnore
    private Building building;

    /**
     * Общая площадь
     */

    @Audited
    @Column(name = "total_area")
    private Double totalArea;

    /**
     * Жилая площадь
     */

    @Audited
    @Column(name = "living_area")
    private Double livingArea;

    /**
     * Нежилая площадь
     */

    @Audited
    @Column(name = "not_a_living_area")
    private Double notALivingArea;

    public BuildingAreas(Double totalArea, Double livingArea, Double notALivingArea) {
        this.totalArea = totalArea;
        this.livingArea = livingArea;
        this.notALivingArea = notALivingArea;
    }
}
