package Group4.Childcare.Controller;

import Group4.Childcare.Model.Classes;
import Group4.Childcare.Service.ClassesService;
import Group4.Childcare.DTO.ClassSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/offset")
    public ResponseEntity<Map<String, Object>> getClassesByOffsetJdbc(@RequestParam(defaultValue = "0") int offset) {
        List<Classes> classes = service.getClassesWithOffsetJdbc(offset);
        long totalCount = service.getTotalCount();

        Map<String, Object> response = new HashMap<>();
        response.put("content", classes);
        response.put("offset", offset);
        response.put("size", 10);
        response.put("totalElements", totalCount);
        response.put("totalPages", (int) Math.ceil((double) totalCount / 10));
        response.put("hasNext", offset + 10 < totalCount);

        return ResponseEntity.ok(response);
    }

    /**
     * 取得所有班級與其機構名稱
     * @return List<ClassSummaryDTO>
     */
    @GetMapping("/with-institution")
    public List<ClassSummaryDTO> getAllWithInstitutionName() {
        return service.getAllWithInstitutionName();
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
     * 刪除班級資料
     * @param id 班級ID
     * @return 刪除結果 ResponseEntity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
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

    /**
     * 依機構名稱模糊搜尋，返回機構及其班級資料
     * @param institutionName 機構名稱關鍵字
     * @return 查詢結果 ResponseEntity<List<Map<String, Object>>>
     */
    @GetMapping("/search/institution")
    public ResponseEntity<List<Map<String, Object>>> searchByInstitutionName(
            @RequestParam("name") String institutionName) {
        if (institutionName == null || institutionName.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Map<String, Object>> results = service.searchInstitutionsWithClassesByName(institutionName.trim());
        return ResponseEntity.ok(results);
    }
}
