package Group4.Childcare.Service;

import Group4.Childcare.Model.Institutions;
import Group4.Childcare.Repository.InstitutionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class InstitutionsService {
    @Autowired
    private InstitutionsRepository institutionsRepository;

    public List<Institutions> getAllInstitutions() {
        return institutionsRepository.findAll();
    }

    public Institutions getInstitutionById(UUID id) {
        return institutionsRepository.findById(id);
    }

    public Institutions createInstitution(Institutions institution) {
        return institutionsRepository.save(institution);
    }

    public Institutions updateInstitution(Institutions institution) {
        return institutionsRepository.update(institution);
    }

    public boolean deleteInstitution(UUID id) {
        return institutionsRepository.delete(id);
    }
}

