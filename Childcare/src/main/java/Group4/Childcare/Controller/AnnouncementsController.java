package Group4.Childcare.Controller;

import Group4.Childcare.Model.Announcements;
import Group4.Childcare.Service.AnnouncementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementsController {

    @Autowired
    private AnnouncementsService announcementsService;

    @GetMapping("")
    public List<Announcements> getAllAnnouncements() {
        return announcementsService.getAllAnnouncements();
    }
}
