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

    public Announcements findById(UUID id) {
        String sql = "SELECT * FROM announcements WHERE AnnouncementId = ?";
        try (Connection conn = databaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
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
                    return announcement;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Announcements save(Announcements announcement) {
        String sql = "INSERT INTO announcements (AnnouncementId, Title, Content, Type, StartDate, EndDate, Status, CreatedUser, CreatedTime, UpdatedUser, UpdatedTime, AttachmentPath) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, announcement.getAnnouncementId().toString());
            pstmt.setString(2, announcement.getTitle());
            pstmt.setString(3, announcement.getContent());
            pstmt.setByte(4, announcement.getType());
            pstmt.setObject(5, announcement.getStartDate());
            pstmt.setObject(6, announcement.getEndDate());
            pstmt.setByte(7, announcement.getStatus());
            pstmt.setString(8, announcement.getCreatedUser());
            pstmt.setObject(9, announcement.getCreatedTime());
            pstmt.setString(10, announcement.getUpdatedUser());
            pstmt.setObject(11, announcement.getUpdatedTime());
            pstmt.setString(12, announcement.getAttachmentPath());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return announcement;
    }

    public Announcements update(Announcements announcement) {
        String sql = "UPDATE announcements SET Title = ?, Content = ?, Type = ?, StartDate = ?, EndDate = ?, Status = ?, CreatedUser = ?, CreatedTime = ?, UpdatedUser = ?, UpdatedTime = ?, AttachmentPath = ? WHERE AnnouncementId = ?";
        try (Connection conn = databaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, announcement.getTitle());
            pstmt.setString(2, announcement.getContent());
            pstmt.setByte(3, announcement.getType());
            pstmt.setObject(4, announcement.getStartDate());
            pstmt.setObject(5, announcement.getEndDate());
            pstmt.setByte(6, announcement.getStatus());
            pstmt.setString(7, announcement.getCreatedUser());
            pstmt.setObject(8, announcement.getCreatedTime());
            pstmt.setString(9, announcement.getUpdatedUser());
            pstmt.setObject(10, announcement.getUpdatedTime());
            pstmt.setString(11, announcement.getAttachmentPath());
            pstmt.setString(12, announcement.getAnnouncementId().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return announcement;
    }

    public boolean delete(UUID id) {
        String sql = "DELETE FROM announcements WHERE AnnouncementId = ?";
        try (Connection conn = databaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
