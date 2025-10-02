package Group4.Childcare.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "child_Info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildInfo {

    @Id
    @Column(name = "ChildId", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID childId;

    @Column(name = "NationalId", length = 10)
    private String nationalId;

    @Column(name = "Name", length = 10)
    private String name;

    @Column(name = "Gender")
    private Boolean gender;

    @Column(name = "BirthDate")
    private LocalDate birthDate;

    @Column(name = "FamilyInfoId", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID familyInfoId;
}
