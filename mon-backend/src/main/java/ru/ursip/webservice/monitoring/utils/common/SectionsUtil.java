package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.Resource;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.ResourceSection;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.service.ResourceSectionService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.service.ResourceService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Инструмент для работы с разделами
 *
 * @author samsonov
 * @since 08.06.2017
 */
@Component
public class SectionsUtil {

    /**
     * Сервис для работы с ресурсами
     */
    @Autowired
    private ResourceService resourceService;

    /**
     * Сервис для работы с разделами ресурсов
     */
    @Autowired
    private ResourceSectionService resourceSectionService;

    /**
     * Формирование списка разделов для отображения заданного списка ресурсов
     *
     * @param allSections  - список разделов
     * @param allResources - спиоск ресурсов
     * @return - список разделов для отображения заданного списка ресурсов
     */
    public List<ResourceSection> createSectionListByResourcesWithNodes(List<ResourceSection> allSections,
                                                                       List<Resource> allResources) throws Exception {
        List<ResourceSection> result = allSections.subList(0, allSections.size());
        Set<ResourceSection> setSectionsByResources = new HashSet<>();
        for (Resource currentResource : allResources) {
            if (currentResource.getResourceSection() != null) {
                setSectionsByResources.add(currentResource.getResourceSection());
            }
        }
        Set<ResourceSection> setSectionsByResourcesWithNodes = new HashSet<>();
        for (ResourceSection currentSection : setSectionsByResources) {
            ResourceSection section = currentSection;
            setSectionsByResourcesWithNodes.add(currentSection);
            while (section.getSectionRoot() != null) {
                section = resourceSectionService.get(section.getSectionRoot());
                // Если раздел уже был добавлен в искомый набор
                if (setSectionsByResourcesWithNodes.contains(section)) break;
                setSectionsByResourcesWithNodes.add(section);
            }
        }
        // Список разделов которые необходимо удалить из общего списка разделов
        List removeList = new ArrayList();
        for (ResourceSection currentResultRS : result) {
            if (!setSectionsByResourcesWithNodes.contains(currentResultRS)) {
                removeList.add(currentResultRS);
            }
        }
        result.removeAll(removeList);
        return result;
    }

    /**
     * Создание списка разделов по типам ресурсов
     *
     * @param resources - список ресурсов
     * @return - список разделов заданных ресурсов
     */
    public List<ResourceSection> createResourceSectionListByResources(List<Resource> resources) {
        List<ResourceSection> result;
        Set<ResourceSection> setSectionsByResources = new HashSet<>();
        for (Resource currentResource : resources) {
            setSectionsByResources.add(currentResource.getResourceSection());
        }
        result = new ArrayList<>(setSectionsByResources);
        return result;
    }

    /**
     * Формирования списка дочерних элементов относительно списка корневых элементов
     *
     * @param rootSections - список корневых элементов
     * @return - список дочерних элементов относительно списка корневых элементов
     */
    public List<ResourceSection> createResourceSectionListByRootSections(List<ResourceSection> rootSections) {
        List<ResourceSection> result = new ArrayList<>();
        List<ResourceSection> allSections = resourceSectionService.getAllWithIsDeleted(false);
//        Set<ResourceSection> setSectionsByResources = new HashSet<>();
        for (ResourceSection currentResourceSectionRoot : rootSections) {
            result.addAll(createResourceSectionListLikeMap(allSections, currentResourceSectionRoot));
        }
        return result;
    }

    /**
     * Формирование списка разделов (последовательно сверху вниз - дерево)
     *
     * @param allResourceSections - список всех раздело
     * @param headResourceSection - корневой раздел
     * @return - список разделов
     */
    public List<ResourceSection> createResourceSectionListLikeMap(List<ResourceSection> allResourceSections,
                                                                  ResourceSection headResourceSection) {
        List<ResourceSection> result = new ArrayList<>();
        result.add(headResourceSection);
        List<ResourceSection> currentNodes = new ArrayList<>();
        for (ResourceSection currentSection : allResourceSections) {
            if (headResourceSection.getId().equals(currentSection.getSectionRoot())) {
                currentNodes.add(currentSection);
            }

        }
        if (!currentNodes.isEmpty()) {
            for (ResourceSection node : currentNodes) {
                result.addAll(createResourceSectionListLikeMap(allResourceSections, node));
            }
        } else {
            return result;
        }
        return result;

    }
}
