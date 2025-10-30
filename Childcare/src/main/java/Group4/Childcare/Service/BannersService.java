package Group4.Childcare.Service;

import Group4.Childcare.Model.Banners;
import Group4.Childcare.Repository.BannersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BannersService {
    @Autowired
    private BannersRepository repository;

    public Banners create(Banners entity) {
        return repository.save(entity);
    }

    public Optional<Banners> getById(Integer id) {
        return repository.findById(id);
    }

    public List<Banners> getAll() {
        return repository.findAll();
    }

    public Banners update(Integer id, Banners entity) {
        entity.setSortOrder(id);
        return repository.save(entity);
    }
}

