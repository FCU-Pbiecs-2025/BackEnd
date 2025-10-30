package Group4.Childcare.Service;

import Group4.Childcare.Model.ChildInfo;
import Group4.Childcare.Repository.ChildInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChildInfoService {
    @Autowired
    private ChildInfoRepository repository;

    public ChildInfo create(ChildInfo entity) {
        return repository.save(entity);
    }

    public Optional<ChildInfo> getById(UUID id) {
        return repository.findById(id);
    }

    public List<ChildInfo> getAll() {
        return repository.findAll();
    }

    public ChildInfo update(UUID id, ChildInfo entity) {
        entity.setChildID(id);
        return repository.save(entity);
    }
}

