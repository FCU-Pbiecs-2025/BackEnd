package Group4.Childcare.Controller;

import Group4.Childcare.Model.ChildInfo;
import Group4.Childcare.Service.ChildInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/child-info")
public class ChildInfoController {
    @Autowired
    private ChildInfoService service;

    @PostMapping
    public ResponseEntity<ChildInfo> create(@RequestBody ChildInfo entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChildInfo> getById(@PathVariable UUID id) {
        Optional<ChildInfo> entity = service.getById(id);
        return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ChildInfo> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChildInfo> update(@PathVariable UUID id, @RequestBody ChildInfo entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }
}

