package Group4.Childcare.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "family_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyInfo {
    @Id
    @Column(name = "FamilyInfoID", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID familyInfoID;
}
