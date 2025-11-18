package Group4.Childcare.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Applications {

    @Id
    @Column(name = "ApplicationID", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID applicationID;

    @Column(name = "ApplicationDate")
    private LocalDate applicationDate;

    @Column(name = "InstitutionID", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID institutionID;

    @Column(name = "UserID", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID userID;

    @Column(name = "IdentityType")
    private Byte identityType;

    @Column(name = "AttachmentPath", columnDefinition = "NVARCHAR(MAX)")
    private String attachmentPath;

    @Column(name = "ReviewUser", length = 50)
    private String reviewUser;


    @OneToMany(mappedBy = "applications")
    private List<ApplicationParticipants> applicationParticipants;
}
