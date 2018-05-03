package ru.XXXXXXXXX.renovation.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Builder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import ru.XXXXXXXXX.data.service.history.HistoryField;
import ru.XXXXXXXXX.dictionary.domain.entity.DictionaryElementShort;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Audited
@Entity(name = "Building")
@DiscriminatorValue("Building")
public class Building extends Renovation  {
    @Builder
    private Building(Integer id, String uniqueId, String address, DictionaryElementShort area, String comments,
                     DictionaryElementShort status, Float completion, DictionaryElementShort rayon,
                     DictionaryElementShort okrug, LocalDateTime lastChangeDate, List<RenovationDocument> documents,
                     Integer postCode, String cadastralNumber, String managementCompany, DictionaryElementShort state,
                     String overhaulFund, DictionaryElementShort typicalSeries, Integer constructionYear, String purpose,
                     String homeType, String category, Integer votePositive, Integer voteNegative, List<RenovationInfrastructure> renovationInfrastructures,
                     BuildingMaterials buildingMaterials,BuildingAreas buildingAreas,BuildingParameters buildingParameters,String type) {
        super(id, uniqueId, address, area, comments, status, completion, rayon, okrug, lastChangeDate, documents, renovationInfrastructures,type);
        this.postCode = postCode;
        this.cadastralNumber = cadastralNumber;
        this.managementCompany = managementCompany;
        this.state = state;
        this.overhaulFund = overhaulFund;
        this.typicalSeries = typicalSeries;
        this.constructionYear = constructionYear;
        this.purpose = purpose;
        this.homeType = homeType;
        this.category = category;
        this.votePositive = votePositive;
        this.voteNegative = voteNegative;
        this.buildingMaterials=buildingMaterials;
        this.buildingAreas=buildingAreas;
        this.buildingParameters=buildingParameters;
    }

    /**
     * Почтовый индекс
     */
    @Audited
    @Column(name = "post_code")
    private Integer postCode;

    /**
     * Кадастровый номер дома
     */
    @Audited
    @Column(name = "сadastral_number")
    private String cadastralNumber;

    /**
     * Управляющая компания
     */
    @Audited
    @Column(name = "management_company")
    private String managementCompany;

    /**
     * Состояние. Значения справочника: - Исправное; - Аварийное.
     */
    @NotAudited
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "state_id")
    @Fetch(FetchMode.JOIN)
    @HistoryField(title = "Статус")
    private DictionaryElementShort state;

    /**
     * Накопление средств на капремонт
     */
    @Audited
    @Column(name = "overhaul_fund")
    private String overhaulFund;

    /**
     * Типовая серия
     */
    @NotAudited
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "typical_series_id")
    @Fetch(FetchMode.JOIN)
    @HistoryField(title = "Типовая серия")
    private DictionaryElementShort typicalSeries;

    /**
     * Материалы
     */
    @Audited
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.JOIN)
    @HistoryField(title = "Материалы")
    private BuildingMaterials buildingMaterials;

    /**
     * Площадь
     */
    @Audited
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.JOIN)
    @HistoryField(title = "Площадь")
    private BuildingAreas buildingAreas;

    /**
     * Параметры
     */
    @Audited
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "building",cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.JOIN)
    @HistoryField(title = "Параметры")
    private BuildingParameters buildingParameters;


    /**
     * Год постройки
     */
    @Audited
    @Column(name = "construction_year")
    private Integer constructionYear;

    /**
     * Назначение
     */
    @Audited
    @Column(name = "purpose")
    private String purpose;

    /**
     * Тип дома
     */
    @Audited
    @Column(name = "home_type")
    private String homeType;

    /**
     * Категория
     */
    @Audited
    @Column(name = "category")
    private String category;

    /**
     * Проголосовали «за»
     */
    @Audited
    @Column(name = "vote_positive")
    private Integer votePositive;

    /**
     * Проголосовали «против»
     */
    @Audited
    @Column(name = "vote_negative")
    private Integer voteNegative;

}
