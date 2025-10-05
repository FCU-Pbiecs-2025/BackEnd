package Group4.Childcare.Controller;

import Group4.Childcare.Model.Users;
import Group4.Childcare.Service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    @Autowired
    private UsersService usersService;

    // 新增帳號
    @PostMapping("/create")
    public Users createUser(@RequestBody Users user) {
        return usersService.createUser(user);
    }

    // 更新會員資料（帳號不能被更改）
    @PutMapping("/update")
    public Users updateUser(@RequestBody Users user) {
        return usersService.updateUser(user);
    }

    // 查詢所有會員
    @GetMapping("")
    public List<Users> getAllUsers() {
        return usersService.getAllUsers();
    }

    // 依ID查詢會員
    @GetMapping("/{id}")
    public Users getUserById(@PathVariable UUID id) {
        return usersService.getUserById(id);
    }
}
