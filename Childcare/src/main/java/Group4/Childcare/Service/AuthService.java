package Group4.Childcare.Service;

import Group4.Childcare.Repository.UserRepository;
import Group4.Childcare.Repository.FamilyInfoRepository;
import Group4.Childcare.Model.Users;
import Group4.Childcare.Model.FamilyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FamilyInfoRepository familyInfoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Map<String, Object> login(String account, String password) {
        Map<String, Object> result = new HashMap<>();
        Users user = userRepository.findByAccount(account).orElse(null);

        if (user == null) {
            result.put("success", false);
            result.put("message", "帳號不存在");
            return result;
        }

        // 檢查帳號狀態，AccountStatus: 1=啟用, 2=停用
        if (user.getAccountStatus() == null || user.getAccountStatus() != 1) {
            result.put("success", false);
            result.put("message", "帳號未啟用或已被停用");
            return result;
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            result.put("success", false);
            result.put("message", "密碼錯誤");
            return result;
        }

        result.put("success", true);
        result.put("user", user);
        return result;
    }

    public Map<String, Object> register(Map<String, String> registerRequest) {
        Map<String, Object> result = new HashMap<>();

        try {
            String account = registerRequest.get("account");
            String password = registerRequest.get("password");
            String name = registerRequest.get("name");
            String gender = registerRequest.get("gender");
            String phone = registerRequest.get("phone");
            String address = registerRequest.get("address");
            String email = registerRequest.get("email");
            String birthday = registerRequest.get("birthday");

            // 檢查帳號是否已存在
            if (userRepository.findByAccount(account).isPresent()) {
                result.put("success", false);
                result.put("message", "帳號已存在");
                return result;
            }

            // 先建立家庭資料
            FamilyInfo familyInfo = new FamilyInfo();
            familyInfo.setFamilyInfoID(UUID.randomUUID());
            familyInfo = familyInfoRepository.save(familyInfo);

            // 建立使用者資料
            Users user = new Users();
            user.setUserID(UUID.randomUUID());
            user.setAccount(account);
            user.setPassword(passwordEncoder.encode(password)); // 加密密碼
            user.setName(name);
            user.setGender("男".equals(gender) || "male".equalsIgnoreCase(gender)); // 假設男性為true，女性為false
            user.setPhoneNumber(phone);
            user.setMailingAddress(address);
            user.setEmail(email);

            // 處理生日格式 (假設前端傳來的是 YYYY-MM-DD 格式)
            if (birthday != null && !birthday.isEmpty()) {
                user.setBirthDate(LocalDate.parse(birthday));
            }

            user.setFamilyInfoID(familyInfo.getFamilyInfoID());
            user.setAccountStatus((byte) 1); // 預設帳號狀態為啟用
            user.setPermissionType((byte) 1); // 預設權限類型為一般使用者

            userRepository.save(user);

            result.put("success", true);
            result.put("message", "註冊成功");
            result.put("userId", user.getUserID());

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "註冊失敗：" + e.getMessage());
        }

        return result;
    }
}
