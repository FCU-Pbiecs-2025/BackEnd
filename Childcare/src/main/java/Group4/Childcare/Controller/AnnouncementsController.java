package Group4.Childcare.Controller;

import Group4.Childcare.Model.Announcements;
import Group4.Childcare.DTO.AnnouncementSummaryDTO;
import Group4.Childcare.Service.AnnouncementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/announcements")
public class AnnouncementsController {
    private static final Logger logger = LoggerFactory.getLogger(AnnouncementsController.class);
    private final AnnouncementsService service;

    @Autowired
    public AnnouncementsController(AnnouncementsService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Announcements> create(@RequestBody Announcements entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Announcements> getById(@PathVariable UUID id) {
        Optional<Announcements> entity = service.getById(id);
        return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Announcements>> getAll() {
        List<Announcements> announcements = service.getAll();
        logger.info("=== getAll() Debug ===");
        for (Announcements ann : announcements) {
            logger.info("Announcement ID: {}, Title: {}, StartDate: {}",
                ann.getAnnouncementID(), ann.getTitle(), ann.getStartDate());
        }
        return ResponseEntity.ok(announcements);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Announcements> update(@PathVariable UUID id, @RequestBody Announcements entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @GetMapping("/summary")
    public ResponseEntity<List<AnnouncementSummaryDTO>> getSummary() {
        List<AnnouncementSummaryDTO> summaries = service.getSummaryAll();
        return ResponseEntity.ok(summaries);
    }
}
