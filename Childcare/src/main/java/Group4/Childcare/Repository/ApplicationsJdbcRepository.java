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
            "AttachmentPath) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    jdbcTemplate.update(sql,
            application.getApplicationID().toString(),
            application.getApplicationDate(),
            application.getInstitutionID() != null ? application.getInstitutionID().toString() : null,
            application.getUserID() != null ? application.getUserID().toString() : null,
            application.getIdentityType(),
            application.getAttachmentPath()
    );

    return application;
  }

  // Update method
  private Applications update(Applications application) {
    String sql = "UPDATE " + TABLE_NAME +
            " SET ApplicationDate = ?, InstitutionID = ?, UserID = ?, IdentityType = ?, " +
            "AttachmentPath = ? WHERE ApplicationID = ?";

    jdbcTemplate.update(sql,
            application.getApplicationDate(),
            application.getInstitutionID() != null ? application.getInstitutionID().toString() : null,
            application.getUserID() != null ? application.getUserID().toString() : null,
            application.getIdentityType(),
            application.getAttachmentPath(),
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
    return Optional.of(result.getFirst());
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
  public Optional<ApplicationCaseDTO> findApplicationCaseById(UUID id, String nationalID) {
    String sql = "SELECT a.ApplicationID, a.ApplicationDate, i.InstitutionName, " +
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
        // Review information stored in database but not exposed in ApplicationCaseDTO
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
          // only add child if no nationalID filter OR it matches the provided nationalID
          if (nationalID == null || nationalID.isEmpty() || nationalID.equals(p.nationalID)) {
            dto.children.add(p);
          }
        }
      }

      return null; // We don't need the return value from RowMapper
    }, id.toString());

    if (!resultMap.containsKey("header")) {
      return Optional.empty();
    }

    ApplicationCaseDTO dto = (ApplicationCaseDTO) resultMap.get("header");

    // ApplicationCaseDTO no longer contains an overall status field. If callers require an overall
    // status in future, compute it there from participant statuses.

    return Optional.of(dto);
  }

  // New: update/insert participants and application header from ApplicationCaseDTO
  public void updateApplicationCase(UUID id, Group4.Childcare.DTO.ApplicationCaseDTO dto) {
    // Update application review info (ReviewUser, ReviewDate) if provided
    if (dto != null) {
      try {
        String updateAppSql = "UPDATE applications SET ReviewUser = ?, ReviewDate = ? WHERE ApplicationID = ?";
        // Note: ReviewUser and ReviewDate are not exposed in ApplicationCaseDTO
        // This update is skipped since the DTO does not contain these fields
        // jdbcTemplate.update(updateAppSql, dto.reviewer, reviewTs, id.toString());
      } catch (Exception ex) {
        // swallow and continue; don't fail whole batch on review update
      }

      java.util.List<Group4.Childcare.DTO.ApplicationParticipantDTO> participants = new java.util.ArrayList<>();
      if (dto.parents != null) participants.addAll(dto.parents);
      if (dto.children != null) participants.addAll(dto.children);

      for (Group4.Childcare.DTO.ApplicationParticipantDTO p : participants) {
        if (p == null || p.nationalID == null || p.nationalID.isEmpty()) continue; // need nationalID to identify record

        // Convert DTO fields to DB types
        Boolean participantType = null;
        if (p.participantType != null) participantType = "家長".equals(p.participantType);
        Boolean gender = null;
        if (p.gender != null) gender = "男".equals(p.gender);
        java.sql.Date birthDate = null;
        if (p.birthDate != null && !p.birthDate.isEmpty()) {
          try { birthDate = java.sql.Date.valueOf(java.time.LocalDate.parse(p.birthDate)); } catch (Exception ex) { birthDate = null; }
        }
        java.sql.Date suspendEnd = null;
        if (p.suspendEnd != null && !p.suspendEnd.isEmpty()) {
          try { suspendEnd = java.sql.Date.valueOf(java.time.LocalDate.parse(p.suspendEnd)); } catch (Exception ex) { suspendEnd = null; }
        }
        java.util.UUID classUUID = null;
        if (p.classID != null && !p.classID.isEmpty()) {
          try { classUUID = java.util.UUID.fromString(p.classID); } catch (Exception ex) { classUUID = null; }
        }

        // Try update first
        String updateSql = "UPDATE application_participants SET ParticipantType = ?, Name = ?, Gender = ?, RelationShip = ?, Occupation = ?, PhoneNumber = ?, HouseholdAddress = ?, MailingAddress = ?, Email = ?, BirthDate = ?, IsSuspended = ?, SuspendEnd = ?, CurrentOrder = ?, Status = ?, Reason = ?, ClassID = ? " +
                "WHERE ApplicationID = ? AND NationalID = ?";
        int updated = 0;
        try {
          updated = jdbcTemplate.update(updateSql,
                  participantType,
                  p.name,
                  gender,
                  p.relationShip,
                  p.occupation,
                  p.phoneNumber,
                  p.householdAddress,
                  p.mailingAddress,
                  p.email,
                  birthDate,
                  p.isSuspended,
                  suspendEnd,
                  p.currentOrder,
                  p.status,
                  p.reason,
                  classUUID != null ? classUUID.toString() : null,
                  id.toString(),
                  p.nationalID
          );
        } catch (Exception ex) {
          updated = 0;
        }

        if (updated == 0) {
          // Insert new record
          String insertSql = "INSERT INTO application_participants (ApplicationID, ParticipantType, NationalID, Name, Gender, RelationShip, Occupation, PhoneNumber, HouseholdAddress, MailingAddress, Email, BirthDate, IsSuspended, SuspendEnd, CurrentOrder, Status, Reason, ClassID) " +
                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
          try {
            jdbcTemplate.update(insertSql,
                    id.toString(),
                    participantType,
                    p.nationalID,
                    p.name,
                    gender,
                    p.relationShip,
                    p.occupation,
                    p.phoneNumber,
                    p.householdAddress,
                    p.mailingAddress,
                    p.email,
                    birthDate,
                    p.isSuspended,
                    suspendEnd,
                    p.currentOrder,
                    p.status,
                    p.reason,
                    classUUID != null ? classUUID.toString() : null
            );
          } catch (Exception ex) {
            // ignore individual insert failures
          }
        }
      }
    }
  }

  // New: update only a single participant's Status and Reason, and optionally update review info
  public void updateParticipantStatusReason(UUID id, String nationalID, String status, String reason, String reviewer, java.time.LocalDateTime reviewDate) {
    if (nationalID == null || nationalID.isEmpty()) return;
    try {
      String updateAppSql = "UPDATE applications SET ReviewUser = ?, ReviewDate = ? WHERE ApplicationID = ?";
      java.sql.Timestamp ts = null;
      if (reviewDate != null) ts = java.sql.Timestamp.valueOf(reviewDate);
      jdbcTemplate.update(updateAppSql, reviewer, ts, id.toString());
    } catch (Exception ex) {
      // ignore review update failures
    }
    try {
      String sql = "UPDATE application_participants SET Status = ?, Reason = ? WHERE ApplicationID = ? AND NationalID = ?";
      jdbcTemplate.update(sql, status, reason, id.toString(), nationalID);
    } catch (Exception ex) {
      // ignore individual update failure
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

