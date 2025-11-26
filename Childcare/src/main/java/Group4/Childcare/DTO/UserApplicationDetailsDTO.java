package Group4.Childcare.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserApplicationDetailsDTO {
    private UUID applicationID;        // from applications table
    private LocalDate applicationDate; // from applications table
    private UUID institutionID;        // from applications table
    private String childname;          // from application_participants table (ap.Name)
    private LocalDate birthDate;       // from application_participants table
    private String status;             // from application_participants table
    private String username;           // from users table (u.Name)
}
