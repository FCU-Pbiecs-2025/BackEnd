package Group4.Childcare.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "application_participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationParticipants {

    @Id
    @Column(name = "ApplicationId", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID applicationId;

    @Column(name = "ParticipantType")
    private Boolean participantType;

    @Column(name = "NationalId", length = 10)
    private String nationalId;

    @Column(name = "FamilyInfoId", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID familyInfoId;
}
