package com.example.art.web;

import com.example.art.exception.DomainException;
import com.example.art.security.AuthenticationDetails;
import com.example.art.user.model.User;
import com.example.art.user.model.UserRole;
import com.example.art.user.service.UserService;
import com.example.art.web.dto.UserEditRequest;
import com.example.art.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/profile/edit")
    public ModelAndView updateUserProfile(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getUserId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile-edit");
        modelAndView.addObject("userEditRequest", DtoMapper.mapUserToUserEditRequest(user));
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PutMapping("/profile/edit")
    public ModelAndView updateUserProfileConfirm(@Valid UserEditRequest userEditRequest,
                                                 BindingResult bindingResult,
                                                 @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {

        if (bindingResult.hasErrors()) {
            User user = userService.getById(authenticationDetails.getUserId());
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("profile-edit");
            modelAndView.addObject("user", user);
            modelAndView.addObject("userEditRequest", userEditRequest);

            return modelAndView;
        }

        userService.editUserData(authenticationDetails.getUserId(), userEditRequest);
        return new ModelAndView("redirect:/home");
    }

    @PutMapping("/{userId}/role")
    public String switchUserRole(@AuthenticationPrincipal AuthenticationDetails authenticationDetails,
                                 @PathVariable UUID userId) {
        User currentUser = userService.getById(authenticationDetails.getUserId());

        if (!currentUser.getRole().equals(UserRole.ADMIN)) {
            throw new DomainException("You do not have sufficient permissions to add new users!");
        }

        userService.switchRole(userId);
        return "redirect:/users";
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllUsers() {
        List<User> users = userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("users", users);

        return modelAndView;
    }
}