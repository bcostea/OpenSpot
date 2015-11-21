package ro.devhacks.terra.web.api;

import ro.devhacks.terra.model.PairingSession;
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

import java.util.Arrays;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppContext.class})
@WebAppConfiguration
public class PairingSessionControllerIntegrationTest {

    private PairingSessionService pairingSessionServiceMock;

    @Before
    public void setup() {
        this.pairingSessionServiceMock = Mockito.mock(PairingSessionService.class);
        Mockito
                .when(pairingSessionServiceMock.findByParticipant(null))
                .thenReturn(Arrays.asList(buildSession("session one"), buildSession("session two")));
    }

    @Test
    public void getPublicSessions_shouldReturn_allPairingSessions() throws Exception {

        given()
            .standaloneSetup(new PairingSessionController(pairingSessionServiceMock))
        .when()
                .get("/api/public/sessions")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("size()", is(2));

        Mockito.verify(pairingSessionServiceMock, Mockito.times(1)).findByParticipant(null);
        Mockito.verifyNoMoreInteractions(pairingSessionServiceMock);
    }

    private static PairingSession buildSession(String name) {
        PairingSession session = new PairingSession();
        session.setSessionName(name);
        return session;
    }
}
