package Group4.Childcare.Repository;

import Group4.Childcare.Model.Users;
import Group4.Childcare.Service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class UsersRepository {
    @Autowired
    private DatabaseService databaseService;

    // 新增帳號
    public Users save(Users user) {
        String sql = "INSERT INTO users (UserId, Account, Password, AccountStatus, PermissionType, FamilyInfoId, InstitutionId) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUserId().toString());
            pstmt.setString(2, user.getAccount());
            pstmt.setString(3, user.getPassword());
            pstmt.setByte(4, user.getAccountStatus());
            pstmt.setByte(5, user.getPermissionType());
            pstmt.setString(6, user.getFamilyInfoId() != null ? user.getFamilyInfoId().toString() : null);
            pstmt.setString(7, user.getInstitutionId() != null ? user.getInstitutionId().toString() : null);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    // 更新會員資料（帳號不能被更改）
    public Users update(Users user) {
        String sql = "UPDATE users SET Password = ?, AccountStatus = ?, PermissionType = ?, FamilyInfoId = ?, InstitutionId = ? WHERE UserId = ?";
        try (Connection conn = databaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getPassword());
            pstmt.setByte(2, user.getAccountStatus());
            pstmt.setByte(3, user.getPermissionType());
            pstmt.setString(4, user.getFamilyInfoId() != null ? user.getFamilyInfoId().toString() : null);
            pstmt.setString(5, user.getInstitutionId() != null ? user.getInstitutionId().toString() : null);
            pstmt.setString(6, user.getUserId().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    // 依ID查詢會員
    public Users findById(UUID id) {
        String sql = "SELECT * FROM users WHERE UserId = ?";
        try (Connection conn = databaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Users user = new Users();
                    user.setUserId(UUID.fromString(rs.getString("UserId")));
                    user.setAccount(rs.getString("Account"));
                    user.setPassword(rs.getString("Password"));
                    user.setAccountStatus(rs.getByte("AccountStatus"));
                    user.setPermissionType(rs.getByte("PermissionType"));
                    String familyInfoId = rs.getString("FamilyInfoId");
                    user.setFamilyInfoId(familyInfoId != null ? UUID.fromString(familyInfoId) : null);
                    String institutionId = rs.getString("InstitutionId");
                    user.setInstitutionId(institutionId != null ? UUID.fromString(institutionId) : null);
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 查詢所有會員
    public List<Users> findAll() {
        List<Users> usersList = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = databaseService.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Users user = new Users();
                user.setUserId(UUID.fromString(rs.getString("UserId")));
                user.setAccount(rs.getString("Account"));
                user.setPassword(rs.getString("Password"));
                user.setAccountStatus(rs.getByte("AccountStatus"));
                user.setPermissionType(rs.getByte("PermissionType"));
                String familyInfoId = rs.getString("FamilyInfoId");
                user.setFamilyInfoId(familyInfoId != null ? UUID.fromString(familyInfoId) : null);
                String institutionId = rs.getString("InstitutionId");
                user.setInstitutionId(institutionId != null ? UUID.fromString(institutionId) : null);
                usersList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usersList;
    }
}
