package Group4.Childcare.Repository;

import Group4.Childcare.Model.Announcements;
import Group4.Childcare.DTO.AnnouncementSummaryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface AnnouncementsRepository extends JpaRepository<Announcements, UUID> {

    @Query("SELECT new Group4.Childcare.DTO.AnnouncementSummaryDTO(a.announcementID,a.title, a.content, a.startDate) FROM Announcements a")
    List<AnnouncementSummaryDTO> findSummaryData();
}
