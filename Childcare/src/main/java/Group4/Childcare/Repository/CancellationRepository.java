package Group4.Childcare.Repository;

import Group4.Childcare.Model.Cancellation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CancellationRepository extends JpaRepository<Cancellation, UUID> {
}

