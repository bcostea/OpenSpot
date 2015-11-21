package ro.devhacks.terra.service.impl;

import ro.devhacks.terra.model.PairingSession;
import ro.devhacks.terra.model.User;
import ro.devhacks.terra.repository.PairingSessionRepository;
import ro.devhacks.terra.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class PairingSessionTest {
    private UserRepository userRepositoryMock;
    private PairingSessionRepository pairingSessionRepositoryMock;
    private PairingSession session;

    @Before
    public void setup() {
        this.pairingSessionRepositoryMock = Mockito.mock(PairingSessionRepository.class);
        this.userRepositoryMock = Mockito.mock(UserRepository.class);

        session = Mockito.mock(PairingSession.class);
        Mockito.when(pairingSessionRepositoryMock.findOne(Mockito.anyLong())).thenReturn(session);
    }

    @Test
    public void joining_a_session_saves_user_as_participant() {
        this.userRepositoryMock = Mockito.mock(UserRepository.class);

        DefaultPairingSessionService service = new DefaultPairingSessionService(pairingSessionRepositoryMock);
        User participant = Mockito.mock(User.class);
        User creator = Mockito.mock(User.class);

        Mockito
                .when(session.getCreator())
                .thenReturn(creator);
        Mockito
                .when(userRepositoryMock.getOne(Mockito.anyLong()))
                .thenReturn(participant);

        service.joinSession(1L, participant);
        Mockito.verify(session).setParticipant(participant);
    }


    @Test(expected = Exception.class)
    public void joining_a_session_created_by_the_same_user_fails() {
        this.userRepositoryMock = Mockito.mock(UserRepository.class);

        DefaultPairingSessionService service = new DefaultPairingSessionService(pairingSessionRepositoryMock);
        User user = Mockito.mock(User.class);

        Mockito
                .when(userRepositoryMock.getOne(Mockito.anyLong()))
                .thenReturn(user);

        Mockito
                .when(session.getCreator())
                .thenReturn(user);

        service.joinSession(1L, user);
    }
}
