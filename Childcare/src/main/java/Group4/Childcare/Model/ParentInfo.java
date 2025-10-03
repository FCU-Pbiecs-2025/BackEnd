package Group4.Childcare.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.UUID;
import java.time.LocalDate;

@Entity
@Table(name = "parent_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentInfo {
    @Id
    @Column(name = "ParentId", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID parentId;

    @Column(name = "NationalId", length = 20)
    private String nationalId;

    @Column(name = "Name", length = 10)
    private String name;

    @Column(name = "Gender")
    private Boolean gender;

    @Column(name = "Relationship", length = 15)
    private String relationship;

    @Column(name = "Occupation", length = 15)
    private String occupation;

    @Column(name = "PhoneNumber", length = 15)
    private String phoneNumber;

    @Column(name = "HouseholdAddress", length = 200)
    private String householdAddress;

    @Column(name = "MailingAddress", length = 200)
    private String mailingAddress;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "BirthDate")
    private LocalDate birthDate;

    @Column(name = "AttachmentPath", columnDefinition = "NVARCHAR(MAX)")
    private String attachmentPath;

    @Column(name = "IsSuspended")
    private Boolean isSuspended;

    @Column(name = "SuspendStart")
    private LocalDate suspendStart;

    @Column(name = "SuspendEnd")
    private LocalDate suspendEnd;

    @Column(name = "FamilyInfoId", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID familyInfoId;
}

