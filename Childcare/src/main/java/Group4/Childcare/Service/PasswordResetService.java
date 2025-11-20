package Group4.Childcare.Service;

import Group4.Childcare.Model.PasswordResetToken;
import Group4.Childcare.Model.Users;
import Group4.Childcare.Repository.PasswordResetTokenRepository;
import Group4.Childcare.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32; // 256 bits
    private static final long EXPIRY_MINUTES = 15; // align with frontend

    public void requestReset(String email) {
        Optional<Users> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // do not reveal existence
            return;
        }
        Users user = userOpt.get();
        // invalidate existing tokens
        List<PasswordResetToken> existing = tokenRepository.findByUserIdAndInvalidatedFalseAndUsedAtIsNull(user.getUserID());
        existing.forEach(t -> { t.setInvalidated(true); tokenRepository.save(t); });
        // generate new token
        String rawToken = generateRawToken();
        String tokenHash = hash(rawToken);
        PasswordResetToken token = new PasswordResetToken();
        token.setUserId(user.getUserID());
        token.setTokenHash(tokenHash);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(EXPIRY_MINUTES));
        tokenRepository.save(token);
        // send email with raw token
        emailService.sendPasswordResetEmail(email, rawToken);
    }

    public boolean verifyToken(String email, String rawToken) {
        Optional<Users> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;
        String tokenHash = hash(rawToken);
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenHash(tokenHash);
        if (tokenOpt.isEmpty()) return false;
        PasswordResetToken token = tokenOpt.get();
        return token.getUserId().equals(userOpt.get().getUserID()) && token.isUsable();
    }

    public boolean resetPassword(String email, String rawToken, String newPassword) {
        Optional<Users> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;
        Users user = userOpt.get();
        String tokenHash = hash(rawToken);
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenHash(tokenHash);
        if (tokenOpt.isEmpty()) return false;
        PasswordResetToken token = tokenOpt.get();
        if (!token.getUserId().equals(user.getUserID()) || !token.isUsable()) return false;
        // basic password policy
        if (newPassword == null || newPassword.length() < 6) return false;
        user.setPassword(passwordEncoder.encode(newPassword));
        token.setUsedAt(LocalDateTime.now());
        tokenRepository.save(token);
        userRepository.save(user);
        return true;
    }

    private String generateRawToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String raw) {
        // Use SHA-256 for simple hashing (not for passwords) - token comparison only
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
        } catch (Exception e) {
            throw new RuntimeException("Unable to hash token", e);
        }
    }

    public void cleanupExpired() {
        LocalDateTime now = LocalDateTime.now();
        List<PasswordResetToken> expired = tokenRepository.findByExpiresAtBefore(now);
        expired.forEach(t -> tokenRepository.delete(t));
    }
}
