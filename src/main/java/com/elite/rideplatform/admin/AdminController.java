package com.elite.rideplatform.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminReportingRepository adminReportingRepository;

    public AdminController(AdminReportingRepository adminReportingRepository) {
        this.adminReportingRepository = adminReportingRepository;
    }

    @GetMapping("/reports/daily-stats")
    // Requires an admin role which could be added later; for now, we just restrict it or leave open.
    // @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<List<Map<String, Object>>> getDailyStats() {
        return ResponseEntity.ok(adminReportingRepository.getDailyStatsByCategory());
    }
}
