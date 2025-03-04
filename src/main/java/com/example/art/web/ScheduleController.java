package com.example.art.web;

import com.example.art.order.model.Orders;
import com.example.art.order.service.OrderService;
import com.example.art.schedule.service.ScheduleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

    private final OrderService orderService;
    private final ScheduleService scheduleService;

    public ScheduleController(OrderService orderService,
                              ScheduleService scheduleService) {
        this.orderService = orderService;
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public ModelAndView schedulePage() {
        List<Orders> allOrders = orderService.getAllOrders();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("schedule");
        modelAndView.addObject("allOrders", allOrders);

        return modelAndView;
    }

    @PostMapping("/{orderId}/confirm")
    public ModelAndView acceptOrder(@PathVariable UUID orderId) {

        Orders order = orderService.getOrderById(orderId);
        scheduleService.acceptOrder(order);

        return new ModelAndView("redirect:/schedule");
    }

    @DeleteMapping("/{orderId}")
    public String removeOrder(@PathVariable UUID orderId) {
        scheduleService.deleteById(orderId);
        return "redirect:/schedule";
    }
}
