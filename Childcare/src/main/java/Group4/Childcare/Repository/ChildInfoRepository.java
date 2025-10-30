package Group4.Childcare.Repository;

import Group4.Childcare.Model.ChildInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ChildInfoRepository extends JpaRepository<ChildInfo, UUID> {
}

