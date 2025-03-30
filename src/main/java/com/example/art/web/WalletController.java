package com.example.art.web;

import com.example.art.security.AuthenticationDetails;
import com.example.art.transaction.model.Transaction;
import com.example.art.transaction.model.TransactionType;
import com.example.art.transaction.service.TransactionService;
import com.example.art.user.model.User;
import com.example.art.user.service.UserService;
import com.example.art.wallet.model.Wallet;
import com.example.art.wallet.service.WalletService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;
    private final TransactionService transactionService;

    public WalletController(WalletService walletService,
                            UserService userService,
                            TransactionService transactionService) {
        this.walletService = walletService;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @GetMapping
    public ModelAndView walletPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        UUID userId = authenticationDetails.getUserId();
        User user = userService.getById(userId);
        Wallet wallet = walletService.getWalletByUser(user);
        List<Transaction> transactions = transactionService.getByUserId(userId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("wallet");
        modelAndView.addObject("wallet", wallet);
        modelAndView.addObject("transactions", transactions);

        return modelAndView;
    }

    @PostMapping("/deposit")
    public String addMoney(@AuthenticationPrincipal AuthenticationDetails authenticationDetails,
                                 @RequestParam(name = "amount") BigDecimal amount,
                                 RedirectAttributes redirectAttributes) {

        UUID userId = authenticationDetails.getUserId();
        User user = userService.getById(userId);

        Wallet wallet = walletService.addBalance(user, amount);
        transactionService.createTransaction(user, wallet, amount, TransactionType.ADD);

        redirectAttributes.addFlashAttribute("wallet", wallet);
        redirectAttributes.addFlashAttribute("transactions",
                transactionService.getByUserId(userId));

        return "redirect:/wallets";
    }

    @DeleteMapping("/transactions/clear")
    public String clearTransactionHistory(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        UUID userId = authenticationDetails.getUserId();
        User user = userService.getById(userId);

        transactionService.clearHistory(user);

        return "redirect:/wallets";
    }
}
