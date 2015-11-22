package ro.devhacks.terra.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import ro.devhacks.terra.model.Role;

@Controller
public class HomeController {

    @RequestMapping({"/", "/index", "/index.html"})
    String index(@ModelAttribute("joined") final String joinedMessage, final Model model, Authentication authentication) {
        model.addAttribute("joined", !StringUtils.isEmpty(joinedMessage));
        model.addAttribute("joinMessage", joinedMessage);

        if(authentication!=null) {

            if(authentication.getAuthorities()!=null && authentication.getAuthorities().size()>0 &&
                    authentication.getAuthorities().toArray()[0].toString().equals(Role.LOT.toString())){
                return "redirect:/lot";
            }
        }

        return "index";
    }

    @RequestMapping({"/calendar"})
    String calendar() {
        return "calendar";
    }

    @RequestMapping("/about")
    String about() { return "about"; }

    @RequestMapping("/api/public/testWebSocket")
    String testWebSocket() { return "testWebSocket"; }

}
