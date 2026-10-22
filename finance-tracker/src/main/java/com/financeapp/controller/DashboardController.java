package com.financeapp.controller;

import com.financeapp.dto.DashboardDto;
import com.financeapp.model.User;
import com.financeapp.service.DashboardService;
import com.financeapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        DashboardDto dashboard = dashboardService.getDashboard(user.getId());
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("user", user);
        return "dashboard";
    }
}
