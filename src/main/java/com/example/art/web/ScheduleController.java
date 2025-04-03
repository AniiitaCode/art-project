package com.example.art.web;

import com.example.art.adminBalance.service.AdminBalanceService;
import com.example.art.order.model.Orders;
import com.example.art.order.service.OrderService;
import com.example.art.schedule.service.ScheduleService;
import com.example.art.transaction.model.TransactionType;
import com.example.art.transaction.service.TransactionService;
import com.example.art.user.model.User;
import com.example.art.wallet.model.Wallet;
import com.example.art.wallet.service.WalletService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

    private final OrderService orderService;
    private final ScheduleService scheduleService;
    private final TransactionService transactionService;
    private final AdminBalanceService adminBalanceService;
    private final WalletService walletService;

    public ScheduleController(OrderService orderService,
                              ScheduleService scheduleService,
                              TransactionService transactionService,
                              AdminBalanceService adminBalanceService,
                              WalletService walletService) {
        this.orderService = orderService;
        this.scheduleService = scheduleService;
        this.transactionService = transactionService;
        this.adminBalanceService = adminBalanceService;
        this.walletService = walletService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ModelAndView schedulePage() {
        List<Orders> allOrders = orderService.getAllOrders();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("schedule");
        modelAndView.addObject("allOrders", allOrders);

        return modelAndView;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{orderId}/confirm")
    public ModelAndView acceptOrder(@PathVariable UUID orderId) {

        Orders order = orderService.getOrderById(orderId);
        scheduleService.acceptOrder(order);

        return new ModelAndView("redirect:/schedule");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @PostMapping("/{orderId}")
    public String removeOrder(@PathVariable UUID orderId) {

        Orders order = orderService.getOrderById(orderId);
        User user = order.getUser();
        Wallet wallet = user.getWallet();
        BigDecimal amount = orderService.createBill(order.getDesign()).get("TotalPrice");

        adminBalanceService.withdrawBalance(user, amount);

        transactionService.createTransaction(user, wallet, amount, TransactionType.ADD);
        walletService.addBalance(user, amount);

        scheduleService.deleteById(orderId);
        return "redirect:/schedule";
    }
}
