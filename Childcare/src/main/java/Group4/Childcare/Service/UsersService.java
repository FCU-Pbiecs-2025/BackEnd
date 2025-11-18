package Group4.Childcare.Service;

import Group4.Childcare.Model.Users;
import Group4.Childcare.Repository.UsersRepository;
import Group4.Childcare.Repository.UserJdbcRepository;
import Group4.Childcare.DTO.UserSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsersService {
  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private UserJdbcRepository jdbcRepository;

  /**
   * 新增使用者
   * @param user Users 實體
   * @return 新增後的 Users
   */
  public Users createUser(Users user) {
    return usersRepository.save(user);
  }

  /**
   * 依使用者ID查詢使用者
   * @param id 使用者ID
   * @return 查詢結果 Optional<Users>
   */
  public Optional<Users> getUserById(UUID id) {
    return usersRepository.findById(id);
  }

  /**
   * 查詢所有使用者
   * @return 使用者列表
   */
  public List<Users> getAllUsers() {
    return usersRepository.findAll();
  }

  /**
   * 更新使用者資料
   * @param id 使用者ID
   * @param user 更新內容
   * @return 更新後的 Users
   */
  public Users updateUser(UUID id, Users user) {
    user.setUserID(id);
    return usersRepository.save(user);
  }

  // 使用JDBC的offset分頁方法，包含機構名稱 - 可指定每頁筆數
  public List<UserSummaryDTO> getUsersWithOffsetAndInstitutionNameJdbc(int offset, int size) {
    try {
      return jdbcRepository.findWithOffsetAndInstitutionName(offset, size);
    } catch (Exception e) {
      System.err.println("Error in getUsersWithOffsetAndInstitutionNameJdbc: " + e.getMessage());
      e.printStackTrace();
      throw new RuntimeException("Failed to get users with offset", e);
    }
  }

  // 取得總筆數用於分頁計算
  public long getTotalCount() {
    try {
      return jdbcRepository.countTotal();
    } catch (Exception e) {
      System.err.println("Error in getTotalCount: " + e.getMessage());
      e.printStackTrace();
      return 0;
    }
  }
}