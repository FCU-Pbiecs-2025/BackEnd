package Group4.Childcare.Controller;

import Group4.Childcare.Model.ApplicationParticipants;
import Group4.Childcare.Service.ApplicationParticipantsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/application-participants")
public class ApplicationParticipantsController {
    private final ApplicationParticipantsService service;

    @Autowired
    public ApplicationParticipantsController(ApplicationParticipantsService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApplicationParticipants> create(@RequestBody ApplicationParticipants entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationParticipants> getById(@PathVariable UUID id) {
        Optional<ApplicationParticipants> entity = service.getById(id);
        return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ApplicationParticipants>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicationParticipants> update(@PathVariable UUID id, @RequestBody ApplicationParticipants entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }
}
