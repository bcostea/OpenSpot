package ro.devhacks.terra.service.impl;

import ro.devhacks.terra.model.PairingSession;
import ro.devhacks.terra.model.User;
import ro.devhacks.terra.repository.PairingSessionRepository;
import ro.devhacks.terra.service.PairingSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class DefaultPairingSessionService implements PairingSessionService {

    private final PairingSessionRepository pairingSessionRepository;

    @Autowired
    public DefaultPairingSessionService(PairingSessionRepository pairingSessionRepository) {
        this.pairingSessionRepository = pairingSessionRepository;
    }

    @Override
    public PairingSession save(PairingSession pairingSession) throws ParseException {
        return pairingSessionRepository.save(translateSessionTime(pairingSession));
    }

    @Override
    public List<PairingSession> getAllPublicSessions() {
        return pairingSessionRepository.findAll();
    }

    @Override
    public List<PairingSession> findAllByOrderByIdDesc() {
        return pairingSessionRepository.findAllByOrderByIdDesc();
    }

    @Override
    public List<PairingSession> findByCreatorNotAndParticipant(User creator, User participant) {
        return pairingSessionRepository.findByCreatorNotAndParticipant(creator, participant);
    }

    @Override
    public List<PairingSession> findByParticipant(User participant) {
        return pairingSessionRepository.findByParticipant(participant);
    }

    @Override
    public List<PairingSession> findByParticipantOrCreator(User participant, User creator) {
        return pairingSessionRepository.findByParticipantOrCreator(participant, creator);
    }

    @Override
    public PairingSession joinSession(Long sessionId, User participant) {
        PairingSession session = pairingSessionRepository.findOne(sessionId);
        if (session.getCreator().equals(participant)) {
            throw new IllegalArgumentException("You cannot join your own session");
        }
        session.setParticipant(participant);

        PairingSession savedSession = pairingSessionRepository.save(session);

        return savedSession;
    }

    private PairingSession translateSessionTime(PairingSession session) throws ParseException {
        SimpleDateFormat sessionDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date atDate = null;

        try {
            atDate = sessionDateFormat.parse(session.getDateAsString());
        } catch (Exception e) {
            if (session.getAtTime() != null) {
                return session;
            } else {
                throw new IllegalArgumentException("Date format is incorrect, try 'yyyy-MM-dd HH:mm'");
            }
        }

        session.setAtTime(atDate);
        return session;
    }
}
