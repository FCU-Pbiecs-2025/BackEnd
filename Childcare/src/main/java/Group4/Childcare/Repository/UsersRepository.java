package Group4.Childcare.Repository;

import Group4.Childcare.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<Users, UUID> {
}

