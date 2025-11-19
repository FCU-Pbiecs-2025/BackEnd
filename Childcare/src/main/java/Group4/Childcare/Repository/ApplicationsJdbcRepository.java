package Group4.Childcare.Repository;

import Group4.Childcare.Model.Applications;
import Group4.Childcare.DTO.ApplicationSummaryWithDetailsDTO;
import Group4.Childcare.DTO.ApplicationCaseDTO;
import Group4.Childcare.DTO.ApplicationParticipantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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

        // user name
        dto.setName(rs.getString("Name"));
        dto.setInstitutionName(rs.getString("InstitutionName"));
        dto.setStatus(rs.getString("Status"));
        dto.setInstitutionID(rs.getString("InstitutionID"));
        // populate NationalID from joined child_info (may be null)
        dto.setNationalID(rs.getString("NationalID"));
        // ParticipantType from application_participants
        try { dto.setParticipantType(rs.getString("ParticipantType")); } catch (Exception ex) { dto.setParticipantType(null); }
        // child's name (Cname) from child_info
        try { dto.setPName(rs.getString("PName")); } catch (Exception ex) { /* ignore if field missing */ }

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

    // 依 ApplicationID 查詢單一個案
    public Optional<Applications> getApplicationById(UUID id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ApplicationID = ?";
        List<Applications> result = jdbcTemplate.query(sql, APPLICATIONS_ROW_MAPPER, id.toString());
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    // Find by ID with details
    public Optional<ApplicationSummaryWithDetailsDTO> findApplicationSummaryWithDetailsById(UUID id) {
        String sql = "SELECT a.ApplicationID, a.ApplicationDate, u.Name AS name, i.InstitutionName AS institutionName, ap.Status, ap.ParticipantType AS ParticipantType, ci.NationalID AS NationalID, ci.Name AS Cname " +
                     "FROM applications a " +
                     "LEFT JOIN users u ON a.UserID = u.UserID  " +
                      "LEFT JOIN application_participants ap ON a.ApplicationID = ap.ApplicationID "+
                      "LEFT JOIN institutions i ON a.InstitutionID = i.InstitutionID "+
                      "LEFT JOIN child_info ci ON a.FamilyInfoID = ci.FamilyInfoID " +
                     "WHERE a.ApplicationID = ?";

        return jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> {
            ApplicationSummaryWithDetailsDTO dto = new ApplicationSummaryWithDetailsDTO();
            dto.setApplicationID(UUID.fromString(rs.getString("ApplicationID")));
            dto.setApplicationDate(rs.getDate("ApplicationDate").toLocalDate());
            dto.setName(rs.getString("name"));
            dto.setInstitutionName(rs.getString("institutionName"));
            dto.setStatus(rs.getString("Status"));
            // set NationalID from joined child_info
            dto.setNationalID(rs.getString("NationalID"));
            dto.setParticipantType(rs.getString("ParticipantType"));
            return dto;
        }).stream().findFirst();
    }

    // New: find single application case DTO by ID (single SQL with JOIN)
    public Optional<ApplicationCaseDTO> findApplicationCaseById(UUID id) {
        String sql = "SELECT a.ApplicationID, a.ApplicationDate, a.ReviewDate, a.ReviewUser, i.InstitutionName, " +
                     "ap.ParticipantType, ap.NationalID, ap.Name, ap.Gender, ap.RelationShip, ap.Occupation, " +
                     "ap.PhoneNumber, ap.HouseholdAddress, ap.MailingAddress, ap.Email, ap.BirthDate, " +
                     "ap.IsSuspended, ap.SuspendEnd, ap.CurrentOrder, ap.Status, ap.Reason, ap.ClassID " +
                     "FROM applications a " +
                     "LEFT JOIN institutions i ON a.InstitutionID = i.InstitutionID " +
                     "LEFT JOIN application_participants ap ON a.ApplicationID = ap.ApplicationID " +
                     "WHERE a.ApplicationID = ? " +
                     "ORDER BY ap.CurrentOrder";

        java.util.Map<String, Object> resultMap = new java.util.HashMap<>();

        jdbcTemplate.query(sql, (rs, rowNum) -> {
            // Process application header data (only once)
            if (!resultMap.containsKey("header")) {
                ApplicationCaseDTO dto = new ApplicationCaseDTO();
                if (rs.getString("ApplicationID") != null) {
                    dto.applicationId = UUID.fromString(rs.getString("ApplicationID"));
                }
                if (rs.getDate("ApplicationDate") != null) {
                    dto.applicationDate = rs.getDate("ApplicationDate").toLocalDate();
                }
                java.sql.Timestamp ts = rs.getTimestamp("ReviewDate");
                if (ts != null) dto.reviewDate = ts.toLocalDateTime();
                dto.reviewer = rs.getString("ReviewUser");
                dto.institutionName = rs.getString("InstitutionName");
                dto.parents = new java.util.ArrayList<>();
                dto.children = new java.util.ArrayList<>();
                resultMap.put("header", dto);
            }

            // Process participant data (if exists)
            if (rs.getString("NationalID") != null) {
                ApplicationParticipantDTO p = new ApplicationParticipantDTO();

                // ParticipantType handling
                Object ptObj = null;
                try { ptObj = rs.getObject("ParticipantType"); } catch (Exception ex) { ptObj = null; }
                Boolean isParent = null;
                if (ptObj instanceof Boolean) {
                    try { isParent = rs.getBoolean("ParticipantType"); } catch (Exception ex) { isParent = null; }
                } else if (ptObj != null) {
                    try { int v = rs.getInt("ParticipantType"); isParent = (v == 2); } catch (Exception ex) { isParent = null; }
                }
                p.participantType = (isParent != null && isParent) ? "家長" : "幼兒";

                p.nationalID = rs.getString("NationalID");
                p.name = rs.getString("Name");
                // Gender BIT -> map to 男/女 or null
                try { Object gObj = rs.getObject("Gender"); if (gObj != null) { p.gender = rs.getBoolean("Gender") ? "男" : "女"; } else { p.gender = null; } } catch (Exception ex) { p.gender = null; }
                p.relationShip = rs.getString("RelationShip");
                p.occupation = rs.getString("Occupation");
                p.phoneNumber = rs.getString("PhoneNumber");
                p.householdAddress = rs.getString("HouseholdAddress");
                p.mailingAddress = rs.getString("MailingAddress");
                p.email = rs.getString("Email");
                if (rs.getDate("BirthDate") != null) p.birthDate = rs.getDate("BirthDate").toString();
                try { Object susp = rs.getObject("IsSuspended"); if (susp != null) p.isSuspended = rs.getBoolean("IsSuspended"); else p.isSuspended = null; } catch (Exception ex) { p.isSuspended = null; }
                if (rs.getDate("SuspendEnd") != null) p.suspendEnd = rs.getDate("SuspendEnd").toString();
                try { Object co = rs.getObject("CurrentOrder"); if (co != null) p.currentOrder = rs.getInt("CurrentOrder"); else p.currentOrder = null; } catch (Exception ex) { p.currentOrder = null; }
                p.status = rs.getString("Status");
                p.reason = rs.getString("Reason");
                p.classID = rs.getString("ClassID");

                ApplicationCaseDTO dto = (ApplicationCaseDTO) resultMap.get("header");
                if ("家長".equals(p.participantType)) {
                    dto.parents.add(p);
                } else {
                    dto.children.add(p);
                }
            }

            return null; // We don't need the return value from RowMapper
        }, id.toString());

        if (!resultMap.containsKey("header")) {
            return Optional.empty();
        }

        ApplicationCaseDTO dto = (ApplicationCaseDTO) resultMap.get("header");

        // Determine overall application status: prefer parent status, fallback to child status
        String overallStatus = null;
        for (ApplicationParticipantDTO p : dto.parents) {
            if (p.status != null && !p.status.isEmpty()) { overallStatus = p.status; break; }
        }
        if (overallStatus == null) {
            for (ApplicationParticipantDTO p : dto.children) {
                if (p.status != null && !p.status.isEmpty()) { overallStatus = p.status; break; }
            }
        }
        dto.status = overallStatus;

        return Optional.of(dto);
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
        String sql = "SELECT a.ApplicationID, a.ApplicationDate, u.Name AS Name, i.InstitutionName AS InstitutionName, ap.Status, a.InstitutionID, ap.NationalID AS NationalID, ap.ParticipantType AS ParticipantType,  ap.Name as PName " +
                     "FROM " + TABLE_NAME + " a " +
                     "LEFT JOIN users u ON a.UserID = u.UserID  " +
                     "LEFT JOIN application_participants ap ON a.ApplicationID = ap.ApplicationID "+
                     "LEFT JOIN institutions i ON a.InstitutionID = i.InstitutionID " +
                     "LEFT JOIN child_info ci ON a.FamilyInfoID = ci.FamilyInfoID " +
                     "ORDER BY a.ApplicationDate DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try {
            return jdbcTemplate.query(sql, DETAILS_ROW_MAPPER, offset, limit);
        } catch (Exception e) {
            throw new RuntimeException("Failed to query application summaries with offset=" + offset + " limit=" + limit + ": " + e.getMessage(), e);
        }
    }

    // New: searchApplications method with dynamic SQL
    public List<ApplicationSummaryWithDetailsDTO> searchApplications(String institutionID, String institutionName, String applicationID) {
        StringBuilder sql = new StringBuilder("SELECT a.ApplicationID, a.ApplicationDate, u.Name AS Name, i.InstitutionName AS InstitutionName, ap.Status, a.InstitutionID, ci.NationalID AS NationalID, ap.ParticipantType AS ParticipantType, ci.Name AS Cname, CASE WHEN ap.ParticipantType = 0 THEN ap.Name ELSE NULL END AS PName " +
                "FROM applications a " +
                "LEFT JOIN users u ON a.UserID = u.UserID " +
                "LEFT JOIN application_participants ap ON a.ApplicationID = ap.ApplicationID " +
                "LEFT JOIN institutions i ON a.InstitutionID = i.InstitutionID LEFT JOIN child_info ci ON a.FamilyInfoID = ci.FamilyInfoID WHERE 1=1 ");
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
