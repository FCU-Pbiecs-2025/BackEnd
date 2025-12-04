package Group4.Childcare.Repository;

import Group4.Childcare.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

/**
 * Users 資料表 JPA Repository
 */
public interface UsersRepository extends JpaRepository<Users, UUID> {
}

