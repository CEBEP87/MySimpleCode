package ru.XXXXXXXXX.renovation.domain.entity.conflicted;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RenovationConflictRelationId implements Serializable {
    Integer conflictId;
    Integer renovationId;
}
