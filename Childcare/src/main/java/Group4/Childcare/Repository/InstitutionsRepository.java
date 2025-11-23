package Group4.Childcare.Repository;

import Group4.Childcare.Model.Institutions;
import Group4.Childcare.DTO.InstitutionSummaryDTO;
import Group4.Childcare.DTO.InstitutionSimpleDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface InstitutionsRepository extends JpaRepository<Institutions, UUID> {

    @Query("SELECT new Group4.Childcare.DTO.InstitutionSummaryDTO(i.institutionID, i.institutionName, i.address, i.phoneNumber) FROM Institutions i")
    List<InstitutionSummaryDTO> findSummaryData();

    @Query("SELECT new Group4.Childcare.DTO.InstitutionSimpleDTO(i.institutionID, i.institutionName) FROM Institutions i")
    List<InstitutionSimpleDTO> findAllSimple();
}
