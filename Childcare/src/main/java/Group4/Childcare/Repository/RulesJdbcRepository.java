package Group4.Childcare.Repository;

import Group4.Childcare.Model.Rules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class RulesJdbcRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TABLE_NAME = "rules";

    // RowMapper for Rules entity
    private static final RowMapper<Rules> RULES_ROW_MAPPER = new RowMapper<Rules>() {
        @Override
        public Rules mapRow(ResultSet rs, int rowNum) throws SQLException {
            Rules rules = new Rules();
            rules.setId(rs.getLong("id"));
            rules.setAdmissionEligibility(rs.getString("AdmissionEligibility"));
            rules.setServiceContentAndTime(rs.getString("ServiceContentAndTime"));
            rules.setFeeAndRefundPolicy(rs.getString("FeeAndRefundPolicy"));
            return rules;
        }
    };

    // Save method
    public Rules save(Rules rules) {
        if (rules.getId() == null || rules.getId() == 0) {
            return insert(rules);
        } else {
            return update(rules);
        }
    }

    // Insert method (for auto-generated ID)
    private Rules insert(Rules rules) {
        String sql = "INSERT INTO " + TABLE_NAME +
                    " (AdmissionEligibility, ServiceContentAndTime, FeeAndRefundPolicy) " +
                    "VALUES (?, ?, ?)";

        jdbcTemplate.update(sql,
            rules.getAdmissionEligibility(),
            rules.getServiceContentAndTime(),
            rules.getFeeAndRefundPolicy()
        );

        // Note: In a real application, you might want to retrieve the generated ID
        // This is a simplified version
        return rules;
    }

    // Update method
    private Rules update(Rules rules) {
        String sql = "UPDATE " + TABLE_NAME +
                    " SET AdmissionEligibility = ?, ServiceContentAndTime = ?, FeeAndRefundPolicy = ? " +
                    "WHERE id = ?";

        jdbcTemplate.update(sql,
            rules.getAdmissionEligibility(),
            rules.getServiceContentAndTime(),
            rules.getFeeAndRefundPolicy(),
            rules.getId()
        );

        return rules;
    }

    // Find by ID
    public Optional<Rules> findById(Long id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try {
            Rules rules = jdbcTemplate.queryForObject(sql, RULES_ROW_MAPPER, id);
            return Optional.ofNullable(rules);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Find all
    public List<Rules> findAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        return jdbcTemplate.query(sql, RULES_ROW_MAPPER);
    }

    // Delete by ID
    public void deleteById(Long id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // Delete entity
    public void delete(Rules rules) {
        deleteById(rules.getId());
    }

    // Check if exists by ID
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    // Count all
    public long count() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }
}
