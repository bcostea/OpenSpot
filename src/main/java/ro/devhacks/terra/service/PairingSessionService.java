package ro.devhacks.terra.service;

import ro.devhacks.terra.model.PairingSession;
import ro.devhacks.terra.model.User;

import java.text.ParseException;
import java.util.List;

public interface PairingSessionService {

    public PairingSession save(PairingSession pairingSession) throws ParseException;
    public List<PairingSession> getAllPublicSessions();
    public PairingSession joinSession(Long sessionId, User participant);
    public List<PairingSession> findAllByOrderByIdDesc();
    public List<PairingSession> findByCreatorNotAndParticipant(User creator, User participant);
    public List<PairingSession> findByParticipant(User participant);
    public List<PairingSession> findByParticipantOrCreator(User participant, User creator);

}
