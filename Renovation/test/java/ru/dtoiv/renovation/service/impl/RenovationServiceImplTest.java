package ru.XXXXXXXXX.renovation.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.XXXXXXXXX.dictionary.domain.entity.DictionaryElementShort;
import ru.XXXXXXXXX.renovation.domain.dto.FilterRenovationDto;
import ru.XXXXXXXXX.renovation.domain.entity.*;
import ru.XXXXXXXXX.renovation.domain.filter.RenovationFilter;
import ru.XXXXXXXXX.renovation.repository.RenovationRepository;
import ru.XXXXXXXXX.renovation.service.RenovationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.XXXXXXXXX.user.domain.entity.UserEntity;
import ru.XXXXXXXXX.user.service.UserService;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring/root-context.xml",
        "classpath:spring/servlet-context.xml"
})
@WebAppConfiguration
@Transactional
public class RenovationServiceImplTest {
    private int iteratorTest;
    private DictionaryElementShort statusInWork;

    private DictionaryElementShort okrugCAO;
    private DictionaryElementShort rayonArbat;
    private DictionaryElementShort rayonBibirevo;
    private Building renovationArbat;
    private BuildingSite renovationBibirevo;
    private Integer renovationArbatId;
    private Integer renovationBibirevoId;
    private Integer infrastructureMedicId;
    private Integer infrastructureClubId;

    private Integer materialsId;
    private Integer areasId;
    private Integer perametersId;

    private RenovationInfrastructure renovationInfrastructureMedic;
    private RenovationInfrastructure renovationInfrastructureClub;
    private DictionaryElementShort typeInfrastructure;

    private BuildingMaterials buildingMaterials;
    private BuildingAreas buildingAreas;
    private BuildingParameters buildingParameters;
    private MockMvc mockMvc;

    @Autowired
    RenovationRepository renovationRepository;

    @Autowired
    RenovationService renovationService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserService userService;


    @Before
    public void setup() {

        initDB();
        buildSecurityContext();

        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(renovationService).build();

    }

    private void initDB() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        statusInWork = DictionaryElementShort.builder().id(71086031).name("В работе").build();
        okrugCAO = DictionaryElementShort.builder().id(20647).build();

        rayonArbat = DictionaryElementShort.builder().id(3470).build();
        rayonBibirevo = DictionaryElementShort.builder().id(3499).build();

        typeInfrastructure = DictionaryElementShort.builder().id(71088771).name("Образование и наука").build();
        renovationInfrastructureMedic = RenovationInfrastructure.builder().name("test1").address("soso").type(typeInfrastructure).build();
        renovationInfrastructureClub = RenovationInfrastructure.builder().name("test2").address("soso2").type(typeInfrastructure).build();
        List<RenovationInfrastructure> renovationInfrastructures = new ArrayList<>();
        renovationInfrastructures.add(renovationInfrastructureMedic);
        renovationInfrastructures.add(renovationInfrastructureClub);

        buildingMaterials = new BuildingMaterials("test1", "test2", "test3", "test4");
        buildingAreas = new BuildingAreas(123.22, 123.22, 123.22);
        DictionaryElementShort garbageChute = DictionaryElementShort.builder().id(71121143).name("да").build();
        buildingParameters = new BuildingParameters(1, 1, 1, 1, 1, garbageChute, garbageChute, garbageChute);

        renovationArbat = Building.builder().type("Building").status(statusInWork).rayon(rayonArbat).okrug(okrugCAO).uniqueId("00-11-121").renovationInfrastructures(renovationInfrastructures).buildingMaterials(buildingMaterials).buildingAreas(buildingAreas).buildingParameters(buildingParameters).address("1-я Сиблова улица, 96").build();
        renovationBibirevo = BuildingSite.builder().type("Building_Site").status(statusInWork).rayon(rayonBibirevo).okrug(okrugCAO).uniqueId("00-11-122").renovationInfrastructures(renovationInfrastructures).address("1-я Сиблова улица, 97").build();

        Building arbat = renovationRepository.save(renovationArbat);
        BuildingSite bibirevo = renovationRepository.save(renovationBibirevo);

