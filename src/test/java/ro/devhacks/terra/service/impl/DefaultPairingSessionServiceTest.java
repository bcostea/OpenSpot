package ro.devhacks.terra.service.impl;

import ro.devhacks.terra.model.PairingSession;
import ro.devhacks.terra.repository.PairingSessionRepository;
import ro.devhacks.terra.service.PairingSessionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalAnswers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultPairingSessionServiceTest {

    private PairingSessionRepository pairingSessionRepository;

    PairingSession session = new PairingSession();
    {
        session.setSessionName("a session");
    }

    @Before
    public void setUp(){
        pairingSessionRepository = mock(PairingSessionRepository.class);

        when(pairingSessionRepository.save(any(PairingSession.class)))
                .then(AdditionalAnswers.returnsFirstArg());
        when(pairingSessionRepository.findAll())
                .thenReturn(new ArrayList<>(Arrays.asList(session)));
        when(pairingSessionRepository.findAllByOrderByIdDesc())
                .thenReturn(new ArrayList<>(Arrays.asList(session)));
    }

    @Test
    public void onSave_return_persistedObject() throws ParseException {
        PairingSessionService pairingSessionService = new DefaultPairingSessionService(pairingSessionRepository);
        session.setDateAsString("2015-10-10 10:10");
        assertThat(pairingSessionService.save(session).getSessionName()).isEqualTo("a session");
    }


    @Test(expected = IllegalArgumentException.class)
    public void onSave_throw_IllegalArgument_on_invalidDate() throws ParseException {
        PairingSessionService pairingSessionService = new DefaultPairingSessionService(pairingSessionRepository);
        session.setDateAsString("2015-10");
        assertThat(pairingSessionService.save(session).getSessionName()).isEqualTo("a session");
    }



    @Test
    public void onGetAllPublicSessions_return_openSessions(){
        PairingSessionService pairingSessionService = new DefaultPairingSessionService(pairingSessionRepository);

        assertThat(pairingSessionService.findAllByOrderByIdDesc()).isNotNull();
        assertThat(pairingSessionService.findAllByOrderByIdDesc().size()).isEqualTo(1);
    }


    @Test
    public void onSave_TranslateTime_withNoText_return_translatedSession_with_unmodifiedDate() throws ParseException {
        PairingSessionService pairingSessionService = new DefaultPairingSessionService(pairingSessionRepository);

        PairingSession sessionWithTime = new PairingSession();
        sessionWithTime.setAtTime(new Date(1444461000000L));

        assertThat(pairingSessionService.save(sessionWithTime).getAtTime())
                .isEqualTo(new Date(1444461000000L)); //atTime did not change

    }

    @Test
    public void onSave_TranslateTime_withCorrectFormat_return_translatedSession() throws ParseException {
        PairingSessionService pairingSessionService = new DefaultPairingSessionService(pairingSessionRepository);

        PairingSession sessionWithTime = new PairingSession();
        sessionWithTime.setDateAsString("2015-10-10 10:10");

        assertThat(pairingSessionService.save(sessionWithTime).getAtTime()).isNotNull();

    }


}
