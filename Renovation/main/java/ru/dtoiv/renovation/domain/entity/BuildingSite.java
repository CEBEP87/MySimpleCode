package ru.XXXXXXXXX.renovation.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Builder;
import org.hibernate.envers.Audited;
import ru.XXXXXXXXX.dictionary.domain.entity.DictionaryElementShort;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Audited
@Entity(name = "BuildingSite")
@DiscriminatorValue("Building_Site")
public class BuildingSite extends Renovation  implements Serializable {

    @Builder
    private BuildingSite(Integer id, String uniqueId, String address, DictionaryElementShort area, String comments,
                         DictionaryElementShort status, Float completion, DictionaryElementShort rayon,
                         DictionaryElementShort okrug, LocalDateTime lastChangeDate, List<RenovationDocument> documents,
                         Integer resettlement,String causeOfConstructionCancellation, List<RenovationInfrastructure> renovationInfrastructures,String type){
        super(id, uniqueId, address, area, comments, status, completion, rayon, okrug, lastChangeDate, documents, renovationInfrastructures,type);
        this.resettlement=resettlement;
        this.causeOfConstructionCancellation=causeOfConstructionCancellation;
    }

    /**
     * Начало переселения
     */
    @Audited
    @Column(name = "start_resettlement")
    private Integer resettlement;

    /**
     * Причина отмены строительства
     */
    @Audited
    @Column(name = "cause_of_construction_cancellation")
    private String causeOfConstructionCancellation;
}
