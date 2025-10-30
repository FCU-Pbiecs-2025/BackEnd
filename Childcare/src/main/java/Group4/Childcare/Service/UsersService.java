package Group4.Childcare.Service;

import Group4.Childcare.Model.Users;
import Group4.Childcare.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;

    public Users createUser(Users user) {
        return usersRepository.save(user);
    }

    public Optional<Users> getUserById(UUID id) {
        return usersRepository.findById(id);
    }

    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public Users updateUser(UUID id, Users user) {
        user.setUserID(id);
        return usersRepository.save(user);
    }
}

