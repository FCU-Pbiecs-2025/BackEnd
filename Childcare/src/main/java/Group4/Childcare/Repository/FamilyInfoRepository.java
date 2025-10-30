package Group4.Childcare.Repository;

import Group4.Childcare.Model.FamilyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FamilyInfoRepository extends JpaRepository<FamilyInfo, UUID> {
}

