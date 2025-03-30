package com.example.art.web;

import com.example.art.adminBalance.service.AdminBalanceService;
import com.example.art.adminTransaction.model.AdminTransaction;
import com.example.art.adminTransaction.service.AdminTransactionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/balance")
public class AdminBalanceController {

    private final AdminBalanceService adminBalanceService;
    private final AdminTransactionService adminTransactionService;

    public AdminBalanceController(AdminBalanceService adminBalanceService,
                                  AdminTransactionService adminTransactionService) {
        this.adminBalanceService = adminBalanceService;
        this.adminTransactionService = adminTransactionService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ModelAndView getBalancePage() {
        ModelAndView modelAndView = new ModelAndView();
        BigDecimal balance = adminBalanceService.getBalance();
        List<AdminTransaction> transactions = adminTransactionService.getAllTransaction();

        modelAndView.setViewName("balance");
        modelAndView.addObject("balance", balance);
        modelAndView.addObject("transactions", transactions);

        return modelAndView;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/transactions/clear")
    public String clearTransactionHistory() {
        List<AdminTransaction> transactions = adminTransactionService.getAllTransaction();
        adminTransactionService.clearHistory(transactions);
        return "redirect:/balance";
    }
}
