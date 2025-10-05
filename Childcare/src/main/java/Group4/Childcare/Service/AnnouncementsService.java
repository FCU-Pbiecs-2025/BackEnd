package Group4.Childcare.Service;

import Group4.Childcare.Model.Announcements;
import Group4.Childcare.Repository.AnnouncementsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class AnnouncementsService {
    @Autowired
    private AnnouncementsRepository announcementsRepository;

    public List<Announcements> getAllAnnouncements() {
        return announcementsRepository.findAll();
    }

    public Announcements getAnnouncementById(UUID id) {
        return announcementsRepository.findById(id);
    }

    public Announcements createAnnouncement(Announcements announcement) {
        return announcementsRepository.save(announcement);
    }

    public Announcements updateAnnouncement(Announcements announcement) {
        return announcementsRepository.update(announcement);
    }

    public boolean deleteAnnouncement(UUID id) {
        return announcementsRepository.delete(id);
    }
}
