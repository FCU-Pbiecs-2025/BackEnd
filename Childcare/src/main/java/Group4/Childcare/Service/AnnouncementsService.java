package Group4.Childcare.Service;

import Group4.Childcare.Model.Announcements;
import Group4.Childcare.Repository.AnnouncementsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AnnouncementsService {
    @Autowired
    private AnnouncementsRepository announcementsRepository;

    public List<Announcements> getAllAnnouncements() {
        return announcementsRepository.findAll();
    }
}
