package com.example.art.web;

import com.example.art.email.client.dto.EmailPreference;
import com.example.art.email.service.EmailService;
import com.example.art.security.AuthenticationDetails;
import com.example.art.user.model.User;
import com.example.art.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/emails")
public class EmailController {

    private final UserService userService;
    private final EmailService emailService;

    public EmailController(UserService userService,
                           EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping
    public ModelAndView preferencePage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getUserId());
        EmailPreference emailPreference =
                emailService.getPreference(user.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("email");
        modelAndView.addObject("user", user);
        modelAndView.addObject("emailPreference", emailPreference);

        return modelAndView;
    }

    @PutMapping("/preference")
    public String changePreference(@RequestParam(name = "enabled") boolean enabled,
                                   @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {

        emailService.changePreference(authenticationDetails.getUserId(), enabled);

        return "redirect:/emails";
    }
}
