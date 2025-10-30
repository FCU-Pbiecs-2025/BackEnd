package Group4.Childcare.Controller;

import Group4.Childcare.Model.Rules;
import Group4.Childcare.Service.RulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rules")
public class RulesController {
    @Autowired
    private RulesService service;

    @PostMapping
    public ResponseEntity<Rules> create(@RequestBody Rules entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rules> getById(@PathVariable Long id) {
        Optional<Rules> entity = service.getById(id);
        return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Rules> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rules> update(@PathVariable Long id, @RequestBody Rules entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }
}

