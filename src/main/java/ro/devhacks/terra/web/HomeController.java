package ro.devhacks.terra.web;

import ro.devhacks.terra.model.AuthenticatedUser;
import ro.devhacks.terra.model.PairingSession;
import ro.devhacks.terra.model.User;
import ro.devhacks.terra.service.PairingSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class HomeController {

    private final PairingSessionService pairingSessionService;

    @Autowired
    public HomeController(PairingSessionService pairingSessionService) {
        this.pairingSessionService = pairingSessionService;
    }

    @RequestMapping({"/", "/index", "/index.html"})
    String index( @ModelAttribute("joined") final String joinedMessage, final Model model) {
        model.addAttribute("joined", !StringUtils.isEmpty(joinedMessage));
        model.addAttribute("joinMessage", joinedMessage);
        return "index";
    }

    @RequestMapping({"/calendar"})
    String calendar() {
        return "calendar";
    }

    @RequestMapping("/about")
    String about() { return "about"; }

    @RequestMapping(value = "/join/{sessionId}",method = RequestMethod.GET)
    public ModelAndView joinSession(@PathVariable Long sessionId, Principal principal, final RedirectAttributes redirectAttributes) {
        ModelAndView mav = new ModelAndView("redirect:" + "/index");

        User currentUser = ((AuthenticatedUser)((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        PairingSession session = pairingSessionService.joinSession(sessionId, currentUser);

        redirectAttributes.addFlashAttribute("joined", "Joined session <strong>'" + session.getSessionName() + "'</strong> successfully!");

        return mav;
    }
}
