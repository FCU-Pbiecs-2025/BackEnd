package Group4.Childcare.Repository;

import Group4.Childcare.Model.Users;
import Group4.Childcare.DTO.UserSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserJdbcRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TABLE_NAME = "users";

    // RowMapper for Users entity
    private static final RowMapper<Users> USERS_ROW_MAPPER = new RowMapper<Users>() {
        @Override
        public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
            Users user = new Users();
            user.setUserID(UUID.fromString(rs.getString("UserID")));
            user.setAccount(rs.getString("Account"));
            user.setPassword(rs.getString("Password"));
            user.setAccountStatus(rs.getByte("AccountStatus"));
            user.setPermissionType(rs.getByte("PermissionType"));
            user.setName(rs.getString("Name"));
            user.setGender(rs.getBoolean("Gender"));
            user.setPhoneNumber(rs.getString("PhoneNumber"));
            user.setMailingAddress(rs.getString("MailingAddress"));
            user.setEmail(rs.getString("Email"));

            if (rs.getDate("BirthDate") != null) {
                user.setBirthDate(rs.getDate("BirthDate").toLocalDate());
            }

            if (rs.getString("FamilyInfoID") != null) {
                user.setFamilyInfoID(UUID.fromString(rs.getString("FamilyInfoID")));
            }

            if (rs.getString("InstitutionID") != null) {
                user.setInstitutionID(UUID.fromString(rs.getString("InstitutionID")));
            }


            return user;
        }
    };

    // RowMapper for UserSummaryDTO
    private static final RowMapper<UserSummaryDTO> USER_SUMMARY_ROW_MAPPER = new RowMapper<UserSummaryDTO>() {
        @Override
        public UserSummaryDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserSummaryDTO user = new UserSummaryDTO();
            user.setUserID(UUID.fromString(rs.getString("UserID")));
            user.setAccount(rs.getString("Account"));
            user.setPermissionType(rs.getByte("PermissionType"));
            user.setAccountStatus(rs.getByte("AccountStatus"));
            // 處理可能為 null 的 InstitutionName
            String institutionName = rs.getString("InstitutionName");
            user.setInstitutionName(institutionName);
            return user;
        }
    };

    // Save method
    public Users save(Users user) {
        if (user.getUserID() == null) {
            user.setUserID(UUID.randomUUID());
            return insert(user);
        } else {
            return update(user);
        }
    }

    // Insert method
    private Users insert(Users user) {
        String sql = "INSERT INTO " + TABLE_NAME +
                    " (UserID, Account, Password, AccountStatus, PermissionType, Name, Gender, " +
                    "PhoneNumber, MailingAddress, Email, BirthDate, FamilyInfoID, InstitutionID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
            user.getUserID().toString(),
            user.getAccount(),
            user.getPassword(),
            user.getAccountStatus(),
            user.getPermissionType(),
            user.getName(),
            user.getGender(),
            user.getPhoneNumber(),
            user.getMailingAddress(),
            user.getEmail(),
            user.getBirthDate(),
            user.getFamilyInfoID() != null ? user.getFamilyInfoID().toString() : null,
            user.getInstitutionID() != null ? user.getInstitutionID().toString() : null
        );

        return user;
    }

    // Update method
    private Users update(Users user) {
        String sql = "UPDATE " + TABLE_NAME +
                    " SET Account = ?, Password = ?, AccountStatus = ?, PermissionType = ?, Name = ?, " +
                    "Gender = ?, PhoneNumber = ?, MailingAddress = ?, Email = ?, BirthDate = ?, " +
                    "FamilyInfoID = ?, InstitutionID = ? WHERE UserID = ?";

        jdbcTemplate.update(sql,
            user.getAccount(),
            user.getPassword(),
            user.getAccountStatus(),
            user.getPermissionType(),
            user.getName(),
            user.getGender(),
            user.getPhoneNumber(),
            user.getMailingAddress(),
            user.getEmail(),
            user.getBirthDate(),
            user.getFamilyInfoID() != null ? user.getFamilyInfoID().toString() : null,
            user.getInstitutionID() != null ? user.getInstitutionID().toString() : null,
            user.getUserID().toString()
        );

        return user;
    }

    // Find by ID
    public Optional<Users> findById(UUID id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE UserID = ?";
        try {
            Users user = jdbcTemplate.queryForObject(sql, USERS_ROW_MAPPER, id.toString());
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Find all
    public List<Users> findAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        return jdbcTemplate.query(sql, USERS_ROW_MAPPER);
    }

    // Delete by ID
    public void deleteById(UUID id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE UserID = ?";
        jdbcTemplate.update(sql, id.toString());
    }

    // Delete entity
    public void delete(Users user) {
        deleteById(user.getUserID());
    }

    // Check if exists by ID
    public boolean existsById(UUID id) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE UserID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id.toString());
        return count != null && count > 0;
    }

    // Count all
    public long count() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }

    // Custom method: Find by Account
    public Optional<Users> findByAccount(String account) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE Account = ?";
        try {
            Users user = jdbcTemplate.queryForObject(sql, USERS_ROW_MAPPER, account);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 取得總筆數用於分頁計算
    public long countTotal() {
        return count();
    }

    // 使用offset分頁查詢，包含機構名稱 - 一次取指定筆數
    public List<UserSummaryDTO> findWithOffsetAndInstitutionName(int offset, int limit) {
        String sql = "SELECT u.UserID, u.Account, u.PermissionType, u.AccountStatus, i.InstitutionName " +
                     "FROM " + TABLE_NAME + " u LEFT JOIN institutions i ON u.InstitutionID = i.InstitutionID " +
                     "ORDER BY u.UserID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        return jdbcTemplate.query(sql, USER_SUMMARY_ROW_MAPPER, offset, limit);
    }
}
