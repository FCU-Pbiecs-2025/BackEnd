package Group4.Childcare.Controller;

import Group4.Childcare.Model.Institutions;
import Group4.Childcare.DTO.InstitutionSummaryDTO;
import Group4.Childcare.DTO.InstitutionSimpleDTO;
import Group4.Childcare.Service.InstitutionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/institutions")
public class InstitutionsController {
    @Autowired
    private InstitutionsService service;

    @PostMapping
    public ResponseEntity<Institutions> create(@RequestBody Institutions entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Institutions> getById(@PathVariable UUID id) {
        Optional<Institutions> entity = service.getById(id);
        return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Institutions> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Institutions> update(@PathVariable UUID id, @RequestBody Institutions entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @GetMapping("/summary")
    public ResponseEntity<List<InstitutionSummaryDTO>> getSummary() {
        return ResponseEntity.ok(service.getSummaryAll());
    }

    /**
     * 取得所有機構的 ID 和 name
     * 使用在個案查詢機構下拉選單
     * @return ResponseEntity<List<InstitutionSimpleDTO>>
     */
    @GetMapping("/simple/all")
    public ResponseEntity<List<InstitutionSimpleDTO>> getAllSimple() {
        return ResponseEntity.ok(service.getAllSimple());
    }
}
