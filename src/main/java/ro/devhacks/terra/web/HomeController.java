package ro.devhacks.terra.web;

import ro.devhacks.terra.model.AuthenticatedUser;
import ro.devhacks.terra.model.User;
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

}
