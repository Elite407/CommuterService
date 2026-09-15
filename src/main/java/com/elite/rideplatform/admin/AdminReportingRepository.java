package com.elite.rideplatform.admin;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AdminReportingRepository {

    private final JdbcTemplate jdbcTemplate;

    public AdminReportingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * An example of a complex analytical query where JdbcTemplate is preferred over JPA.
     * Calculates total trips, average distance, and total revenue grouped by vehicle category.
     */
    public List<Map<String, Object>> getDailyStatsByCategory() {
        String sql = """
            SELECT 
                v.category,
                COUNT(t.trip_id) as total_trips,
                AVG(t.distance_km) as avg_distance_km,
                SUM(t.final_fare) as total_revenue
            FROM rp_trips t
            JOIN rp_vehicles v ON t.vehicle_id = v.vehicle_id
            WHERE t.status = 'COMPLETED'
              AND t.created_at >= CURRENT_DATE
            GROUP BY v.category
            """;
            
        return jdbcTemplate.queryForList(sql);
    }
}
