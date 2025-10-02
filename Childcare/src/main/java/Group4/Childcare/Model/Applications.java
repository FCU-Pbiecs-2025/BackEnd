package Group4.Childcare.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Applications {

    @Id
    @Column(name = "ApplicationId", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID applicationId;

    @Column(name = "ApplicationDate")
    private LocalDate applicationDate;

    @Column(name = "InstitutionId", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID institutionId;

    @Column(name = "UserId", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID userId;

    @Column(name = "IdentityType")
    private Byte identityType;

    @Column(name = "AttachmentPath", columnDefinition = "NVARCHAR(MAX)")
    private String attachmentPath;

    @Column(name = "ReviewStatus", length = 50)
    private String reviewStatus;

    @Column(name = "ReviewUser", length = 50)
    private String reviewUser;

    @Column(name = "CurrentOrder")
    private Integer currentOrder;

    @Column(name = "Withdrawal", columnDefinition = "NVARCHAR(50)")
    private String withdrawal;
}
