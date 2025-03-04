package com.example.art.web;

import com.example.art.design.service.DesignService;
import com.example.art.security.AuthenticationDetails;
import com.example.art.user.model.User;
import com.example.art.user.service.UserService;
import com.example.art.web.dto.DesignDecorationRequest;
import com.example.art.web.dto.DesignFormRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/designs")
public class DesignController {

    private final UserService userService;
    private final DesignService designService;

    public DesignController(UserService userService,
                            DesignService designService) {
        this.userService = userService;
        this.designService = designService;
    }

    @GetMapping
    public ModelAndView designCreatePage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("design-create");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/nextPage")
    public ModelAndView designFormPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("design-form");
        modelAndView.addObject("user", user);
        modelAndView.addObject("designFormRequest", new DesignFormRequest());
        return modelAndView;
    }

    @PostMapping("/nextPage")
    public String designFormConfirm(@Valid DesignFormRequest designFormRequest,
                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "design-form";
        }

        designService.saveForm(designFormRequest);
        return "redirect:/designs/decoration";
    }

    @GetMapping("/forms")
    public ModelAndView showFormPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("design-form-show");
        return modelAndView;
    }

    @GetMapping("/decoration")
    public ModelAndView designDecorationPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("design-decoration");
        modelAndView.addObject("user", user);
        modelAndView.addObject("designDecorationRequest", new DesignDecorationRequest());

        return modelAndView;
    }

    @PostMapping("/decoration")
    public String designDecorationConfirm(@Valid DesignDecorationRequest designDecorationRequest,
                                          BindingResult bindingResult,
                                          @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {

        if (bindingResult.hasErrors()) {
            return "design-decoration";
        }

        User user = userService.getById(authenticationDetails.getUserId());
        designService.saveDecoration(designDecorationRequest);
        designService.saveAllInformationDesign(designService.getSavedForm(), designDecorationRequest, user);

        return "redirect:/orders";
    }
}
