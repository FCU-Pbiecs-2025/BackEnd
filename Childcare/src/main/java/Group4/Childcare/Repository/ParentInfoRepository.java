package Group4.Childcare.Repository;

import Group4.Childcare.Model.ParentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ParentInfoRepository extends JpaRepository<ParentInfo, UUID> {
}