        infrastructureMedicId = arbat.getRenovationInfrastructures().get(0).getId();
        infrastructureClubId = arbat.getRenovationInfrastructures().get(1).getId();
        renovationArbatId = arbat.getId();
        materialsId = arbat.getBuildingMaterials().getId();
        areasId = arbat.getBuildingAreas().getId();
        perametersId = arbat.getBuildingParameters().getId();
        renovationBibirevoId = bibirevo.getId();
        databasePopulator.execute(dataSource);
    }

    private void buildSecurityContext() {
        UserEntity user = userService.loadUserByUsername("admin_user");
        Authentication authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authToken);
        SecurityContextHolder.setContext(securityContext);
    }

    private static List<Object[]> testDataByFindByFilter() {
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[]{0, 10, "Сиблова", 2});
        data.add(new Object[]{0, 10, "1-я Сиблова улица, 97", 1});
        return data;
    }

    private List<Object[]> testDataByFindRenovation() {
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[]{renovationArbatId, "00-11-121"});
        data.add(new Object[]{renovationBibirevoId, "00-11-122"});
        return data;
    }

    private List<Object[]> testDataByUpdateBuilding() {
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[]{Building.builder().id(renovationArbatId).address("Change adress")
                .uniqueId("00-11-121")
                .type("Building")
                .rayon(DictionaryElementShort.builder().id(3460).name("Восточный административный округ").build())
                .okrug(DictionaryElementShort.builder().id(3513).name("район Богородское").parentKey(3460).build())
                .lastChangeDate(LocalDateTime.parse("2018-03-09T23:46:06"))
                .documents(new ArrayList<>())
                .postCode(111222).renovationInfrastructures(new ArrayList<>())
                .cadastralNumber("№233987239238")
                .managementCompany("Gods")
                .state(DictionaryElementShort.builder().id(978689).name("Аварийное").build())
                .buildingMaterials(new BuildingMaterials("test1", "test2", "test3", "test4"))
                .buildingAreas(new BuildingAreas(123.22, 123.22, 123.22))
                .buildingParameters(new BuildingParameters(1, 1, 1, 1, 1, DictionaryElementShort.builder().id(71121143).name("Да").build(), DictionaryElementShort.builder().id(71121143).name("Да").build(), DictionaryElementShort.builder().id(71121143).name("Да").build()))
                .overhaulFund("Gods")
                .typicalSeries(DictionaryElementShort.builder().id(72083100).name("25").build())
                .constructionYear(2014)
                .purpose("naznachenie")
                .homeType("stone")
                .category("one")
                .votePositive(312)
                .voteNegative(324)
                .comments("some comment").build(), "Change adress"});
        return data;
    }

    private List<Object[]> testDataByUpdateBuildingSite() {
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[]{BuildingSite.builder().id(renovationBibirevoId).address("Zvezdova4")
                .uniqueId("00-11-122")
                .type("Building_Site")
                .rayon(DictionaryElementShort.builder().id(3460).name("Восточный административный округ").build())
                .okrug(DictionaryElementShort.builder().id(3513).name("район Богородское").parentKey(3460).build())
                .lastChangeDate(LocalDateTime.parse("2018-03-09T23:46:06"))
                .documents(new ArrayList<>())
                .renovationInfrastructures(new ArrayList<>())
                .comments("bad")
                .causeOfConstructionCancellation("bad")
                .resettlement(2019).build(), "Zvezdova4"});
        return data;
    }

    private List<Object[]> testDataByUpdateRenovation() {
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[]{BuildingSite.builder().id(renovationBibirevoId).address("Zvezdova4")
                .uniqueId("00-11-122")
                .type("Building_Site")
                .status(DictionaryElementShort.builder().id(71086031).name("В работе").build())
                .rayon(DictionaryElementShort.builder().id(3460).name("Восточный административный округ").build())
                .okrug(DictionaryElementShort.builder().id(3513).name("район Богородское").parentKey(3460).build())
                .lastChangeDate(LocalDateTime.parse("2018-03-09T23:46:06"))
                .documents(new ArrayList<>())
                .renovationInfrastructures(new ArrayList<>())
                .comments("bad")
                .causeOfConstructionCancellation("bad")
                .resettlement(2019).build(), "Zvezdova4"});

        data.add(new Object[]{Building.builder().id(renovationArbatId).address("Change adress")
                .uniqueId("00-11-121")
                .type("Building")
                .rayon(DictionaryElementShort.builder().id(3460).name("Восточный административный округ").build())
                .okrug(DictionaryElementShort.builder().id(3513).name("район Богородское").parentKey(3460).build())
                .lastChangeDate(LocalDateTime.parse("2018-03-09T23:46:06"))
                .documents(new ArrayList<>())
                .postCode(111222).renovationInfrastructures(new ArrayList<>())
                .cadastralNumber("№233987239238")
                .managementCompany("Gods")
                .state(DictionaryElementShort.builder().id(978689).name("Аварийное").build())
                .buildingMaterials(new BuildingMaterials("test1", "test2", "test3", "test4"))
                .buildingAreas(new BuildingAreas(123.22, 123.22, 123.22))
                .buildingParameters(new BuildingParameters(1, 1, 1, 1, 1, DictionaryElementShort.builder().id(71121143).name("Да").build(), DictionaryElementShort.builder().id(71121143).name("Да").build(), DictionaryElementShort.builder().id(71121143).name("Да").build()))
                .overhaulFund("Gods")
                .typicalSeries(DictionaryElementShort.builder().id(72083100).name("25").build())
                .constructionYear(2014)
                .purpose("naznachenie")
                .homeType("stone")
                .category("one")
                .votePositive(312)
                .voteNegative(324)
                .comments("some comment").build(), "Change adress"});
        return data;
    }

    private List<Object[]> testDataByPostBuilding() {
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[]{Building.builder().address("Change adress")
                .uniqueId("00-11-121")
                .type("Building")
                .rayon(DictionaryElementShort.builder().id(3460).name("Восточный административный округ").build())
                .okrug(DictionaryElementShort.builder().id(3513).name("район Богородское").parentKey(3460).build())
                .lastChangeDate(LocalDateTime.parse("2018-03-09T23:41:06"))
                .documents(new ArrayList<>())
                .postCode(111222).renovationInfrastructures(new ArrayList<>())
                .cadastralNumber("№233987239238")
                .managementCompany("Gods")
                .state(DictionaryElementShort.builder().id(978689).name("Аварийное").build())
                .buildingMaterials(new BuildingMaterials("test1", "test2", "test3", "test4"))
                .buildingAreas(new BuildingAreas(123.22, 123.22, 123.22))
                .buildingParameters(new BuildingParameters(1, 1, 1, 1, 1, DictionaryElementShort.builder().id(71121143).name("Да").build(), DictionaryElementShort.builder().id(71121143).name("Да").build(), DictionaryElementShort.builder().id(71121143).name("Да").build()))
                .overhaulFund("Gods")
                .typicalSeries(DictionaryElementShort.builder().id(72083100).name("25").build())
                .constructionYear(2014)
                .purpose("naznachenie")
                .homeType("stone")
                .category("one")
                .votePositive(312)
                .voteNegative(324)
                .comments("some comment").build(), "Change adress"});
        return data;
    }

    private List<Object[]> testDataByPostBuildingSite() {
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[]{BuildingSite.builder().address("Zvezdova4")
                .type("Building_Site")
                .uniqueId("00-11-122")
                .rayon(DictionaryElementShort.builder().id(3460).name("Восточный административный округ").build())
                .okrug(DictionaryElementShort.builder().id(3513).name("район Богородское").parentKey(3460).build())
                .lastChangeDate(LocalDateTime.parse("2018-03-09T23:42:06"))
                .documents(new ArrayList<>())
                .renovationInfrastructures(new ArrayList<>())
                .comments("bad")
                .causeOfConstructionCancellation("bad")
                .resettlement(2019).build(), "Zvezdova4"});
        return data;
    }


    private List<Object[]> testDataByFindOne() {
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[]{renovationArbatId, "00-11-121"});
        data.add(new Object[]{renovationBibirevoId, "00-11-122"});
        return data;
    }

    private RenovationFilter<Renovation> dataProviderByFindByFilter(List<Object[]> data) {
        RenovationFilter<Renovation> filter = new RenovationFilter<>();
        filter.setPage((Integer) data.get(iteratorTest)[0]);
        filter.setSize((Integer) data.get(iteratorTest)[1]);
        filter.setSearch((String) data.get(iteratorTest)[2]);
        return filter;
    }


    @Test
    public void findByFilter() {
        iteratorTest = 0;
        while (iteratorTest < testDataByFindByFilter().size()) {
            FilterRenovationDto renovationDto = renovationService.findByFilter(dataProviderByFindByFilter(testDataByFindByFilter()));
            assertEquals(testDataByFindByFilter().get(iteratorTest)[3], renovationDto.getSummary().getTotal());
            iteratorTest++;
        }
    }

    @Test
    public void findRenovation() {
        iteratorTest = 0;
        while (iteratorTest < testDataByFindRenovation().size()) {
            Renovation renovation = renovationService.findRenovation((Integer) testDataByFindRenovation().get(iteratorTest)[0]);
            assertEquals(testDataByFindRenovation().get(iteratorTest)[1], renovation.getUniqueId());
            iteratorTest++;
        }
    }

    @Test
    public void getHistory() {
        renovationService.getHistory(1, null);
    }

    @Test
    public void updateBuilding() {
        iteratorTest = 0;
        while (iteratorTest < testDataByUpdateBuilding().size()) {
            Building building = renovationService.updateBuilding((Building) testDataByUpdateBuilding().get(iteratorTest)[0]);
            assertEquals(testDataByUpdateBuilding().get(iteratorTest)[1], building.getAddress());
            iteratorTest++;
        }
    }

    @Test
    public void updateBuildingSite() {
        iteratorTest = 0;
        while (iteratorTest < testDataByUpdateBuildingSite().size()) {
            BuildingSite buildingSite = renovationService.updateBuildingSite((BuildingSite) testDataByUpdateBuildingSite().get(iteratorTest)[0]);
            assertEquals(testDataByUpdateBuildingSite().get(iteratorTest)[1], buildingSite.getAddress());
            iteratorTest++;
        }
    }

    @Test
    public void postBuilding() {
        iteratorTest = 0;
        while (iteratorTest < testDataByPostBuilding().size()) {
            Building building = renovationService.postBuilding((Building) testDataByPostBuilding().get(iteratorTest)[0]);
            assertEquals(testDataByPostBuilding().get(iteratorTest)[1], building.getAddress());
            iteratorTest++;
        }
    }

    @Test
    public void postBuildingSite() {
        iteratorTest = 0;
        while (iteratorTest < testDataByPostBuildingSite().size()) {
            BuildingSite buildingSite = renovationService.postBuildingSite((BuildingSite) testDataByPostBuildingSite().get(iteratorTest)[0]);
            assertEquals(testDataByPostBuildingSite().get(iteratorTest)[1], buildingSite.getAddress());
            iteratorTest++;
        }
    }

    @Test
    public void findOne() {
        iteratorTest = 0;
        while (iteratorTest < testDataByFindOne().size()) {
            Renovation renovationDto = renovationService.findOne((Integer) testDataByFindOne().get(iteratorTest)[0]);
            assertEquals(testDataByFindOne().get(iteratorTest)[1], renovationDto.getUniqueId());
            iteratorTest++;
        }
    }

    @Test
    public void updateRenovation() {
        ObjectMapper oMapper = new ObjectMapper();
        iteratorTest = 0;
        while (iteratorTest < testDataByUpdateRenovation().size()) {

            String testAdress=null;
            Map<String, Object> testData = oMapper.convertValue(testDataByUpdateRenovation().get(iteratorTest)[0], Map.class);
            Object ob = renovationService.updateRenovation(testData);

            if (ob instanceof Building) {
                Building building = (Building) ob;
                testAdress=building.getAddress();
            } else if (ob instanceof BuildingSite) {
                BuildingSite buildingSite = (BuildingSite) ob;
                testAdress=buildingSite.getAddress();
            }
            assertEquals(testDataByUpdateRenovation().get(iteratorTest)[1], testAdress);
            iteratorTest++;

        }
    }
}