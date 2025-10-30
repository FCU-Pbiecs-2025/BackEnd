package Group4.Childcare.Controller;

import Group4.Childcare.Model.ParentInfo;
import Group4.Childcare.Service.ParentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/parent-info")
public class ParentInfoController {
    @Autowired
    private ParentInfoService service;

    @PostMapping
    public ResponseEntity<ParentInfo> create(@RequestBody ParentInfo entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParentInfo> getById(@PathVariable UUID id) {
        Optional<ParentInfo> entity = service.getById(id);
        return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ParentInfo> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParentInfo> update(@PathVariable UUID id, @RequestBody ParentInfo entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }
}

