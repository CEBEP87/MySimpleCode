package ru.XXXXXXXXX.renovation.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Builder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Proxy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import ru.XXXXXXXXX.data.service.history.HistoryField;
import ru.XXXXXXXXX.data.service.history.HistoryIgnore;
import ru.XXXXXXXXX.dictionary.domain.entity.DictionaryElementShort;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "renovation_infrastructure", schema = "renovation")
@SequenceGenerator(name = "sq_ren_infr_id", sequenceName = "renovation.renovation_infrastructure_seq", allocationSize = 1)
@Proxy(lazy = false)
@Audited
@AttributeOverride(name = "content", column = @Column(name = "content", columnDefinition = "OID", updatable = false))
public class RenovationInfrastructure {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_ren_infr_id")
    private Integer id;

    @JsonIgnore
    @Column(name = "renovation_object_id", nullable = false, insertable = false, updatable = false)
    @HistoryIgnore
    private Integer renovationId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "renovation_object_id")
    @HistoryIgnore
    private Renovation renovation;

    /**
     * Вид обьекта
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id")
    @Fetch(FetchMode.JOIN)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @HistoryField(title = "Вид обьекта")
    private DictionaryElementShort type;

    /**
     * Адрес
     */
    @Column(name = "address")
    private String address;

    /**
     * Наименование
     */
    @Column(name = "name")
    private String name;

    public RenovationInfrastructure(DictionaryElementShort type, String address, String name) {
        this.type = type;
        this.address = address;
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.getId());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RenovationInfrastructure other = (RenovationInfrastructure) obj;
        return Objects.equals(this.getId(), other.getId());
    }
}
