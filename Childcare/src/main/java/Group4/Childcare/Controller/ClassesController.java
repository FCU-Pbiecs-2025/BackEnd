package Group4.Childcare.Controller;

import Group4.Childcare.Model.Classes;
import Group4.Childcare.Service.ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/classes")
public class ClassesController {
    @Autowired
    private ClassesService service;

    @PostMapping
    public ResponseEntity<Classes> create(@RequestBody Classes entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classes> getById(@PathVariable UUID id) {
        Optional<Classes> entity = service.getById(id);
        return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Classes> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Classes> update(@PathVariable UUID id, @RequestBody Classes entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }
}

