package Group4.Childcare.Repository;

import Group4.Childcare.Model.ApplicationParticipants;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ApplicationParticipantsRepository extends JpaRepository<ApplicationParticipants, UUID> {

    // 根據 ApplicationID 和 NationalID 查找參與者
    List<ApplicationParticipants> findByApplicationIDAndNationalID(UUID applicationID, String nationalID);
}

