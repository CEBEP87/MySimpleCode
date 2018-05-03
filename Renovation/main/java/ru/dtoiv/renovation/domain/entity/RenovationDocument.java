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
import ru.XXXXXXXXX.data.jpa.entity.BaseSaveDocumentEntity;
import ru.XXXXXXXXX.data.service.history.HistoryIgnore;

import javax.persistence.*;
import java.sql.Blob;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "renovation_object_document", schema = "renovation")
@SequenceGenerator(name = "sq_ren_obj_id", sequenceName = "sq_renovation_object_id", allocationSize = 1)
@Proxy(lazy = false)
@Audited
@AttributeOverride(name = "content", column = @Column(name = "content", columnDefinition = "OID", updatable = false))
public class RenovationDocument extends BaseSaveDocumentEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_ren_obj_id")
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

    RenovationDocument(String name, String description, Integer fileLength, Date creationDate, String type, String contentType, Blob content, Integer renovationId) {
        super(name, description, fileLength, creationDate, type, contentType, content);
        this.renovationId = renovationId;
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
        final RenovationDocument other = (RenovationDocument) obj;
        return Objects.equals(this.getId(), other.getId());
    }
}
