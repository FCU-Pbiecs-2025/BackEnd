package Group4.Childcare.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementSummaryDTO {
    private UUID announcementID;
    private String title;
    private String content;
    private LocalDate startDate;
}
