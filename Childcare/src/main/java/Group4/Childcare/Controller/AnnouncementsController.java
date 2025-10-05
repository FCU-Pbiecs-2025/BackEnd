package Group4.Childcare.Controller;

import Group4.Childcare.Model.Announcements;
import Group4.Childcare.Service.AnnouncementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementsController {
    @Autowired
    private AnnouncementsService announcementsService;

    @GetMapping("")
    public List<Announcements> getAllAnnouncements() {
        return announcementsService.getAllAnnouncements();
    }

    @GetMapping("/{id}")
    public Announcements getAnnouncementById(@PathVariable UUID id) {
        return announcementsService.getAnnouncementById(id);
    }

    @PostMapping("/create")
    public Announcements createAnnouncement(@RequestBody Announcements announcement) {
        return announcementsService.createAnnouncement(announcement);
    }

    @PutMapping("/update")
    public Announcements updateAnnouncement(@RequestBody Announcements announcement) {
        return announcementsService.updateAnnouncement(announcement);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteAnnouncement(@PathVariable UUID id) {
        return announcementsService.deleteAnnouncement(id);
    }
}

