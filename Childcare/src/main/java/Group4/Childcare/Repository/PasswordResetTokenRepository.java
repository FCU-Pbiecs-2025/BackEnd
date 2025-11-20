package Group4.Childcare.Repository;

import Group4.Childcare.Model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    List<PasswordResetToken> findByUserIdAndInvalidatedFalseAndUsedAtIsNull(UUID userId);
    List<PasswordResetToken> findByExpiresAtBefore(LocalDateTime cutoff);
}
