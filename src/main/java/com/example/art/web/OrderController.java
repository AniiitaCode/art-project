package com.example.art.web;

import com.example.art.design.model.*;
import com.example.art.design.service.DesignService;
import com.example.art.order.service.OrderService;
import com.example.art.security.AuthenticationDetails;
import com.example.art.user.model.User;
import com.example.art.user.service.UserService;
import com.example.art.web.dto.OrderRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final UserService userService;
    private final DesignService designService;
    private final OrderService orderService;

    public OrderController(UserService userService,
                           DesignService designService,
                           OrderService orderService) {
        this.userService = userService;
        this.designService = designService;
        this.orderService = orderService;
    }

    @GetMapping
    public ModelAndView orderPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getUserId());
        Design design = designService.getLastDesign(user.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("order");
        modelAndView.addObject("user", user);
        modelAndView.addObject("design", design);
        modelAndView.addObject("orderRequest", new OrderRequest());

        return modelAndView;
    }

    @PostMapping
    public ModelAndView orderPageConfirm(@Valid OrderRequest orderRequest,
                                   BindingResult bindingResult,
                                         @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {

        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {
            User user = userService.getById(authenticationDetails.getUserId());
            Design design = designService.getLastDesign(user.getId());

            modelAndView.setViewName("order");
            modelAndView.addObject("user", user);
            modelAndView.addObject("design", design);
            modelAndView.addObject("orderRequest", orderRequest);

            return modelAndView;
        }

        User user = userService.getById(authenticationDetails.getUserId());
        Design design = designService.getLastDesign(user.getId());

        orderService.saveOrder(orderRequest, design);

        modelAndView.setViewName("redirect:/home");
        return modelAndView;
    }


}
