package Group4.Childcare.Repository;

import Group4.Childcare.Model.Announcements;
import Group4.Childcare.DTO.AnnouncementSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AnnouncementsJdbcRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TABLE_NAME = "announcements";

    // RowMapper for Announcements entity
    private static final RowMapper<Announcements> ANNOUNCEMENTS_ROW_MAPPER = (rs, rowNum) -> {
        Announcements announcement = new Announcements();
        String announcementIdStr = rs.getString("AnnouncementID");
        if (announcementIdStr != null) {
            announcement.setAnnouncementID(UUID.fromString(announcementIdStr));
        }
        announcement.setTitle(rs.getString("Title"));
        announcement.setContent(rs.getString("Content"));
        announcement.setType(rs.getByte("Type"));
        Date startDate = rs.getDate("StartDate");
        if (startDate != null) {
            announcement.setStartDate(startDate.toLocalDate());
        }
        Date endDate = rs.getDate("EndDate");
        if (endDate != null) {
            announcement.setEndDate(endDate.toLocalDate());
        }
        announcement.setStatus(rs.getByte("Status"));
        announcement.setCreatedUser(rs.getString("CreatedUser"));
        Timestamp createdTime = rs.getTimestamp("CreatedTime");
        if (createdTime != null) {
            announcement.setCreatedTime(createdTime.toLocalDateTime());
        }
        announcement.setUpdatedUser(rs.getString("UpdatedUser"));
        Timestamp updatedTime = rs.getTimestamp("UpdatedTime");
        if (updatedTime != null) {
            announcement.setUpdatedTime(updatedTime.toLocalDateTime());
        }
        return announcement;
    };

    // RowMapper for AnnouncementSummaryDTO
    private static final RowMapper<AnnouncementSummaryDTO> SUMMARY_ROW_MAPPER = (rs, rowNum) -> {
        LocalDate startDate = null;
        if (rs.getDate("StartDate") != null) {
            startDate = rs.getDate("StartDate").toLocalDate();
        }
        return new AnnouncementSummaryDTO(
            UUID.fromString(rs.getString("AnnouncementID")),
            rs.getString("Title"),
            rs.getString("Content"),
            startDate
        );
    };

    // Save method
    public Announcements save(Announcements announcement) {
        if (announcement.getAnnouncementID() == null) {
            announcement.setAnnouncementID(UUID.randomUUID());
            return insert(announcement);
        } else {
            return update(announcement);
        }
    }

    // Insert method
    private Announcements insert(Announcements announcement) {
        String sql = "INSERT INTO " + TABLE_NAME +
                    " (AnnouncementID, Title, Content, Type, StartDate, EndDate, Status, " +
                    "CreatedUser, CreatedTime, UpdatedUser, UpdatedTime) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
            announcement.getAnnouncementID() != null ? announcement.getAnnouncementID().toString() : null,
            announcement.getTitle(),
            announcement.getContent(),
            announcement.getType(),
            announcement.getStartDate(),
            announcement.getEndDate(),
            announcement.getStatus(),
            announcement.getCreatedUser(),
            announcement.getCreatedTime(),
            announcement.getUpdatedUser(),
            announcement.getUpdatedTime()
        );
        return announcement;
    }

    // Update method
    private Announcements update(Announcements announcement) {
        String sql = "UPDATE " + TABLE_NAME +
                    " SET Title = ?, Content = ?, Type = ?, StartDate = ?, EndDate = ?, Status = ?, " +
                    "CreatedUser = ?, CreatedTime = ?, UpdatedUser = ?, UpdatedTime = ? " +
                    "WHERE AnnouncementID = ?";
        jdbcTemplate.update(sql,
            announcement.getTitle(),
            announcement.getContent(),
            announcement.getType(),
            announcement.getStartDate(),
            announcement.getEndDate(),
            announcement.getStatus(),
            announcement.getCreatedUser(),
            announcement.getCreatedTime(),
            announcement.getUpdatedUser(),
            announcement.getUpdatedTime(),
            announcement.getAnnouncementID() != null ? announcement.getAnnouncementID().toString() : null
        );
        return announcement;
    }

    // Find by ID
    public Optional<Announcements> findById(UUID id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE AnnouncementID = ?";
        try {
            Announcements announcement = jdbcTemplate.queryForObject(sql, ANNOUNCEMENTS_ROW_MAPPER, id.toString());
            return Optional.ofNullable(announcement);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Find all
    public List<Announcements> findAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        return jdbcTemplate.query(sql, ANNOUNCEMENTS_ROW_MAPPER);
    }

    // Delete by ID
    public void deleteById(UUID id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE AnnouncementID = ?";
        jdbcTemplate.update(sql, id.toString());
    }

    // Delete entity
    public void delete(Announcements announcement) {
        deleteById(announcement.getAnnouncementID());
    }

    // Check if exists by ID
    public boolean existsById(UUID id) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE AnnouncementID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id.toString());
        return count != null && count > 0;
    }

    // Count all
    public long count() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }

    // Custom method: Find summary data
    public List<AnnouncementSummaryDTO> findSummaryData() {
        String sql = "SELECT AnnouncementID, Title, Content, StartDate FROM " + TABLE_NAME;
        return jdbcTemplate.query(sql, SUMMARY_ROW_MAPPER);
    }
}
