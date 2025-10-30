package Group4.Childcare.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Classes {

    @Id
    @Column(name = "ClassID", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID classID;

    @Column(name = "ClassName", length = 50)
    private String className;

    @Column(name = "Capacity")
    private Byte capacity;

    @Column(name = "CurrentStudents")
    private Byte currentStudents;

    @Column(name = "MinAgeDescription", length = 50)
    private String minAgeDescription;

    @Column(name = "MaxAgeDescription", length = 50)
    private String maxAgeDescription;

    @Column(name = "AdditionalInfo", length = 100)
    private String additionalInfo;

    @Column(name = "InstitutionID", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID institutionID;
}
