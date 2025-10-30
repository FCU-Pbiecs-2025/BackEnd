package Group4.Childcare.Controller;

import Group4.Childcare.Model.Applications;
import Group4.Childcare.DTO.ApplicationSummaryDTO;
import Group4.Childcare.Service.ApplicationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/applications")
public class ApplicationsController {
    private final ApplicationsService service;

    @Autowired
    public ApplicationsController(ApplicationsService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Applications> create(@RequestBody Applications entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Applications> getById(@PathVariable UUID id) {
        Optional<Applications> entity = service.getById(id);
        return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Applications>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Applications> update(@PathVariable UUID id, @RequestBody Applications entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @GetMapping("/application-status/{userID}")
    public ResponseEntity<List<ApplicationSummaryDTO>> getSummaryByUserID(@PathVariable UUID userID) {
        return ResponseEntity.ok(service.getSummaryByUserID(userID));
    }
}
