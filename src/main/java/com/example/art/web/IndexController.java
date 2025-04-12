package com.example.art.web;

import com.example.art.security.AuthenticationDetails;
import com.example.art.user.service.UserService;
import com.example.art.web.dto.LoginRequest;
import com.example.art.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

    private final UserService userService;

    public IndexController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ModelAndView indexPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {

        if (authenticationDetails != null) {
            return new ModelAndView("redirect:/home");
        }

        return new ModelAndView("index");
    }

    @GetMapping("/register")
    public ModelAndView registerPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {

        if (authenticationDetails != null) {
            return new ModelAndView("redirect:/home");
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());

        return modelAndView;
    }

    @PostMapping("/register")
    public String registerPageConfirm(@Valid RegisterRequest registerRequest,
                                      BindingResult bindingResult) {

        if (bindingResult.hasErrors() ||
                !registerRequest.getConfirmPassword().equals(registerRequest.getPassword())) {
            return "register";
        }

        userService.register(registerRequest);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public ModelAndView loginPage(@RequestParam(value = "error", required = false)String errorParam,
                                  @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {

        if (authenticationDetails != null) {
            return new ModelAndView("redirect:/home");
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        if (errorParam != null) {
            modelAndView.addObject("errorMessage", "Incorrect username or password!");
        }

        return modelAndView;
    }
}


