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

    /**
     * 新增一筆班級資料
     * @param entity Classes 實體
     * @return 新增後的 Classes
     */
    @PostMapping
    public ResponseEntity<Classes> create(@RequestBody Classes entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    /**
     * 依班級ID查詢班級資料
     * @param id 班級ID
     * @return 查詢結果 ResponseEntity<Classes>
     */
    @GetMapping("/{id}")
    public ResponseEntity<Classes> getById(@PathVariable UUID id) {
        Optional<Classes> entity = service.getById(id);
        return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 查詢所有班級資料
     * @return 班級列表
     */
    @GetMapping
    public List<Classes> getAll() {
        return service.getAll();
    }

    /**
     * 更新班級資料
     * @param id 班級ID
     * @param entity 更新內容
     * @return 更新後的 Classes
     */
    @PutMapping("/{id}")
    public ResponseEntity<Classes> update(@PathVariable UUID id, @RequestBody Classes entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    /**
     * 依機構ID查詢班級資料
     * @param institutionId 機構ID
     * @return 查詢結果 ResponseEntity<List<Classes>>
     */
    @GetMapping("/institution/{institutionId}")
    public ResponseEntity<List<Classes>> getByInstitutionId(@PathVariable UUID institutionId) {
        List<Classes> entities = service.getByInstitutionId(institutionId);
        return ResponseEntity.ok(entities);
    }
}
