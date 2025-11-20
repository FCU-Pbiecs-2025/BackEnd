package Group4.Childcare.Controller;

import Group4.Childcare.Model.Applications;
import Group4.Childcare.DTO.ApplicationSummaryDTO;
import Group4.Childcare.DTO.ApplicationSummaryWithDetailsDTO;
import Group4.Childcare.DTO.ApplicationCaseDTO;
import Group4.Childcare.Service.ApplicationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
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


  @PutMapping("/{id}")
  public ResponseEntity<Applications> update(@PathVariable UUID id, @RequestBody Applications entity) {
    return ResponseEntity.ok(service.update(id, entity));
  }

  @GetMapping("/application-status/{userID}")
  public ResponseEntity<List<ApplicationSummaryDTO>> getSummaryByUserID(@PathVariable UUID userID) {
    return ResponseEntity.ok(service.getSummaryByUserID(userID));
  }

  // New endpoint to expose JDBC offset API
  @GetMapping("/offset")
  public ResponseEntity<Object> getWithOffset(
          @RequestParam(defaultValue = "0") int offset,
          @RequestParam(defaultValue = "10") int size) {
    // basic validation and sanitization
    if (offset < 0) {
      return ResponseEntity.badRequest().build();
    }
    if (size <= 0) {
      return ResponseEntity.badRequest().build();
    }
    // cap size to prevent abuse
    final int MAX_SIZE = 100;
    if (size > MAX_SIZE) {
      size = MAX_SIZE;
    }

    // Fetch content and total count
    List<ApplicationSummaryWithDetailsDTO> content = service.getSummariesWithOffset(offset, size);
    long totalElements = service.getTotalApplicationsCount(); // Assume this method exists in the service
    int totalPages = (int) Math.ceil((double) totalElements / size);
    boolean hasNext = offset + size < totalElements;

    // Build response with corrected field placement
    Map<String, Object> response = Map.of(
            "totalPages", totalPages,
            "hasNext", hasNext,
            "offset", offset,
            "size", size,
            "content", content,
            "totalElements", totalElements
    );

    return ResponseEntity.ok(response);
  }


  /**
   * 給查詢卡片使用的API
   * */
  @GetMapping("/search")
  public ResponseEntity<List<ApplicationSummaryWithDetailsDTO>> searchApplications(
          @RequestParam(required = false) String institutionID,
          @RequestParam(required = false) String institutionName,
          @RequestParam(required = false) String applicationID) {
    List<ApplicationSummaryWithDetailsDTO> result = service.searchApplications(institutionID, institutionName, applicationID);
    return ResponseEntity.ok(result);
  }

  /**
   * 申請審核reviewEdit.vue 畫面呈現使用
   * */
  @GetMapping("/{id}")
  public ResponseEntity<?> getApplicationById(@PathVariable UUID id,
                                              @RequestParam(required = false, value = "NationalID") String nationalID) {
    Optional<ApplicationCaseDTO> opt = service.getApplicationByIdJdbc(id, nationalID);
    return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * 更新單一申請（包含參與者資料與審核欄位）
   * 接受 JSON body 為 ApplicationCaseDTO
   * 新增 RequestParam:
   *  - reviewDate: 會在後端直接設為當下時間（傳入會被覆蓋）
   *  - reason: 可為 null
   *  - status: 必填（若 participant 沒有 status，會以此參數補上）
   * */
  @PutMapping("/{id}/case")
  public ResponseEntity<?> updateApplicationCase(
          @PathVariable UUID id,
          @RequestBody(required = false) ApplicationCaseDTO dto,
          @RequestParam(required = false, value = "reviewDate") String reviewDateParam,
          @RequestParam(required = false, value = "reason") String reason,
          @RequestParam(required = false, value = "status") String statusParam,
          @RequestParam(required = false, value = "NationalID") String nationalID) {
    // Basic validation
    if (id == null) return ResponseEntity.badRequest().body("Missing application id");

    try {
      // If NationalID provided, we treat this as "update single participant's status/reason"
      if (nationalID != null && !nationalID.isEmpty()) {
        if (statusParam == null || statusParam.isEmpty()) {
          return ResponseEntity.badRequest().body("Missing required parameter: status (provide as query param when NationalID is used)");
        }
        // reviewer: prefer first participant's revieweUser if provided in body, otherwise null
        String reviewer = null;
        String participantReviewer = null;
        if (dto != null) {
          if (dto.parents != null && !dto.parents.isEmpty() && dto.parents.getFirst().revieweUser != null) {
            participantReviewer = dto.parents.getFirst().revieweUser;
          } else if (dto.children != null && !dto.children.isEmpty() && dto.children.getFirst().revieweUser != null) {
            participantReviewer = dto.children.getFirst().revieweUser;
          }
        }
        // reviewDate: server sets to now
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        service.updateParticipantStatusReason(id, nationalID, statusParam, reason, reviewer, now);
        // return updated single application DTO (filter by nationalID so children contains only that child)
        Optional<ApplicationCaseDTO> opt = service.getApplicationByIdJdbc(id, nationalID);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
      }

      // Otherwise handle full update path (existing behavior)
      if (dto == null) dto = new ApplicationCaseDTO();

      // For update: statusParam is required (used to fill participant status if missing)
      String finalStatus = null;
      if (statusParam != null && !statusParam.isEmpty()) finalStatus = statusParam;

      if (finalStatus == null) {
        return ResponseEntity.badRequest().body("Missing required parameter: status (provide as query param)");
      }

      // set server-side reviewDate to now (ignore any incoming reviewDateParam)
      java.time.LocalDateTime now = java.time.LocalDateTime.now();
      // reference reviewDateParam to avoid unused-parameter warning
      if (reviewDateParam != null) {
        try { java.time.LocalDateTime.parse(reviewDateParam); } catch (Exception ex) { /* ignored */ }
      }

      // reason: prefer request param if provided, otherwise keep body.reason
      if (reason != null) dto.reason = reason;

      // Set reviewDate for participants and fill missing status with provided status
      if (dto.parents != null) {
        for (Group4.Childcare.DTO.ApplicationParticipantDTO p : dto.parents) {
          if (p != null) {
            p.reviewDate = now;
            if (p.status == null || p.status.isEmpty()) {
              p.status = finalStatus;
            }
          }
        }
      }
      if (dto.children != null) {
        for (Group4.Childcare.DTO.ApplicationParticipantDTO p : dto.children) {
          if (p != null) {
            p.reviewDate = now;
            if (p.status == null || p.status.isEmpty()) {
              p.status = finalStatus;
            }
          }
        }
      }

      // Update application case
      service.updateApplicationCase(id, dto);
      return ResponseEntity.noContent().build();
    } catch (Exception ex) {
      return ResponseEntity.status(500).body("Failed to update application case: " + ex.getMessage());
    }
  }
}