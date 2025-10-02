package Group4.Childcare.Repository;

import Group4.Childcare.Model.Announcements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import Group4.Childcare.Service.DatabaseService;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class AnnouncementsRepository {
    @Autowired
    private DatabaseService databaseService;

    public List<Announcements> findAll() {
        List<Announcements> announcements = new ArrayList<>();
        String sql = "SELECT * FROM announcements";
        try (Connection conn = databaseService.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Announcements announcement = new Announcements();
                announcement.setAnnouncementId(UUID.fromString(rs.getString("AnnouncementId")));
                announcement.setTitle(rs.getString("Title"));
                announcement.setContent(rs.getString("Content"));
                announcement.setType(rs.getByte("Type"));
                announcement.setStartDate(rs.getObject("StartDate", LocalDate.class));
                announcement.setEndDate(rs.getObject("EndDate", LocalDate.class));
                announcement.setStatus(rs.getByte("Status"));
                announcement.setCreatedUser(rs.getString("CreatedUser"));
                announcement.setCreatedTime(rs.getObject("CreatedTime", LocalDateTime.class));
                announcement.setUpdatedUser(rs.getString("UpdatedUser"));
                announcement.setUpdatedTime(rs.getObject("UpdatedTime", LocalDateTime.class));
                announcement.setAttachmentPath(rs.getString("AttachmentPath"));
                announcements.add(announcement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return announcements;
    }
}
