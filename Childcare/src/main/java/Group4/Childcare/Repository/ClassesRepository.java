package Group4.Childcare.Repository;

import Group4.Childcare.Model.Classes;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ClassesRepository extends JpaRepository<Classes, UUID> {
}

