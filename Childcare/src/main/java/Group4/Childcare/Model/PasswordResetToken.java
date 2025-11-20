package Group4.Childcare.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
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

    // 對應規格 NVARCHAR(255)
    @Column(name = "TokenHash", length = 255, nullable = false)
    private String tokenHash;

    // DATETIME2 對應 Java LocalDateTime
    @Column(name = "ExpiresAt", columnDefinition = "DATETIME2", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "CreatedAt", columnDefinition = "DATETIME2", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UsedAt", columnDefinition = "DATETIME2")
    private LocalDateTime usedAt;

    @Column(name = "Invalidated", nullable = false)
    private boolean invalidated = false;

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isUsable() {
        return !isExpired() && usedAt == null && !invalidated;
    }
}
