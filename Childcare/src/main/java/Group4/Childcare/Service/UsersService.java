package Group4.Childcare.Service;

import Group4.Childcare.Model.Users;
import Group4.Childcare.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;

    // 新增帳號
    public Users createUser(Users user) {
        return usersRepository.save(user);
    }

    // 更新會員資料（帳號不能被更改）
    public Users updateUser(Users user) {
        return usersRepository.update(user);
    }

    // 查詢所有會員
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    // 依ID查詢會員
    public Users getUserById(UUID id) {
        return usersRepository.findById(id);
    }
}
