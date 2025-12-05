package Group4.Childcare.Repository;

import Group4.Childcare.Model.Institutions;
import Group4.Childcare.DTO.InstitutionSummaryDTO;
import Group4.Childcare.DTO.InstitutionSimpleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface InstitutionsRepository extends JpaRepository<Institutions, UUID> {

    @Query("SELECT new Group4.Childcare.DTO.InstitutionSummaryDTO(i.institutionID, i.institutionName, i.address, i.phoneNumber) FROM Institutions i")
    List<InstitutionSummaryDTO> findSummaryData();

    @Query("SELECT new Group4.Childcare.DTO.InstitutionSimpleDTO(i.institutionID, i.institutionName) FROM Institutions i")
    List<InstitutionSimpleDTO> findAllSimple();

    /**
     * 根據機構 ID 查詢機構分頁資料（用於 admin 角色只查看自己的機構）
     * @param institutionID 機構 ID
     * @param pageable 分頁參數
     * @return 分頁資料
     */
    Page<Institutions> findByInstitutionID(UUID institutionID, Pageable pageable);
}
