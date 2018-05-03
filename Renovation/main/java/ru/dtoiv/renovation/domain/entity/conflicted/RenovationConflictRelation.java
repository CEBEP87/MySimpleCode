package ru.XXXXXXXXX.renovation.domain.entity.conflicted;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import ru.XXXXXXXXX.data.jpa.entity.conflict.ConflictShort;
import ru.XXXXXXXXX.data.jpa.entity.passport.DistrictPassport;
import ru.XXXXXXXXX.renovation.domain.entity.Renovation;
import ru.XXXXXXXXX.renovation.domain.entity.conflicted.RenovationConflictRelationId;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode(exclude = {"renovation", "conflict"})
@Audited
@Entity
@Table(name = "renovation_object_conflict", schema = "renovation")
@NoArgsConstructor
public class RenovationConflictRelation implements Serializable {

    @EmbeddedId
    private RenovationConflictRelationId id;

    @JoinColumn(name = "conflict_id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @NotAudited
    private ConflictShort conflict;

    @JoinColumn(name = "renovation_id", insertable = false, updatable = false)
    @ManyToOne
    @NotAudited
    private Renovation renovation;

    public RenovationConflictRelation(ConflictShort conflict, Renovation renovation) {
        id = new RenovationConflictRelationId(conflict.getId(), renovation.getId());
        this.conflict = conflict;
        this.renovation = renovation;
    }

}
