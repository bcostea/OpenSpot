package ro.devhacks.terra.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ro.devhacks.terra.model.AuthenticatedUser;
import ro.devhacks.terra.model.Lot;
import ro.devhacks.terra.model.ParkingSpot;
import ro.devhacks.terra.model.User;
import ro.devhacks.terra.service.LotService;

import java.security.Principal;
import java.util.List;

@Controller
public class LotController {

    private final LotService lotService;

    @Autowired
    public LotController(LotService lotService) {
        this.lotService = lotService;
    }

    @RequestMapping("/lot")
    public String showLot(Principal principal, final Model model){
        AuthenticatedUser  activeUser = (AuthenticatedUser) ((Authentication) principal).getPrincipal();
        Lot lot = lotService.getLot(activeUser.getUser());
        List<ParkingSpot> spots = lotService.getSpots(lot);
        model.addAttribute("lot", lot);
        model.addAttribute("spots", spots);
        return "lot";
    }
    @RequestMapping("/lotconfig")
    public String showLotConfig(Principal principal, final Model model){
        AuthenticatedUser  activeUser = (AuthenticatedUser) ((Authentication) principal).getPrincipal();
        Lot lot = lotService.getLot(activeUser.getUser());
        model.addAttribute("lot", lot);
        return "lotconfig";
    }
}
