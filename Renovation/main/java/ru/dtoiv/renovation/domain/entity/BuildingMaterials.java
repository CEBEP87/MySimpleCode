package ru.XXXXXXXXX.renovation.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import ru.XXXXXXXXX.data.service.history.HistoryIgnore;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Audited
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "building_materials", schema = "renovation")
public class BuildingMaterials {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "renovation_building_materials_generator")
    @SequenceGenerator(name = "renovation_building_materials_generator", sequenceName = "renovation.renovation_building_materials_generator_seq", allocationSize = 1)
    @Column(name = "id")
    private Integer id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "building_id")
    @Audited
    @HistoryIgnore
    private Building building;

    /**
     * Перекрытия
     */
    @Audited
    @Column(name = "ceiling")
    private String ceiling;

    /**
     * Каркасы
     */
    @Column(name = "carcass")
    private String carcass;

    /**
     * Стены
     */
    @Audited
    @Column(name = "walls")
    private String walls;

    /**
     * Фундамент
     */
    @Audited
    @Column(name = "foundation")
    private String foundation;

    public BuildingMaterials(String ceiling, String carcass, String walls, String foundation) {
        this.ceiling = ceiling;
        this.carcass = carcass;
        this.walls = walls;
        this.foundation = foundation;
    }
}
