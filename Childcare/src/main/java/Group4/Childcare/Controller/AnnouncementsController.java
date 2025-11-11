package Group4.Childcare.Controller;

import Group4.Childcare.Model.Announcements;
import Group4.Childcare.DTO.AnnouncementSummaryDTO;
import Group4.Childcare.Model.Users;
import Group4.Childcare.Service.AnnouncementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/announcements")
public class AnnouncementsController {
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
        return ResponseEntity.ok(announcements);
    }

    @GetMapping("/offset")
    public ResponseEntity<Map<String, Object>> getAnnouncementsByOffsetJdbc(@RequestParam(defaultValue = "0") int offset) {
        List<Announcements> announcements = service.getAnnouncementsWithOffsetJdbc(offset);
        long totalCount = service.getTotalCount();

        Map<String, Object> response = new HashMap<>();
        response.put("content", announcements);
        response.put("offset", offset);
        response.put("size", 8);
        response.put("totalElements", totalCount);
        response.put("totalPages", (int) Math.ceil((double) totalCount / 8));
        response.put("hasNext", offset + 8 < totalCount);

        return ResponseEntity.ok(response);
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
