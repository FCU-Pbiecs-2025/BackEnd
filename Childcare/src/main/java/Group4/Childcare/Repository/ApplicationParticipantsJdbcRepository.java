package Group4.Childcare.Repository;

import Group4.Childcare.Model.ApplicationParticipants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ApplicationParticipantsJdbcRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TABLE_NAME = "application_participants";

    private static final RowMapper<ApplicationParticipants> ROW_MAPPER = (rs, rowNum) -> {
        ApplicationParticipants ap = new ApplicationParticipants();
        String applicationIdStr = rs.getString("ApplicationID");
        if (applicationIdStr != null) {
            ap.setApplicationID(UUID.fromString(applicationIdStr));
        }
        ap.setParticipantType(rs.getBoolean("ParticipantType"));
        ap.setNationalID(rs.getString("NationalID"));
        ap.setName(rs.getString("Name"));
        ap.setGender(rs.getBoolean("Gender"));
        ap.setRelationShip(rs.getString("RelationShip"));
        ap.setOccupation(rs.getString("Occupation"));
        ap.setPhoneNumber(rs.getString("PhoneNumber"));
        ap.setHouseholdAddress(rs.getString("HouseholdAddress"));
        ap.setMailingAddress(rs.getString("MailingAddress"));
        ap.setEmail(rs.getString("Email"));
        Date birthDate = rs.getDate("BirthDate");
        if (birthDate != null) {
            ap.setBirthDate(birthDate.toLocalDate());
        }
        ap.setIsSuspended(rs.getBoolean("IsSuspended"));
        Date suspendEnd = rs.getDate("SuspendEnd");
        if (suspendEnd != null) {
            ap.setSuspendEnd(suspendEnd.toLocalDate());
        }
        ap.setCurrentOrder(rs.getInt("CurrentOrder"));
        ap.setStatus(rs.getString("Status"));
        ap.setReason(rs.getString("Reason"));
        String classIdStr = rs.getString("ClassID");
        if (classIdStr != null) {
            ap.setClassID(UUID.fromString(classIdStr));
        }
        return ap;
    };

    public ApplicationParticipants save(ApplicationParticipants ap) {
        if (ap.getApplicationID() == null) {
            ap.setApplicationID(UUID.randomUUID());
            return insert(ap);
        } else {
            return update(ap);
        }
    }

    private ApplicationParticipants insert(ApplicationParticipants ap) {
        String sql = "INSERT INTO " + TABLE_NAME +
                " (ApplicationID, ParticipantType, NationalID, Name, Gender, RelationShip, Occupation, PhoneNumber, HouseholdAddress, MailingAddress, Email, BirthDate, IsSuspended, SuspendEnd, CurrentOrder, Status, Reason, ClassID) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                ap.getApplicationID() != null ? ap.getApplicationID().toString() : null,
                ap.getParticipantType(),
                ap.getNationalID(),
                ap.getName(),
                ap.getGender(),
                ap.getRelationShip(),
                ap.getOccupation(),
                ap.getPhoneNumber(),
                ap.getHouseholdAddress(),
                ap.getMailingAddress(),
                ap.getEmail(),
                ap.getBirthDate(),
                ap.getIsSuspended(),
                ap.getSuspendEnd(),
                ap.getCurrentOrder(),
                ap.getStatus(),
                ap.getReason(),
                ap.getClassID() != null ? ap.getClassID().toString() : null
        );
        return ap;
    }

    private ApplicationParticipants update(ApplicationParticipants ap) {
        String sql = "UPDATE " + TABLE_NAME +
                " SET ParticipantType = ?, NationalID = ?, Name = ?, Gender = ?, RelationShip = ?, Occupation = ?, PhoneNumber = ?, HouseholdAddress = ?, MailingAddress = ?, Email = ?, BirthDate = ?, IsSuspended = ?, SuspendEnd = ?, CurrentOrder = ?, Status = ?, Reason = ?, ClassID = ? WHERE ApplicationID = ?";
        jdbcTemplate.update(sql,
                ap.getParticipantType(),
                ap.getNationalID(),
                ap.getName(),
                ap.getGender(),
                ap.getRelationShip(),
                ap.getOccupation(),
                ap.getPhoneNumber(),
                ap.getHouseholdAddress(),
                ap.getMailingAddress(),
                ap.getEmail(),
                ap.getBirthDate(),
                ap.getIsSuspended(),
                ap.getSuspendEnd(),
                ap.getCurrentOrder(),
                ap.getStatus(),
                ap.getReason(),
                ap.getClassID() != null ? ap.getClassID().toString() : null,
                ap.getApplicationID() != null ? ap.getApplicationID().toString() : null
        );
        return ap;
    }

    public Optional<ApplicationParticipants> findById(UUID id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ApplicationID = ?";
        try {
            ApplicationParticipants ap = jdbcTemplate.queryForObject(sql, ROW_MAPPER, id.toString());
            return Optional.ofNullable(ap);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<ApplicationParticipants> findAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE ApplicationID = ?";
        jdbcTemplate.update(sql, id.toString());
    }

    public void delete(ApplicationParticipants ap) {
        deleteById(ap.getApplicationID());
    }

    public boolean existsById(UUID id) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE ApplicationID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id.toString());
        return count != null && count > 0;
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }
}

