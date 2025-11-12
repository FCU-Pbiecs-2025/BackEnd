package Group4.Childcare.Controller;

import Group4.Childcare.Model.Classes;
import Group4.Childcare.Service.ClassesService;
import Group4.Childcare.DTO.ClassSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
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
    public ResponseEntity<Map<String, Object>> getClassesByOffsetJdbc(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int size) {
        List<ClassSummaryDTO> classes = service.getClassesWithOffsetAndInstitutionNameJdbc(offset, size);
        long totalCount = service.getTotalCount();

        Map<String, Object> response = new HashMap<>();
        response.put("content", classes);
        response.put("offset", offset);
        response.put("size", size);
        response.put("totalElements", totalCount);
        response.put("totalPages", (int) Math.ceil((double) totalCount / size));
        response.put("hasNext", offset + size < totalCount);

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
        try {
            System.out.println("Updating class with ID: " + id);
            System.out.println("Received entity: " + entity);

            // 確保 ID 一致
            entity.setClassID(id);
            Classes updatedClass = service.update(id, entity);
            return ResponseEntity.ok(updatedClass);
        } catch (RuntimeException e) {
            // 業務邏輯錯誤（如記錄不存在）
            System.err.println("Business logic error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // 其他系統錯誤
            System.err.println("System error during update: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
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
     * 依機構名稱模糊搜尋，返回班級資料（跟 offset 端點相同格式）
     * @param institutionName 機構名稱關鍵字
     * @param offset 分頁偏移量
     * @return 查詢結果 ResponseEntity<Map<String, Object>>
     */
    @GetMapping("/search/institution")
    public ResponseEntity<Map<String, Object>> searchByInstitutionName(
            @RequestParam("name") String institutionName,
            @RequestParam(defaultValue = "0") int offset) {
        if (institutionName == null || institutionName.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<ClassSummaryDTO> allClasses = service.searchClassesByInstitutionName(institutionName.trim());

        // 手動分頁處理
        int size = 10;
        int totalElements = allClasses.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // 取得當前頁面的資料
        List<ClassSummaryDTO> pagedClasses;
        if (offset >= totalElements) {
            pagedClasses = new ArrayList<>();
        } else {
            int endIndex = Math.min(offset + size, totalElements);
            pagedClasses = allClasses.subList(offset, endIndex);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", pagedClasses);
        response.put("offset", offset);
        response.put("size", size);
        response.put("totalElements", totalElements);
        response.put("totalPages", totalPages);
        response.put("hasNext", offset + size < totalElements);

        return ResponseEntity.ok(response);
    }
}
