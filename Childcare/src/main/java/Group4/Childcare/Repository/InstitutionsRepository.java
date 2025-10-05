package Group4.Childcare.Repository;

import Group4.Childcare.Model.Institutions;
import Group4.Childcare.Service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class InstitutionsRepository {
    @Autowired
    private DatabaseService databaseService;

    // 取得所有機構
    public List<Institutions> findAll() {
        List<Institutions> institutionsList = new ArrayList<>();
        String sql = "SELECT * FROM institutions";
        try (Connection conn = databaseService.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Institutions inst = new Institutions();
                inst.setInstitutionId(UUID.fromString(rs.getString("InstitutionId")));
                inst.setInstitutionName(rs.getString("InstitutionName"));
                inst.setCommissionUnit(rs.getString("CommissionUnit"));
                inst.setContactPerson(rs.getString("ContactPerson"));
                inst.setAddress(rs.getString("Address"));
                inst.setPhoneNumber(rs.getString("PhoneNumber"));
                inst.setFax(rs.getString("Fax"));
                inst.setEmail(rs.getString("Email"));
                inst.setRelatedLinks(rs.getString("RelatedLinks"));
                inst.setDescription(rs.getString("Description"));
                inst.setAdditionalInfo(rs.getString("AdditionalInfo"));
                inst.setResponsiblePerson(rs.getString("ResponsiblePerson"));
                inst.setImagePath(rs.getString("ImagePath"));
                inst.setCreatedUser(rs.getString("CreatedUser"));
                inst.setCreatedTime(rs.getObject("CreatedTime", LocalDateTime.class));
                inst.setUpdatedUser(rs.getString("UpdatedUser"));
                inst.setUpdatedTime(rs.getObject("UpdatedTime", LocalDateTime.class));
                institutionsList.add(inst);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return institutionsList;
    }

    // 依ID取得機構
    public Institutions findById(UUID id) {
        String sql = "SELECT * FROM institutions WHERE InstitutionId = ?";
        try (Connection conn = databaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Institutions inst = new Institutions();
                    inst.setInstitutionId(UUID.fromString(rs.getString("InstitutionId")));
                    inst.setInstitutionName(rs.getString("InstitutionName"));
                    inst.setCommissionUnit(rs.getString("CommissionUnit"));
                    inst.setContactPerson(rs.getString("ContactPerson"));
                    inst.setAddress(rs.getString("Address"));
                    inst.setPhoneNumber(rs.getString("PhoneNumber"));
                    inst.setFax(rs.getString("Fax"));
                    inst.setEmail(rs.getString("Email"));
                    inst.setRelatedLinks(rs.getString("RelatedLinks"));
                    inst.setDescription(rs.getString("Description"));
                    inst.setAdditionalInfo(rs.getString("AdditionalInfo"));
                    inst.setResponsiblePerson(rs.getString("ResponsiblePerson"));
                    inst.setImagePath(rs.getString("ImagePath"));
                    inst.setCreatedUser(rs.getString("CreatedUser"));
                    inst.setCreatedTime(rs.getObject("CreatedTime", LocalDateTime.class));
                    inst.setUpdatedUser(rs.getString("UpdatedUser"));
                    inst.setUpdatedTime(rs.getObject("UpdatedTime", LocalDateTime.class));
                    return inst;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 新增機構
    public Institutions save(Institutions inst) {
        String sql = "INSERT INTO institutions (InstitutionId, InstitutionName, CommissionUnit, ContactPerson, Address, PhoneNumber, Fax, Email, RelatedLinks, Description, AdditionalInfo, ResponsiblePerson, ImagePath, CreatedUser, CreatedTime, UpdatedUser, UpdatedTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, inst.getInstitutionId().toString());
            pstmt.setString(2, inst.getInstitutionName());
            pstmt.setString(3, inst.getCommissionUnit());
            pstmt.setString(4, inst.getContactPerson());
            pstmt.setString(5, inst.getAddress());
            pstmt.setString(6, inst.getPhoneNumber());
            pstmt.setString(7, inst.getFax());
            pstmt.setString(8, inst.getEmail());
            pstmt.setString(9, inst.getRelatedLinks());
            pstmt.setString(10, inst.getDescription());
            pstmt.setString(11, inst.getAdditionalInfo());
            pstmt.setString(12, inst.getResponsiblePerson());
            pstmt.setString(13, inst.getImagePath());
            pstmt.setString(14, inst.getCreatedUser());
            pstmt.setObject(15, inst.getCreatedTime());
            pstmt.setString(16, inst.getUpdatedUser());
            pstmt.setObject(17, inst.getUpdatedTime());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inst;
    }

    // 更新機構
    public Institutions update(Institutions inst) {
        String sql = "UPDATE institutions SET InstitutionName = ?, CommissionUnit = ?, ContactPerson = ?, Address = ?, PhoneNumber = ?, Fax = ?, Email = ?, RelatedLinks = ?, Description = ?, AdditionalInfo = ?, ResponsiblePerson = ?, ImagePath = ?, CreatedUser = ?, CreatedTime = ?, UpdatedUser = ?, UpdatedTime = ? WHERE InstitutionId = ?";
        try (Connection conn = databaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, inst.getInstitutionName());
            pstmt.setString(2, inst.getCommissionUnit());
            pstmt.setString(3, inst.getContactPerson());
            pstmt.setString(4, inst.getAddress());
            pstmt.setString(5, inst.getPhoneNumber());
            pstmt.setString(6, inst.getFax());
            pstmt.setString(7, inst.getEmail());
            pstmt.setString(8, inst.getRelatedLinks());
            pstmt.setString(9, inst.getDescription());
            pstmt.setString(10, inst.getAdditionalInfo());
            pstmt.setString(11, inst.getResponsiblePerson());
            pstmt.setString(12, inst.getImagePath());
            pstmt.setString(13, inst.getCreatedUser());
            pstmt.setObject(14, inst.getCreatedTime());
            pstmt.setString(15, inst.getUpdatedUser());
            pstmt.setObject(16, inst.getUpdatedTime());
            pstmt.setString(17, inst.getInstitutionId().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inst;
    }

    // 刪除機構
    public boolean delete(UUID id) {
        String sql = "DELETE FROM institutions WHERE InstitutionId = ?";
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

