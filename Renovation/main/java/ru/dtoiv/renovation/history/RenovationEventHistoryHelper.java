package ru.XXXXXXXXX.renovation.history;

import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.internal.util.Assert;
import ru.XXXXXXXXX.data.service.history.*;
import ru.XXXXXXXXX.renovation.domain.entity.Renovation;

import java.util.Objects;
import java.util.Optional;

/**
 * XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 *
 * Created by Samsonov on 21.03.2017.
 */
public class RenovationEventHistoryHelper implements HistoryHelper<Renovation> {

    @Override
    public String calculateKind(Renovation prev, Renovation next, HistoryItem historyItem) {
        Assert.isTrue(prev != null || next != null);
        if (next == null) {
            throw new RuntimeException("Удаление Renovation не реализовано");
        }
        Optional<HistoryObjectFieldGroup> hasChangesInGroups = Optional.empty();
        Optional<HistoryObjectCollection> hasChangesInCollections = Optional.empty();
        if (historyItem.getObject() != null ) {
            if (historyItem.getObject().getGroups() != null) {
                hasChangesInGroups = historyItem.getObject().getGroups().stream().filter(historyObjectFieldGroup ->
                        historyObjectFieldGroup.getFields().stream().filter(f -> !Objects.equals(f.getName(), "confStatus")).anyMatch(HistoryObjectField::getChanged)).findAny();
            }
            if (historyItem.getObject().getCollections() != null) {
                hasChangesInCollections = historyItem.getObject().getCollections()
                        .stream()
                        .filter(coll -> coll.getChangeCount() > 0 &&
                                coll.getItems().stream()
                                        .anyMatch(item -> item.getCollections() != null && item.getCollections()
                                                .stream()
                                                .anyMatch(inColl -> inColl.getChangeCount() > 0)))
                        .findAny();
            }
        }
        HistoryObjectWrapper prevWrapper = new HistoryObjectWrapper(prev);
        HistoryObjectWrapper nextWrapper = new HistoryObjectWrapper(next);

        // если изменялся статус
        final Object prevStatusId = prevWrapper.getPropertyValue("status");
        final Object nextStatusId = nextWrapper.getPropertyValue("status");
        if (ObjectUtils.notEqual(prevStatusId, nextStatusId) && !hasChangesInGroups.isPresent() && !hasChangesInCollections.isPresent()) {


            switch ((int) nextStatusId) {
                case 71086031:
                    return "Отправка на доработку";

                case 71086011:
                    return "Отправка на согласование";

                case 71085991:
                    return "Согласование";

                default:
                    throw new RuntimeException("Некорректный статус");
            }
        }

        return "Редактирование Renovation";
    }

    @Override
    public String getInitialKind() {
        return "Создание Renovation";
    }

}
