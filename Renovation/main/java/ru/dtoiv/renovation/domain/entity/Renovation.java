package ru.XXXXXXXXX.renovation.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import ru.XXXXXXXXX.common.util.LocalDateTimeDeserializer;
import ru.XXXXXXXXX.common.util.LocalDateTimeSerializer;
import ru.XXXXXXXXX.data.jpa.entity.HasBaseSaveDocumentEntity;
import ru.XXXXXXXXX.data.service.history.HistoryField;
import ru.XXXXXXXXX.data.service.history.HistoryIgnore;
import ru.XXXXXXXXX.data.service.history.HistoryRoot;
import ru.XXXXXXXXX.dictionary.domain.entity.DictionaryElementShort;
import ru.XXXXXXXXX.renovation.history.RenovationEventHistoryHelper;

import javax.persistence.*;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Audited
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Renovation_Type")
@HistoryRoot(defaultGroup = "Общая информация", helper = RenovationEventHistoryHelper.class)
@Table(name = "renovation_object", schema = "renovation")
public class Renovation implements HasBaseSaveDocumentEntity<RenovationDocument>, HasRenovationInfrastructureEntity {

    @Id
    @NotAudited
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "renovation_object_generator")
    @SequenceGenerator(name = "renovation_object_generator", sequenceName = "renovation.renovation_object_seq", allocationSize = 1)
    @Column(name = "id")
    private Integer id;

    @HistoryIgnore
    @NotAudited
    @Column(name = "Renovation_Type", insertable = false, updatable = false)
    private String type;

    @Audited
    @Column(name = "unique_id", length = 15)
    private String uniqueId;

    @Transient
    @NotAudited
    private DictionaryElementShort area;

    /**
     * Адрес
     */
    @Audited
    @Column(name = "address")
    private String address;

    /**
     * Комментарий
     */
    @Audited()
    @Column(name = "comment")
    private String comment;

    /**
     * Статус объекта реновации
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    @Fetch(FetchMode.JOIN)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @HistoryField(title = "Статус объекта реновации", order = "3")
    private DictionaryElementShort status;

    /**
     * Индикатор заполнения карточки объекта
     */
    @NotAudited
    @Column(name = "fullness")
    private Float completion;

    /**
     * Район
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rayon_id", unique = true)
    @Fetch(FetchMode.JOIN)
    @NotAudited
    private DictionaryElementShort district;

    /**
     * Округ
     */
    @ManyToOne
    @JoinColumn(name = "okrug_id")
    @Fetch(FetchMode.JOIN)
    @NotAudited
    private DictionaryElementShort okrug;

    @Audited
    @Column(name = "last_change_date", columnDefinition = "timestamp without time zone")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastChangeDate;

    @ManyToOne
    @JoinColumn(name = "renovation_status_id")
    @Fetch(FetchMode.JOIN)
    @NotAudited
    private DictionaryElementShort renovationStatus;

    @OneToMany(mappedBy = "renovation", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @NotAudited
    @OrderBy("creationDate asc")
    private List<RenovationDocument> documents = new ArrayList<>();

    @ManyToMany(mappedBy = "renovation", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("name asc")
    @NotAudited
    private List<RenovationInfrastructure> renovationInfrastructures = new ArrayList<>();

    @Column(name = "point_lng")
    @HistoryIgnore
    private Double pointLng;

    @Column(name = "point_lat")
    @HistoryIgnore
    private Double pointLat;

    @NotAudited
    @Formula(value = " (select regexp_replace(replace(replace(replace(lower(de.name), 'поселение', ''),'район',''),'\"',''), '^\\s+', '')  from dict_elements de where de.id = rayon_id) ")
    private String districtNameShort;

    @NotAudited
    @Formula(value = " (select sp.okrug_shortname from spr_prefecture sp where sp.okrug_id = (select de.parent_id from dict_elements de where de.id = rayon_id)) ")
    private String areaName;

    public Renovation(Integer id, String uniqueId, String address, DictionaryElementShort area, String comment, DictionaryElementShort status,
                      Float completion, DictionaryElementShort district, DictionaryElementShort okrug,
                      LocalDateTime lastChangeDate, List<RenovationDocument> documents, List<RenovationInfrastructure> renovationInfrastructures,String type) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.area = area;
        this.type = type;
        this.address = address;
        this.comment = comment;
        this.status = status;
        this.completion = completion;
        this.district = district;
        this.okrug = okrug;
        this.lastChangeDate = lastChangeDate;
        this.documents = new ArrayList<>();
        setDocuments(documents);
        this.renovationInfrastructures = new ArrayList<>();
        setRenovationInfrastructures(renovationInfrastructures);
    }


    /* HasBaseSaveDocumentEntity<RenovationDocument> */
    @Override
    public boolean hasDocument(Integer documentId) {
        return false;
    }

    public void setDocuments(List<RenovationDocument> documents) {
        this.documents.clear();
        if (documents != null) if (documents.size() > 0)
            this.documents.addAll(documents);
    }

    @Override
    public RenovationDocument getDocument(Integer documentId) {
        if (CollectionUtils.isEmpty(documents)) {
            return null;
        }

        return documents.stream().filter(document -> document.getId().equals(documentId)).findFirst().orElse(null);
    }

    @Override
    public boolean hasDocument(RenovationDocument document) {
        return document != null && getDocument(document.getId()) != null;
    }

    public void setRenovationInfrastructures(List<RenovationInfrastructure> renovationInfrastructures) {
        this.renovationInfrastructures.clear();
        if (renovationInfrastructures != null) if (renovationInfrastructures.size() > 0)
            this.renovationInfrastructures.addAll(renovationInfrastructures);
    }

    @Override
    public RenovationDocument createDocument(String name, String description, Integer fileLength, Date creationDate, String type, String contentType, Blob content) {
        return new RenovationDocument(name, description, fileLength, creationDate, type, contentType, content, getId());
    }

    @Override
    public boolean addDocument(RenovationDocument document) {
        if (hasDocument(document)) {
            return true;
        }
        if (document == null) {
            return false;
        }
        if (!this.getDocuments().add(document)) {
            return false;
        }
        document.setRenovationId(this.getId());

        return true;
    }

    @Override
    public boolean removeDocument(RenovationDocument document) {
        if (document == null) {
            return false;
        }
        if (!hasDocument(document)) {
            return true;
        }
        if (!getDocuments().remove(document)) {
            return false;
        }
        document.setRenovation(null);

        return true;
    }


    /* HasRenovationInfrastructureEntity */
    @Override
    public boolean hasInfrastructure(Integer infrastructureId) {
        return getInfrastructure(infrastructureId) != null;
    }

    public boolean hasInfrastructure(RenovationInfrastructure infrastructure) {
        if (CollectionUtils.isEmpty(renovationInfrastructures)) {
            return false;
        }
        if (renovationInfrastructures.contains(infrastructure)) {
            return true;
        }

        return false;
    }

    @Override
    public RenovationInfrastructure getInfrastructure(Integer infrastructureId) {
        if (CollectionUtils.isEmpty(renovationInfrastructures)) {
            return null;
        }

        return renovationInfrastructures.stream().filter(renovationInfrastructures -> renovationInfrastructures.getId().equals(infrastructureId)).findFirst().orElse(null);
    }

    @Override
    public boolean addInfrastructure(RenovationInfrastructure infrastructure) {
        if (hasInfrastructure(infrastructure)) {
            return true;
        }
        if (infrastructure == null) {
            return false;
        }
        if (!this.getRenovationInfrastructures().add(infrastructure)) {
            return false;
        }
        infrastructure.setRenovationId(this.getId());

        return true;
    }

    @Override
    public RenovationInfrastructure createInfrastructure(DictionaryElementShort type, String address, String name) {
        return new RenovationInfrastructure(type, address, name);
    }

    @Override
    public boolean removeInfrastructure(RenovationInfrastructure infrastructure) {
        if (infrastructure == null) {
            return false;
        }
        if (!hasInfrastructure(infrastructure)) {
            return true;
        }
        if (!getRenovationInfrastructures().remove(infrastructure)) {
            return false;
        }
        infrastructure.setRenovation(null);

        return true;
    }
}
