package ro.devhacks.terra.web.api;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.matcher.RestAssuredMatchers;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ro.devhacks.terra.model.ParkingSpot;
import ro.devhacks.terra.model.ParkingSpotStatus;
import ro.devhacks.terra.model.dto.ParkingSpotUpdate;
import ro.devhacks.terra.repository.ParkingSpotRepository;
import ro.devhacks.terra.service.ParkingSpotService;
import ro.devhacks.terra.service.impl.DefaultParkingSpotService;
import ro.devhacks.terra.web.HomeController;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static com.jayway.restassured.module.mockmvc.matcher.RestAssuredMockMvcMatchers.equalToPath;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppContext.class})
@WebAppConfiguration
public class ParkingSpotSensorAPIControllerIntegrationTest {

    private ParkingSpotService parkingSpotService;

    @Before
    public void setUp() {
    }

    @Test
    public void updateSpotToFree() throws Exception {

        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setId("TEST");
        parkingSpot.setStatus(ParkingSpotStatus.OCCUPIED);


        ParkingSpotRepository parkingSpotRepository = Mockito.mock(ParkingSpotRepository.class);

        Mockito
                .when(parkingSpotRepository.findOne(anyString()))
                .thenReturn(parkingSpot);

        Mockito
                .when(parkingSpotRepository.save(any(ParkingSpot.class)))
                .then(AdditionalAnswers.returnsFirstArg());

        parkingSpotService = new DefaultParkingSpotService(parkingSpotRepository);

        given().standaloneSetup(new ParkingSpotSensorAPIController(parkingSpotService)).contentType(ContentType.JSON)
                .body("{\"id\":\"TEST\", \"free\":true}")
                .when().post("/api/public/spots/TEST/update")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("status", org.hamcrest.Matchers.equalTo("FREE"));
    }

}
