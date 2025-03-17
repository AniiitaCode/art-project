package com.example.art.web;

import com.example.art.security.AuthenticationDetails;
import com.example.art.user.model.User;
import com.example.art.user.service.UserService;
import com.example.art.wallet.model.Wallet;
import com.example.art.wallet.service.WalletService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;

    public WalletController(WalletService walletService,
                            UserService userService) {
        this.walletService = walletService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView walletPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        UUID userId = authenticationDetails.getUserId();
        User user = userService.getById(userId);
        Wallet wallet = walletService.getWalletByUser(user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("wallet");
        modelAndView.addObject("wallet", wallet);

        return modelAndView;
    }
}
