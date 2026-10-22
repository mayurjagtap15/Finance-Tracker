package com.financeapp.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class AppErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        int statusCode = status != null ? Integer.parseInt(status.toString()) : 500;
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("statusName", HttpStatus.valueOf(statusCode).getReasonPhrase());
        model.addAttribute("message", message != null && !message.toString().isEmpty()
                ? message.toString()
                : defaultMessage(statusCode));
        return "error";
    }

    private String defaultMessage(int code) {
        if (code == 404) return "The page you are looking for does not exist.";
        if (code == 403) return "You do not have permission to access this page.";
        return "An unexpected error occurred.";
    }
}
