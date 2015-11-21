package ro.devhacks.terra.web.api;

import ro.devhacks.terra.model.AuthenticatedUser;
import ro.devhacks.terra.model.PairingSession;
import ro.devhacks.terra.model.User;
import ro.devhacks.terra.service.PairingSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;

@Controller
public class PairingSessionController {

    private final PairingSessionService pairingSessionService;

    @Autowired
    public PairingSessionController(PairingSessionService pairingSessionService) {
        this.pairingSessionService = pairingSessionService;
    }

    private User getUser(Principal principal) {
        return ((AuthenticatedUser)((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
    }


    @RequestMapping(value = "/api/public/sessions/calendar", method= RequestMethod.GET)
    public @ResponseBody List<PairingSession> getSessionsCalendar(Principal principal) {
        User user = getUser(principal);
        return pairingSessionService.findByParticipantOrCreator(user, user);
    }

    @RequestMapping(value = "/api/public/sessions", method= RequestMethod.GET)
    public @ResponseBody List<PairingSession> getPublicSessions(Principal principal) {

        if (principal!=null) {
            User user = getUser(principal);
            return pairingSessionService.findByCreatorNotAndParticipant(user, null);
        } else {
            return pairingSessionService.findByParticipant(null);
        }
    }

    @RequestMapping(value = "/sessions", method = RequestMethod.GET)
    public String sessions(){
        return "sessions";
    }

    @RequestMapping(value = "/api/session/add", method= RequestMethod.POST)
    public String savePairingSession(@RequestBody PairingSession pairingSession, Principal principal) throws ParseException {

        pairingSession.setCreator(getUser(principal));

        pairingSessionService.save(pairingSession);

        return "index";
    }

}
