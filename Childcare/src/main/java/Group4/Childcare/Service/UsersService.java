package Group4.Childcare.Service;

import Group4.Childcare.Model.Users;
import Group4.Childcare.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;

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
}
