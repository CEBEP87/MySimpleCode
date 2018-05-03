package ru.XXXXXXXXX.renovation.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import ru.XXXXXXXXX.common.util.LocalDateTimeDeserializer;
import ru.XXXXXXXXX.common.util.LocalDateTimeSerializer;
import ru.XXXXXXXXX.data.service.history.HistoryField;
import ru.XXXXXXXXX.data.service.history.HistoryIgnore;
import ru.XXXXXXXXX.dictionary.domain.entity.DictionaryElementShort;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Renovation_Type")
@Table(name = "renovation_object", schema = "renovation")
public class RenovationShort {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @HistoryIgnore
    @NotAudited
    @Column(name = "Renovation_Type", insertable = false, updatable = false)
    private String type;

    @HistoryField(title = "Номер карточки объекта реновации", order = "0")
    @Column(name = "unique_id", length = 15)
    private String uniqueId;

    @Transient
    @HistoryIgnore
    private DictionaryElementShort area;

    /**
     * Адрес
     */
    @Column(name = "address")
    private String address;

    /**
     * Статус объекта реновации
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    @Fetch(FetchMode.JOIN)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @HistoryField(title = "Статус")
    private DictionaryElementShort status;

    /**
     * Индикатор заполнения карточки объекта
     */
    @HistoryIgnore
    @Column(name = "fullness")
    private Float completion;

    /**
     * Район
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rayon_id", unique = true)
    @Fetch(FetchMode.JOIN)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private DictionaryElementShort district;

    /**
     * Округ
     */
    @ManyToOne
    @JoinColumn(name = "okrug_id")
    @Fetch(FetchMode.JOIN)
    @HistoryField(title = "Район", order = "14")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private DictionaryElementShort okrug;


    @Column(name = "last_change_date", columnDefinition = "timestamp without time zone")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastChangeDate;

    @NotAudited
    @Formula(value = " (select regexp_replace(replace(replace(replace(lower(de.name), 'поселение', ''),'район',''),'\"',''), '^\\s+', '')  from dict_elements de where de.id = rayon_id) " )
    private String districtName;

    @NotAudited
    @Formula(value = " (select sp.okrug_shortname from spr_prefecture sp where sp.okrug_id = (select de.parent_id from dict_elements de where de.id = rayon_id)) ")
    private String areaName;
}
