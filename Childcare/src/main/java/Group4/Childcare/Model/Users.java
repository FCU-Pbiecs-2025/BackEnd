package Group4.Childcare.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @Column(name = "UserId", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID userId;

    @Column(name = "Account", length = 50)
    private String account;

    @Column(name = "Password", length = 512)
    private String password;

    @Column(name = "AccountStatus")
    private Byte accountStatus;

    @Column(name = "PermissionType")
    private Byte permissionType;

    @Column(name = "FamilyInfoId", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID familyInfoId;

    @Column(name = "InstitutionId", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID institutionId;
}
