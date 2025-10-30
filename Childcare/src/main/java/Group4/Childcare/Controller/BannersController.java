package Group4.Childcare.Controller;

import Group4.Childcare.Model.Banners;
import Group4.Childcare.Service.BannersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/banners")
public class BannersController {
    @Autowired
    private BannersService service;

    @PostMapping
    public ResponseEntity<Banners> create(@RequestBody Banners entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Banners> getById(@PathVariable Integer id) {
        Optional<Banners> entity = service.getById(id);
        return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Banners> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Banners> update(@PathVariable Integer id, @RequestBody Banners entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }
}

