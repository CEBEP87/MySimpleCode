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
import ru.XXXXXXXXX.renovation.repository.RenovationRepository;
import ru.XXXXXXXXX.renovation.web.api.ApiRenovationReviewController;
import ru.XXXXXXXXX.user.domain.entity.UserEntity;
import ru.XXXXXXXXX.user.service.UserService;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring/root-context.xml",
        "classpath:spring/servlet-context.xml"
})
@WebAppConfiguration
@Transactional
public class ApiRenovationReviewControllerTest {

    private DictionaryElementShort statusInWork;

    private DictionaryElementShort okrugCAO;
    private DictionaryElementShort rayonArbat;
    private Building renovationArbat;
    private Building arbat;
    private Integer renovationArbatId;

    private RenovationInfrastructure renovationInfrastructureMedic;
    private RenovationInfrastructure renovationInfrastructureClub;
    private DictionaryElementShort typeInfrastructure;

    private BuildingMaterials buildingMaterials;
    private BuildingAreas buildingAreas;
    private BuildingParameters buildingParameters;


    private static final String CONTROLLER_PATH = "/api/renovations";
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    @Autowired
    AuditService auditService;

    @Autowired
    RenovationRepository renovationRepository;

    @Autowired
    private ApiRenovationReviewController apiRenovationReviewController;

    @Autowired
    private MappingJackson2HttpMessageConverter springMvcJacksonConverter;

    @Autowired
    private UserService userService;


    @Autowired
    private DataSource dataSource;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        initDB();
        buildSecurityContext();
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(apiRenovationReviewController).build();

    }

    private void initDB() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();

        statusInWork = DictionaryElementShort.builder().id(71086031).build();
        okrugCAO = DictionaryElementShort.builder().id(20647).build();

        rayonArbat = DictionaryElementShort.builder().id(3470).build();

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

        renovationArbat = Building.builder().status(statusInWork).rayon(rayonArbat).okrug(okrugCAO).uniqueId("00-11-121").renovationInfrastructures(renovationInfrastructures).buildingMaterials(buildingMaterials).buildingAreas(buildingAreas).buildingParameters(buildingParameters).build();

        arbat = renovationRepository.save(renovationArbat);
        renovationArbatId = arbat.getId();
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
    public void confirmReview() throws Exception {
        mockMvc.perform(get(CONTROLLER_PATH + "/{id}/confirmReview", renovationArbatId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Проверено")));
    }

    @Test
    public void rejectReview() throws Exception {
        String sourceContent = this.readFile("ru.XXXXXXXXX.renovation.ApiRenovationReviewControllerTest/rejectReview.json");
        mockMvc.perform(
                post(CONTROLLER_PATH + "/{id}/rejectReview", renovationArbatId)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(sourceContent))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("В работе")));
    }

    @Test
    public void sendOnReview() throws Exception {
        String sourceContent = this.readFile("ru.XXXXXXXXX.renovation.ApiRenovationReviewControllerTest/rejectReview.json");
        mockMvc.perform(
                post(CONTROLLER_PATH + "/{id}/sendOnReview", renovationArbatId)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(sourceContent))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("На проверке")));

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
