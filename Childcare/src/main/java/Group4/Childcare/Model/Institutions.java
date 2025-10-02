package Group4.Childcare.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "institutions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Institutions {

    @Id
    @Column(name = "InstitutionId", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID institutionId;

    @Column(name = "InstitutionName", length = 100)
    private String institutionName;

    @Column(name = "CommissionUnit", length = 100)
    private String commissionUnit;

    @Column(name = "ContactPerson", length = 50)
    private String contactPerson;

    @Column(name = "Address", length = 100)
    private String address;

    @Column(name = "PhoneNumber", length = 20)
    private String phoneNumber;

    @Column(name = "Fax", length = 20)
    private String fax;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "RelatedLinks", columnDefinition = "NVARCHAR(MAX)")
    private String relatedLinks;

    @Column(name = "Description", length = 1000)
    private String description;

    @Column(name = "AdditionalInfo", length = 100)
    private String additionalInfo;

    @Column(name = "ResponsiblePerson", length = 50)
    private String responsiblePerson;

    @Column(name = "ImagePath", columnDefinition = "NVARCHAR(MAX)")
    private String imagePath;

    @Column(name = "CreatedUser", length = 50)
    private String createdUser;

    @Column(name = "CreatedTime")
    private LocalDateTime createdTime;

    @Column(name = "UpdatedUser", length = 50)
    private String updatedUser;

    @Column(name = "UpdatedTime")
    private LocalDateTime updatedTime;
}
