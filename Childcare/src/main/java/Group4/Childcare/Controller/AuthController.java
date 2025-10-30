package Group4.Childcare.Controller;

import Group4.Childcare.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin // 若前後端不同網域就需要設定
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        String account = loginRequest.get("account");
        String password = loginRequest.get("password");
        return authService.login(account, password);
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> registerRequest) {
        return authService.register(registerRequest);
    }
}
