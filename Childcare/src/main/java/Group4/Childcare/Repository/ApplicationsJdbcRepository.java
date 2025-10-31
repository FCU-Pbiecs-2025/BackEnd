package Group4.Childcare.Repository;

import Group4.Childcare.Model.Applications;
import Group4.Childcare.DTO.ApplicationSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ApplicationsJdbcRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TABLE_NAME = "applications";

    // RowMapper for Applications entity
    private static final RowMapper<Applications> APPLICATIONS_ROW_MAPPER = new RowMapper<Applications>() {
        @Override
        public Applications mapRow(ResultSet rs, int rowNum) throws SQLException {
            Applications application = new Applications();
            application.setApplicationID(UUID.fromString(rs.getString("ApplicationID")));

            if (rs.getDate("ApplicationDate") != null) {
                application.setApplicationDate(rs.getDate("ApplicationDate").toLocalDate());
            }

            if (rs.getString("InstitutionID") != null) {
                application.setInstitutionID(UUID.fromString(rs.getString("InstitutionID")));
            }

            if (rs.getString("UserID") != null) {
                application.setUserID(UUID.fromString(rs.getString("UserID")));
            }

            application.setIdentityType(rs.getByte("IdentityType"));
            application.setAttachmentPath(rs.getString("AttachmentPath"));
            application.setReviewUser(rs.getString("ReviewUser"));
            application.setReason(rs.getString("Reason"));

            return application;
        }
    };

    // RowMapper for ApplicationSummaryDTO
    private static final RowMapper<ApplicationSummaryDTO> SUMMARY_ROW_MAPPER = new RowMapper<ApplicationSummaryDTO>() {
        @Override
        public ApplicationSummaryDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            LocalDate applicationDate = null;
            if (rs.getDate("ApplicationDate") != null) {
                applicationDate = rs.getDate("ApplicationDate").toLocalDate();
            }

            return new ApplicationSummaryDTO(
                UUID.fromString(rs.getString("ApplicationID")),
                applicationDate,
                rs.getString("Reason"),
                rs.getString("Status")
            );
        }
    };

    // Save method
    public Applications save(Applications application) {
        if (application.getApplicationID() == null) {
            application.setApplicationID(UUID.randomUUID());
            return insert(application);
        } else {
            return update(application);
        }
    }

    // Insert method
    private Applications insert(Applications application) {
        String sql = "INSERT INTO " + TABLE_NAME +
                    " (ApplicationID, ApplicationDate, InstitutionID, UserID, IdentityType, " +
                    "AttachmentPath, ReviewUser, Reason) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
            application.getApplicationID().toString(),
            application.getApplicationDate(),
            application.getInstitutionID() != null ? application.getInstitutionID().toString() : null,
            application.getUserID() != null ? application.getUserID().toString() : null,
            application.getIdentityType(),
            application.getAttachmentPath(),
            application.getReviewUser(),
            application.getReason()
        );

        return application;
    }

    // Update method
    private Applications update(Applications application) {
        String sql = "UPDATE " + TABLE_NAME +
                    " SET ApplicationDate = ?, InstitutionID = ?, UserID = ?, IdentityType = ?, " +
                    "AttachmentPath = ?, ReviewUser = ?, Reason = ? WHERE ApplicationID = ?";

        jdbcTemplate.update(sql,
            application.getApplicationDate(),
            application.getInstitutionID() != null ? application.getInstitutionID().toString() : null,
            application.getUserID() != null ? application.getUserID().toString() : null,
            application.getIdentityType(),
            application.getAttachmentPath(),
            application.getReviewUser(),
            application.getReason(),
            application.getApplicationID().toString()
        );

        return application;
    }

    // Find by ID
    public Optional<Applications> findById(UUID id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ApplicationID = ?";
        try {
            Applications application = jdbcTemplate.queryForObject(sql, APPLICATIONS_ROW_MAPPER, id.toString());
            return Optional.ofNullable(application);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Find all
    public List<Applications> findAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        return jdbcTemplate.query(sql, APPLICATIONS_ROW_MAPPER);
    }

    // Delete by ID
    public void deleteById(UUID id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE ApplicationID = ?";
        jdbcTemplate.update(sql, id.toString());
    }

    // Delete entity
    public void delete(Applications application) {
        deleteById(application.getApplicationID());
    }

    // Check if exists by ID
    public boolean existsById(UUID id) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE ApplicationID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id.toString());
        return count != null && count > 0;
    }

    // Count all
    public long count() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }

    // Custom method: Find summary by UserID
    public List<ApplicationSummaryDTO> findSummaryByUserID(UUID userID) {
        String sql = "SELECT a.ApplicationID, a.ApplicationDate, a.Reason, ap.Status " +
                    "FROM " + TABLE_NAME + " a " +
                    "LEFT JOIN application_participants ap ON a.ApplicationID = ap.ApplicationID " +
                    "WHERE a.UserID = ?";
        return jdbcTemplate.query(sql, SUMMARY_ROW_MAPPER, userID.toString());
    }
}
