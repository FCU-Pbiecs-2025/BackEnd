package Group4.Childcare.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
@Data
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TokenID", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID tokenId;

    @Column(name = "UserID", columnDefinition = "UNIQUEIDENTIFIER", nullable = false)
    private UUID userId;

    @Column(name = "TokenHash", length = 128, nullable = false)
    private String tokenHash;

    @Column(name = "ExpiresAt", nullable = false)
    private Instant expiresAt;

    @Column(name = "CreatedAt", nullable = false)
    private Instant createdAt;

    @Column(name = "UsedAt")
    private Instant usedAt;

    @Column(name = "Invalidated", nullable = false)
    private boolean invalidated = false;

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public boolean isUsable() {
        return !isExpired() && usedAt == null && !invalidated;
    }
}
