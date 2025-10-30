package Group4.Childcare.Service;

import Group4.Childcare.Model.Classes;
import Group4.Childcare.Repository.ClassesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClassesService {
    @Autowired
    private ClassesRepository repository;

    public Classes create(Classes entity) {
        return repository.save(entity);
    }

    public Optional<Classes> getById(UUID id) {
        return repository.findById(id);
    }

    public List<Classes> getAll() {
        return repository.findAll();
    }

    public Classes update(UUID id, Classes entity) {
        entity.setClassID(id);
        return repository.save(entity);
    }
}

