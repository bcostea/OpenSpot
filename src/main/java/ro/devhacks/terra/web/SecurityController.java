package ro.devhacks.terra.web;

import ro.devhacks.terra.repository.UserRepository;
import ro.devhacks.terra.service.UserService;
import ro.devhacks.terra.service.impl.DatabaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.expression.Strings;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class SecurityController {


    UserService userService;

    @Autowired
    public SecurityController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView getLoginPage(@RequestParam Optional<String> error) {
        return new ModelAndView("login", "error", error);
    }

    @RequestMapping(value = "/createaccount", method = RequestMethod.GET)
    public ModelAndView createAccount(@RequestParam Optional<String> error) {
        return new ModelAndView("createaccount", "error", error);
    }

    @RequestMapping(value = "/createaccount", method = RequestMethod.POST)
    public ModelAndView  createAccount(@RequestParam("username") String username, @RequestParam("password") String pass){
        if (StringUtils.isEmpty(username))
        {
            return new ModelAndView("createaccount", "error", "User name required");
        }
        if (StringUtils.isEmpty(pass)){
            return new ModelAndView("createaccount", "error", "Password required");
        }

        Boolean userSaved = userService.saveUser(username, pass);
        if(userSaved){
            return new ModelAndView("login");
        }
        return new ModelAndView("createaccount", "error", "Failed to create user. Please try again later");

    }

}