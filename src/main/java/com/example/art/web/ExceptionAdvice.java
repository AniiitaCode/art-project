package com.example.art.web;

import com.example.art.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class ExceptionAdvice {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyException(Exception exception) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("server-error");

        return modelAndView;
    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public String handleUsernameAlreadyExist(RedirectAttributes redirectAttributes,
                                             UsernameAlreadyExistException exception) {

        String message = exception.getMessage();
        redirectAttributes.addFlashAttribute("usernameAlreadyExistMessage", message);
        return "redirect:/register";
    }

    @ExceptionHandler(DateAndTimeAlreadyExistException.class)
    public ModelAndView handleDateAndTimeAlreadyExist() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("exist-error");

        return modelAndView;
    }

    @ExceptionHandler(DateMustBeInFutureException.class)
    public ModelAndView handleDateMustBeInFuture() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error-bad-request");

        return modelAndView;
    }

    @ExceptionHandler(InvalidTimeException.class)
    public ModelAndView handleInvalidTime() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error-invalid-time");

        return modelAndView;
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ModelAndView handleInsufficientBalance() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error-insufficient-balance");

        return modelAndView;
    }

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error-not-found");

        return modelAndView;
    }
}
