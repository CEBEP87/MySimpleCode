package ru.XXXXXXXXX.renovation.domain.entity;

import ru.XXXXXXXXX.data.jpa.entity.BaseSaveDocumentEntity;
import ru.XXXXXXXXX.dictionary.domain.entity.DictionaryElementShort;
import ru.XXXXXXXXX.renovation.domain.entity.RenovationDocument;
import ru.XXXXXXXXX.renovation.domain.entity.RenovationInfrastructure;

import java.sql.Blob;
import java.util.Date;

/**
 *
 * @author Samsonov
 *
 */
public interface HasRenovationInfrastructureEntity {

	boolean hasInfrastructure(Integer infrastructureId);

	boolean hasInfrastructure(RenovationInfrastructure infrastructure);

	RenovationInfrastructure getInfrastructure(Integer infrastructureId);

	boolean addInfrastructure(RenovationInfrastructure infrastructure);

	RenovationInfrastructure createInfrastructure(DictionaryElementShort type, String address, String name);

	boolean removeInfrastructure(RenovationInfrastructure infrastructure);
}
