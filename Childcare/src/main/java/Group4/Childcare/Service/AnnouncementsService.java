package Group4.Childcare.Service;

import Group4.Childcare.Model.Announcements;
import Group4.Childcare.DTO.AnnouncementSummaryDTO;
import Group4.Childcare.Repository.AnnouncementsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AnnouncementsService {
    @Autowired
    private AnnouncementsRepository repository;

    public Announcements create(Announcements entity) {
        return repository.save(entity);
    }

    public Optional<Announcements> getById(UUID id) {
        return repository.findById(id);
    }

    public List<Announcements> getAll() {
        return repository.findAll();
    }

    public Announcements update(UUID id, Announcements entity) {
        entity.setAnnouncementID(id);
        return repository.save(entity);
    }

    public List<AnnouncementSummaryDTO> getSummaryAll() {
        return repository.findSummaryData();
    }
}
