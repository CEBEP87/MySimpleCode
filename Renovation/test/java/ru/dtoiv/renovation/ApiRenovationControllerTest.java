package ru.XXXXXXXXX.renovation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.XXXXXXXXX.data.service.audit.AuditService;
import ru.XXXXXXXXX.dictionary.domain.entity.DictionaryElementShort;
import ru.XXXXXXXXX.renovation.domain.entity.*;
import ru.XXXXXXXXX.renovation.domain.filter.RenovationFilter;
import ru.XXXXXXXXX.renovation.repository.RenovationRepository;
import ru.XXXXXXXXX.renovation.service.RenovationService;
import ru.XXXXXXXXX.renovation.web.api.ApiRenovationController;
import ru.XXXXXXXXX.user.domain.entity.UserEntity;
import ru.XXXXXXXXX.user.service.UserService;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring/root-context.xml",
        "classpath:spring/servlet-context.xml"
})
@WebAppConfiguration
@Transactional
public class ApiRenovationControllerTest {

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

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    @Autowired
    AuditService auditService;
    @Autowired
    RenovationRepository renovationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RenovationService renovationService;

    @Autowired
    private ApiRenovationController apiRenovationController;

    @Autowired
    private MappingJackson2HttpMessageConverter springMvcJacksonConverter;

    @Autowired
    private DataSource dataSource;
    private MockMvc mockMvc;

    @Before
    public void setup() {

        initDB();
        buildSecurityContext();

        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(apiRenovationController).build();

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

    @Test
    public void getBuildingRenovationById() throws Exception {
        mockMvc.perform(get("/api/renovations/" + renovationArbatId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.uniqueId").value("00-11-121"));
    }

    @Test
    public void getBuildingSiteRenovationById() throws Exception {
        mockMvc.perform(get("/api/renovations/" + renovationBibirevoId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.uniqueId").value("00-11-122"));
    }

    @Test
    public void putRenovation() throws Exception {
        String sourceContent = this.readFile("ru.XXXXXXXXX.renovation.ApiRenovationControllerTest/putBuildingRenovationSiteById-response.json")
                .replace("{id}", renovationBibirevoId.toString());

        mockMvc.perform(
                put("/api/renovations/renovation")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content((sourceContent)))
                .andExpect(status().isOk())
                    .andExpect(content().json(sourceContent, false));

        sourceContent = this.readFile("ru.XXXXXXXXX.renovation.ApiRenovationControllerTest/putBuildingRenovationById-response.json")
                .replace("{id}", renovationArbatId.toString());

        mockMvc.perform(
                put("/api/renovations/renovation")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content((sourceContent)))
                .andExpect(status().isOk())
                .andExpect(content().json(sourceContent, false));

    }

    @Test
    public void postBuildingRenovationById() throws Exception {
        String sourceContent = this.readFile("ru.XXXXXXXXX.renovation.ApiRenovationControllerTest/postBuildingRenovationById-response.json");
        mockMvc.perform(
                post("/api/renovations/building")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content((sourceContent)))
                .andExpect(status().isOk())
                .andExpect(content().json(sourceContent, false));

    }

    @Test
    public void postBuildingSiteRenovationById() throws Exception {
        String sourceContent = this.readFile("ru.XXXXXXXXX.renovation.ApiRenovationControllerTest/postBuildingRenovationSiteById-response.json");
        mockMvc.perform(
                post("/api/renovations/building-site")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content((sourceContent)))
                .andExpect(status().isOk())
                .andExpect(content().json(sourceContent, false));

    }

    @Test
    public void getHistory() throws Exception {
        mockMvc.perform(
                get("/api/renovations/1/hist")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void getShortRenovationsFilteredByAddress() throws Exception {
        RenovationFilter<Renovation> filter = new RenovationFilter<>();
        filter.setPage(0);
        filter.setSize(10);
        filter.setSearch("Сиблова");

        renovationService.findByFilter(filter);
        String renovationContent = this.readFile("ru.XXXXXXXXX.renovation.ApiRenovationControllerTest/getShortRenovations-response.json");
        mockMvc.perform(
                post("/api/renovations/readAll/")
                        .content(json(filter))
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().json(renovationContent, false));
    }


    protected String readFile(String fileName) throws Exception {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(fileName));
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.springMvcJacksonConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
