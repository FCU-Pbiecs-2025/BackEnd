package Group4.Childcare.Controller;

import Group4.Childcare.Model.Users;
import Group4.Childcare.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/Login")
@CrossOrigin
public class SimpleLoginController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/Verify")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        Map<String, Object> result = new HashMap<>();

        String account = loginRequest.get("account");
        String password = loginRequest.get("password");

        // 檢查輸入是否為空
        if (account == null || account.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "帳號不能為空");
            return ResponseEntity.badRequest().body(result);
        }

        if (password == null || password.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "密碼不能為空");
            return ResponseEntity.badRequest().body(result);
        }

        // 根據帳號查詢使用者
        Optional<Users> userOptional = userRepository.findByAccount(account);

        if (userOptional.isEmpty()) {
            result.put("success", false);
            result.put("message", "帳號不存在");
            return ResponseEntity.notFound().build();
        }

        Users user = userOptional.get();

        // 直接比對明文密碼
        if (!password.equals(user.getPassword())) {
            result.put("success", false);
            result.put("message", "密碼錯誤");
            return ResponseEntity.badRequest().body(result);
        }

        // 檢查帳號狀態（假設 1 為啟用，0 為停用）
        if (user.getAccountStatus() != 1) {
            result.put("success", false);
            result.put("message", "帳號已停用");
            return ResponseEntity.badRequest().body(result);
        }

        // 登入成功
        result.put("success", true);
        result.put("message", "登入成功");
        result.put("user", user);

        return ResponseEntity.ok(result);
    }
}
