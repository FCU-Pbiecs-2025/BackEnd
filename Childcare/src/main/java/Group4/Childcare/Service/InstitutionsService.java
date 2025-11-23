package Group4.Childcare.Service;

import Group4.Childcare.Model.Institutions;
import Group4.Childcare.DTO.InstitutionSummaryDTO;
import Group4.Childcare.DTO.InstitutionSimpleDTO;
import Group4.Childcare.Repository.InstitutionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InstitutionsService {
    @Autowired
    private InstitutionsRepository repository;

    public Institutions create(Institutions entity) {
        return repository.save(entity);
    }

    public Optional<Institutions> getById(UUID id) {
        return repository.findById(id);
    }

    public List<Institutions> getAll() {
        return repository.findAll();
    }

    public Institutions update(UUID id, Institutions entity) {
        entity.setInstitutionID(id);
        return repository.save(entity);
    }

    public List<InstitutionSummaryDTO> getSummaryAll() {
        return repository.findSummaryData();
    }

    /**
     * 取得所有機構的 ID 和 name
     * @return List<InstitutionSimpleDTO>
     */
    public List<InstitutionSimpleDTO> getAllSimple() {
        return repository.findAllSimple();
    }
}
