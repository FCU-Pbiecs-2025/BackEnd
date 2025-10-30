package Group4.Childcare.Repository;

import Group4.Childcare.Model.ApplicationParticipants;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ApplicationParticipantsRepository extends JpaRepository<ApplicationParticipants, UUID> {
}

