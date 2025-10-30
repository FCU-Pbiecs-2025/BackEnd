package Group4.Childcare.Service;

import Group4.Childcare.Model.ApplicationParticipants;
import Group4.Childcare.Repository.ApplicationParticipantsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationParticipantsService {
    private final ApplicationParticipantsRepository repository;

    @Autowired
    public ApplicationParticipantsService(ApplicationParticipantsRepository repository) {
        this.repository = repository;
    }

    public ApplicationParticipants create(ApplicationParticipants entity) {
        return repository.save(entity);
    }

    public Optional<ApplicationParticipants> getById(UUID id) {
        return repository.findById(id);
    }

    public List<ApplicationParticipants> getAll() {
        return repository.findAll();
    }

    public ApplicationParticipants update(UUID id, ApplicationParticipants entity) {
        entity.setApplicationID(id);
        return repository.save(entity);
    }
}
