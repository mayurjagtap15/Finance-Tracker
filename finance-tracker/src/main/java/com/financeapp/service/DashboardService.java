package com.financeapp.service;

import com.financeapp.dto.DashboardDto;

public interface DashboardService {
    DashboardDto getDashboard(Long userId);
}
