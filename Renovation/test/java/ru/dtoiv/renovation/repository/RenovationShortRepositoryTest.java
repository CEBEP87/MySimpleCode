package ru.XXXXXXXXX.renovation.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.XXXXXXXXX.dictionary.domain.entity.DictionaryElementShort;
import ru.XXXXXXXXX.renovation.domain.entity.BuildingSite;
import ru.XXXXXXXXX.renovation.domain.entity.Renovation;
import ru.XXXXXXXXX.renovation.domain.filter.RenovationFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIn.isIn;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@Transactional
@ContextConfiguration(locations = {
        "classpath:spring/root-context.xml",
})
public class RenovationShortRepositoryTest {

    private DictionaryElementShort ПРОВЕРЕНО;
    private DictionaryElementShort НА_ПРОВЕРКЕ;
    private DictionaryElementShort В_РАБОТЕ;

    private DictionaryElementShort ВАО;
    private DictionaryElementShort ЦАО;
    private DictionaryElementShort БОГОРОДСКОЕ;
    private DictionaryElementShort АРБАТ;
    private DictionaryElementShort ЯКИМАНКА;

    private Renovation RENOVATION_ПРОВЕРЕНО;
    private Renovation RENOVATION_НА_ПРОВЕРКЕ;
    private Renovation RENOVATION_В_РАБОТЕ_БОГОР;
    private Renovation RENOVATION_В_РАБОТЕ_АРБАТ;

    @Autowired
    RenovationRepository renovationRepository;

    @Before
    public void init() {
        //renovationRepository.deleteAll();

        ПРОВЕРЕНО = DictionaryElementShort.builder().id(71085991).build();
        НА_ПРОВЕРКЕ = DictionaryElementShort.builder().id(71086011).build();
        В_РАБОТЕ = DictionaryElementShort.builder().id(71086031).build();

        ВАО = DictionaryElementShort.builder().id(3460).name("").build();
        ЦАО = DictionaryElementShort.builder().id(20647).build();
        БОГОРОДСКОЕ = DictionaryElementShort.builder().id(3513).build();
        АРБАТ = DictionaryElementShort.builder().id(3470).build();
        ЯКИМАНКА = DictionaryElementShort.builder().id(3479).build();

        RENOVATION_ПРОВЕРЕНО = BuildingSite.builder().address("testing").status(ПРОВЕРЕНО).rayon(БОГОРОДСКОЕ).okrug(ВАО).build();
        RENOVATION_НА_ПРОВЕРКЕ = BuildingSite.builder().address("testing").status(НА_ПРОВЕРКЕ).rayon(БОГОРОДСКОЕ).okrug(ВАО).build();
        RENOVATION_В_РАБОТЕ_БОГОР = BuildingSite.builder().address("testing2").status(В_РАБОТЕ).rayon(БОГОРОДСКОЕ).okrug(ВАО).build();
        RENOVATION_В_РАБОТЕ_АРБАТ = BuildingSite.builder().address("testing1").status(В_РАБОТЕ).rayon(АРБАТ).okrug(ЦАО).build();

        List<Renovation> renovations = Arrays.asList(RENOVATION_ПРОВЕРЕНО, RENOVATION_НА_ПРОВЕРКЕ, RENOVATION_В_РАБОТЕ_БОГОР, RENOVATION_В_РАБОТЕ_АРБАТ);
        renovationRepository.save(renovations);
        renovationRepository.findAll();
    }

    @Test
    public void checkStatusFiltering() {
        // Given
        RenovationFilter<Renovation> ПРОВЕРЕНО_FILTER = RenovationFilter.<Renovation>builder().search("testing").state(Collections.singleton(ПРОВЕРЕНО.getId())).build();
        RenovationFilter<Renovation> НА_ПРОВЕРКЕ_FILTER = RenovationFilter.<Renovation>builder().search("testing").state(Collections.singleton(НА_ПРОВЕРКЕ.getId())).build();
        RenovationFilter<Renovation> В_РАБОТЕ_FILTER = RenovationFilter.<Renovation>builder().search("testing").state(Collections.singleton(В_РАБОТЕ.getId())).build();


        // Then
        List<Renovation> actual_ПРОВЕРЕНО = renovationRepository.findAll(ПРОВЕРЕНО_FILTER);
        List<Renovation> actual_НА_ПРОВЕРКЕ = renovationRepository.findAll(НА_ПРОВЕРКЕ_FILTER);
        List<Renovation> actual_В_РАБОТЕ = renovationRepository.findAll(В_РАБОТЕ_FILTER);

        // Verify
        assertThat(actual_ПРОВЕРЕНО, hasSize(1));
        assertThat(actual_ПРОВЕРЕНО.get(0).getId(), is(RENOVATION_ПРОВЕРЕНО.getId()));

        assertThat(actual_НА_ПРОВЕРКЕ, hasSize(1));
        assertThat(actual_НА_ПРОВЕРКЕ.get(0).getId(), is(RENOVATION_НА_ПРОВЕРКЕ.getId()));

        assertThat(actual_В_РАБОТЕ, hasSize(2));
        assertThat(actual_В_РАБОТЕ, containsInAnyOrder(RENOVATION_В_РАБОТЕ_БОГОР, RENOVATION_В_РАБОТЕ_АРБАТ));
    }

    @Test
    public void checkAllStatusFiltering() {
        // Given
        RenovationFilter<Renovation> ALL_STATUS_FILTER = RenovationFilter.<Renovation>builder()
                .search("testing").state(new HashSet<>(Arrays.asList(ПРОВЕРЕНО.getId(), НА_ПРОВЕРКЕ.getId(), В_РАБОТЕ.getId()))).build();

        // Then
        List<Renovation> actual = renovationRepository.findAll(ALL_STATUS_FILTER);

        // Verify
        assertThat(actual, hasSize(4));
        assertThat(RENOVATION_В_РАБОТЕ_БОГОР, isIn(actual));
        assertThat(RENOVATION_В_РАБОТЕ_АРБАТ, isIn(actual));
        assertThat(RENOVATION_НА_ПРОВЕРКЕ, isIn(actual));
        assertThat(RENOVATION_ПРОВЕРЕНО, isIn(actual));
    }

    @Test
    public void checkFilteringByЦАО() {
        System.out.println("size = " + renovationRepository.findAll().size());
        // Given
        RenovationFilter<Renovation> ЦАО_FILTER = RenovationFilter.<Renovation>builder()
                .search("testing")
                .okrug(Collections.singleton(АРБАТ.getId()))
                .state(new HashSet<>(Arrays.asList(ПРОВЕРЕНО.getId(), НА_ПРОВЕРКЕ.getId(), В_РАБОТЕ.getId()))).build();

        // Then
        List<Renovation> actual = renovationRepository.findAll(ЦАО_FILTER);

        // Verify
        assertThat(actual, hasSize(1));
        assertThat(RENOVATION_В_РАБОТЕ_АРБАТ, isIn(actual));
    }

    @Test
    public void checkSortingByAreaName() {
        RenovationFilter<Renovation> SORT_BY_NAME = RenovationFilter.<Renovation>builder()
                .state(new HashSet<>(Arrays.asList(В_РАБОТЕ.getId())))
                .search("testing")
                .sort(Arrays.asList("areaName")).build();

        // Then
        List<Renovation> actual = renovationRepository.findAll(SORT_BY_NAME, SORT_BY_NAME.getPageRequest()).getContent();

        assertThat(actual.get(0), is(RENOVATION_В_РАБОТЕ_БОГОР));
        assertThat(actual.get(1), is(RENOVATION_В_РАБОТЕ_АРБАТ));
    }

}