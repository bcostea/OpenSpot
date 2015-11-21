package ro.devhacks.terra.web;

import ro.devhacks.terra.service.PairingSessionService;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;

import static com.jayway.restassured.RestAssured.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppContext.class})
@WebAppConfiguration
public class HomeControllerIntegrationTest {

    private PairingSessionService pairingSessionService;

    @Before
    public void setUp(){
        pairingSessionService = Mockito.mock(PairingSessionService.class);
    }

    @Test
    public void getHome() throws Exception {
        given()
            .standaloneSetup(new HomeController(pairingSessionService))
        .when()
            .get("/")
        .then()
            .statusCode(HttpStatus.OK.value());
    }

}
