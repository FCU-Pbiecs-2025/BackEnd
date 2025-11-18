package Group4.Childcare.Repository;

import Group4.Childcare.Model.Applications;
import Group4.Childcare.DTO.ApplicationSummaryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface ApplicationsRepository extends JpaRepository<Applications, UUID> {

    @Query("SELECT new Group4.Childcare.DTO.ApplicationSummaryDTO(a.applicationID, a.applicationDate,ap.reason, ap.status) " +
           "FROM Applications a LEFT JOIN a.applicationParticipants ap WHERE a.userID = :userID")
    List<ApplicationSummaryDTO> findSummaryByUserID(@Param("userID") UUID userID);
}
