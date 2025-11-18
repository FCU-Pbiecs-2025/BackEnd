package Group4.Childcare.Repository;

import Group4.Childcare.Model.Applications;
import Group4.Childcare.DTO.ApplicationSummaryDTO;
import Group4.Childcare.DTO.ApplicationSummaryWithDetailsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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
    // Simplify RowMapper using lambda expressions
    private static final RowMapper<Applications> APPLICATIONS_ROW_MAPPER = (rs, rowNum) -> {
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
        return application;
    };



    // RowMapper for ApplicationSummaryWithDetailsDTO
    private static final RowMapper<ApplicationSummaryWithDetailsDTO> DETAILS_ROW_MAPPER = (rs, rowNum) -> {
        ApplicationSummaryWithDetailsDTO dto = new ApplicationSummaryWithDetailsDTO();
        dto.setApplicationID(UUID.fromString(rs.getString("ApplicationID")));

        if (rs.getDate("ApplicationDate") != null) {
            dto.setApplicationDate(rs.getDate("ApplicationDate").toLocalDate());
        }

        dto.setName(rs.getString("Name"));
        dto.setInstitutionName(rs.getString("InstitutionName"));
        dto.setStatus(rs.getString("Status"));
        dto.setInstitutionID(rs.getString("InstitutionID"));

        return dto;
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
        // Removed Reason from SQL insert statement
        String sql = "INSERT INTO " + TABLE_NAME +
                    " (ApplicationID, ApplicationDate, InstitutionID, UserID, IdentityType, " +
                    "AttachmentPath, ReviewUser) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
            application.getApplicationID().toString(),
            application.getApplicationDate(),
            application.getInstitutionID() != null ? application.getInstitutionID().toString() : null,
            application.getUserID() != null ? application.getUserID().toString() : null,
            application.getIdentityType(),
            application.getAttachmentPath(),
            application.getReviewUser()
        );

        return application;
    }

    // Update method
    private Applications update(Applications application) {
        String sql = "UPDATE " + TABLE_NAME +
                    " SET ApplicationDate = ?, InstitutionID = ?, UserID = ?, IdentityType = ?, " +
                    "AttachmentPath = ?, ReviewUser = ? WHERE ApplicationID = ?";

        jdbcTemplate.update(sql,
            application.getApplicationDate(),
            application.getInstitutionID() != null ? application.getInstitutionID().toString() : null,
            application.getUserID() != null ? application.getUserID().toString() : null,
            application.getIdentityType(),
            application.getAttachmentPath(),
            application.getReviewUser(),
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

    // Find by ID with details
    public Optional<ApplicationSummaryWithDetailsDTO> findApplicationSummaryWithDetailsById(UUID id) {
        String sql = "SELECT a.ApplicationID, a.ApplicationDate, u.Name AS name, i.InstitutionName AS institutionName, ap.Status " +
                     "FROM applications a " +
                     "LEFT JOIN users u ON a.UserID = u.UserID  " +
                      "LEFT JOIN application_participants ap ON a.ApplicationID = ap.ApplicationID "+
                      "LEFT JOIN institutions i ON a.InstitutionID = i.InstitutionID "+
                     "WHERE a.ApplicationID = ?";

        return jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> {
            ApplicationSummaryWithDetailsDTO dto = new ApplicationSummaryWithDetailsDTO();
            dto.setApplicationID(UUID.fromString(rs.getString("ApplicationID")));
            dto.setApplicationDate(rs.getDate("ApplicationDate").toLocalDate());
            dto.setName(rs.getString("name"));
            dto.setInstitutionName(rs.getString("institutionName"));
            dto.setStatus(rs.getString("Status"));
            return dto;
        }).stream().findFirst();
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

//    // Custom method: Find summary by UserID
//    public List<ApplicationSummaryDTO> findSummaryByUserID(UUID userID) {
//        String sql = "SELECT a.ApplicationID, a.ApplicationDate, ap.Status " +
//                    "FROM " + TABLE_NAME + " a " +
//                    "LEFT JOIN application_participants ap ON a.ApplicationID = ap.ApplicationID " +
//                    "WHERE a.UserID = ?";
//        return jdbcTemplate.query(sql, SUMMARY_ROW_MAPPER, userID.toString());
//    }

    // New: find summaries with offset/limit using JDBC (joins to get participant name and institution name)
    public List<ApplicationSummaryWithDetailsDTO> findSummariesWithOffset(int offset, int limit) {
        // Use SQL Server style pagination (OFFSET .. ROWS FETCH NEXT .. ROWS ONLY) to match other repositories
        String sql = "SELECT a.ApplicationID, a.ApplicationDate, u.Name AS Name, i.InstitutionName AS InstitutionName, ap.Status, a.InstitutionID " +
                     "FROM " + TABLE_NAME + " a " +
                     "LEFT JOIN users u ON a.UserID = u.UserID  " +
                     "LEFT JOIN application_participants ap ON a.ApplicationID = ap.ApplicationID "+
                     "LEFT JOIN institutions i ON a.InstitutionID = i.InstitutionID " +
                     "ORDER BY a.ApplicationDate DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try {
            return jdbcTemplate.query(sql, DETAILS_ROW_MAPPER, offset, limit);
        } catch (Exception e) {
            // wrap and rethrow so controller/service can handle
            throw new RuntimeException("Failed to query application summaries with offset=" + offset + " limit=" + limit + ": " + e.getMessage(), e);
        }
    }

    // New: searchApplications method with dynamic SQL
    public List<ApplicationSummaryWithDetailsDTO> searchApplications(String institutionID, String institutionName, String applicationID) {
        StringBuilder sql = new StringBuilder("SELECT a.ApplicationID, a.ApplicationDate, u.Name AS Name, i.InstitutionName AS InstitutionName, ap.Status, a.InstitutionID " +
                "FROM applications a " +
                "LEFT JOIN users u ON a.UserID = u.UserID " +
                "LEFT JOIN application_participants ap ON a.ApplicationID = ap.ApplicationID " +
                "LEFT JOIN institutions i ON a.InstitutionID = i.InstitutionID WHERE 1=1 ");
        // 動態組合 where 條件
        if (institutionID != null && !institutionID.isEmpty()) {
            sql.append(" AND a.InstitutionID = ? ");
        }
        if (institutionName != null && !institutionName.isEmpty()) {
            sql.append(" AND i.InstitutionName LIKE ? ");
        }
        if (applicationID != null && !applicationID.isEmpty()) {
            sql.append(" AND a.ApplicationID = ? ");
        }
        sql.append(" ORDER BY a.ApplicationDate DESC ");

        // 動態組合參數
        java.util.List<Object> params = new java.util.ArrayList<>();
        if (institutionID != null && !institutionID.isEmpty()) {
            params.add(institutionID);
        }
        if (institutionName != null && !institutionName.isEmpty()) {
            params.add("%" + institutionName + "%");
        }
        if (applicationID != null && !applicationID.isEmpty()) {
            params.add(applicationID);
        }
        return jdbcTemplate.query(sql.toString(), params.toArray(), DETAILS_ROW_MAPPER);
    }
}
