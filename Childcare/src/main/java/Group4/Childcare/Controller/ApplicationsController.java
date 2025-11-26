package Group4.Childcare.Controller;

import Group4.Childcare.Model.Applications;
import Group4.Childcare.DTO.ApplicationSummaryDTO;
import Group4.Childcare.DTO.ApplicationSummaryWithDetailsDTO;
import Group4.Childcare.DTO.ApplicationCaseDTO;
import Group4.Childcare.DTO.AdminCaseSearchRequestDto;
import Group4.Childcare.DTO.CaseOffsetListDTO;
import Group4.Childcare.DTO.CaseEditUpdateDTO;
import Group4.Childcare.DTO.UserApplicationDetailsDTO;
import Group4.Childcare.Service.ApplicationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

@RestController
@RequestMapping("/applications")
public class ApplicationsController {
  private final ApplicationsService service;

  @Autowired
  private JdbcTemplate jdbcTemplate;

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
   *
   * 個案管理編輯頁面使用
   * GET - 根據幼兒身分證字號查詢案件並自動讀取檔案列表
   * 個案管理編輯.vue使用
   * 端點: GET /applications/case?childrenNationalID=xxx
   *
   * @param childrenNationalID 幼兒身分證字號（查詢參數，增加隱私性）
   * @return CaseEditUpdateDTO（包含檔案列表）或 404 Not Found
   *
   * 功能流程:
   * 1. 根據幼兒身分證字號查詢案件信息
   * 2. 使用查詢到的 applicationId 掃描 IdentityResource/{applicationId}/ 文件夾
   * 3. 如果文件夾不存在 → files 返回空陣列 []
   * 4. 如果文件夾存在 → 讀取所有檔案名稱到 files 陣列
   *
   * 成功回應 (200 OK):
   * {
   *   "caseNumber": 1,
   *   "applyDate": "2025-01-15",
   *   "identityType": 1,
   *   "institutionId": "550e8400-e29b-41d4-a716-446655440000",
   *   "institutionName": "台北市立幼兒園",
   *   "selectedClass": "小班A",
   *   "currentOrder": 5,
   *   "reviewDate": "2025-01-20T10:00:00",
   *   "applicationID": "550e8400-e29b-41d4-a716-446655440001",
   *   "User": { ... },
   *   "parents": [ ... ],
   *   "children": [ ... ],
   *   "files": ["證明文件1.pdf", "身份證掃描.jpg"]
   * }
   *
   * 錯誤回應:
   * - 400 Bad Request - 缺少或無效的 childrenNationalID 參數
   * - 404 Not Found - 找不到該幼兒身分證字號對應的案件
   *
   * 使用範例:
   * GET /applications/case?childrenNationalID=H123456789
   */
  @GetMapping("/case")
  public ResponseEntity<?> getCaseByChildrenNationalId(@RequestParam String childrenNationalID) {
    if (childrenNationalID == null || childrenNationalID.trim().isEmpty()) {
      return ResponseEntity.badRequest().body("Missing or invalid childrenNationalID parameter");
    }

    try {
      Optional<CaseEditUpdateDTO> result = service.getCaseByChildrenNationalId(childrenNationalID);
      return result.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    } catch (Exception ex) {
      return ResponseEntity.status(500).body("Error retrieving case: " + ex.getMessage());
    }
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
   *
   * 使用方式分為兩種：
   * 1. 更新單個參與者：提供 NationalID 參數（只更新該參與者的 status、reason、reviewDate）
   * 2. 批量更新：不提供 NationalID，直接傳遞 ApplicationCaseDTO JSON body
   *
   * RequestParam 說明：
   *  - id (PathVariable): 申請編號 (必填)
   *  - status (必填): 參與者狀態。若 JSON body 中的參與者沒有 status，則使用此參數補上
   *  - reason: 拒絕原因 (可為 null)
   *  - reviewDate: 審核日期 (會被忽略，後端直接設為當下時間)
   *  - NationalID: 國民ID (可選)
   *    - 若有提供：只更新該身分證號碼的參與者的 status、reason、reviewDate
   *    - 若無提供：批量更新所有參與者（包含 parents 和 children）
   *
   * JSON body (ApplicationCaseDTO):
   *  - parents: 家長列表 (批量更新時使用)
   *  - children: 幼兒列表 (批量更新時使用)
   *  - reason: 拒絕原因 (可為 null)
   *  - applicationId, applicationDate, institutionName: 只讀欄位 (會被查詢時覆蓋)
   *
   * 回傳值：
   *  - 若提供 NationalID：回傳 ApplicationCaseDTO (只包含該參與者)，其中 parents 包含所有家長，children 只包含指定身分證的幼兒
   *  - 若未提供 NationalID：回傳 HTTP 204 No Content
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
        // reviewDate: server sets to now
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        service.updateParticipantStatusReason(id, nationalID, statusParam, reason, now);
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

  /**
   * 沒再用的API
   * 後台案件搜尋 API
   * 支援多條件查詢：機構、班級、流水案號、申請人身分證、身分別、案件狀態
   *
   * @param searchDto 包含查詢條件的 AdminCaseSearchRequestDto
   * @return 查詢結果列表
   */
  @PostMapping("/admin/search")
  public ResponseEntity<List<Map<String, Object>>> adminSearchCases(@RequestBody AdminCaseSearchRequestDto searchDto) {
    StringBuilder sql = new StringBuilder(
        "SELECT " +
        "  a.ApplicationID, " +
        "  a.CaseNumber, " +
        "  a.ApplicationDate, " +
        "  a.IdentityType, " +
        "  i.InstitutionName, " +
        "  c.ClassName, " +
        "  u.NationalID AS ApplicantNationalID, " +
        "  u.Name AS ApplicantName, " +
        "  ap.NationalID AS ChildNationalID, " +
        "  ap.Name AS ChildName, " +
        "  ap.Status AS CaseStatus, " +
        "  ap.ReviewDate, " +
        "  ap.CurrentOrder " +
        "FROM applications a " +
        "LEFT JOIN institutions i ON a.InstitutionID = i.InstitutionID " +
        "LEFT JOIN users u ON a.UserID = u.UserID " +
        "LEFT JOIN application_participants ap ON a.ApplicationID = ap.ApplicationID " +
        "LEFT JOIN classes c ON ap.ClassID = c.ClassID " +
        "WHERE ap.ParticipantType = 0 "  // 只查詢幼兒記錄
    );

    List<Object> params = new ArrayList<>();

    // 機構篩選 (applications.InstitutionID)
    if (searchDto.getInstitutionId() != null) {
      sql.append("AND a.InstitutionID = ? ");
      params.add(searchDto.getInstitutionId().toString());
    }

    // 班級篩選 (application_participants.ClassID)
    if (searchDto.getClassId() != null) {
      sql.append("AND ap.ClassID = ? ");
      params.add(searchDto.getClassId().toString());
    }

    // 流水案號篩選 (applications.CaseNumber)
    if (searchDto.getCaseNumber() != null) {
      sql.append("AND a.CaseNumber = ? ");
      params.add(searchDto.getCaseNumber());
    }

    // 申請人身分證字號 (users.NationalID)
    if (searchDto.getApplicantNationalId() != null && !searchDto.getApplicantNationalId().isEmpty()) {
      sql.append("AND u.NationalID = ? ");
      params.add(searchDto.getApplicantNationalId());
    }

    // 身分別 (applications.IdentityType)
    if (searchDto.getIdentityType() != null && !searchDto.getIdentityType().isEmpty()) {
      sql.append("AND a.IdentityType = ? ");
      params.add(searchDto.getIdentityType());
    }

    // 案件狀態 (application_participants.Status，且 ParticipantType = 0)
    if (searchDto.getCaseStatus() != null && !searchDto.getCaseStatus().isEmpty()) {
      sql.append("AND ap.Status = ? ");
      params.add(searchDto.getCaseStatus());
    }

    sql.append("ORDER BY a.ApplicationDate DESC, a.CaseNumber ASC");

    try {
      List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), params.toArray());
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(null);
    }
  }

  /**
   * 沒再用的API
   * 後台案件搜尋 API - GET 方式
   * 支援查詢參數形式的搜尋
   */
  @GetMapping("/case/search")
  public ResponseEntity<List<Map<String, Object>>> adminSearchCasesGet(
      @RequestParam(required = false) String institutionId,
      @RequestParam(required = false) String classId,
      @RequestParam(required = false) Integer caseNumber,
      @RequestParam(required = false) String applicantNationalId,
      @RequestParam(required = false) String identityType,
      @RequestParam(required = false) String caseStatus) {

    AdminCaseSearchRequestDto dto = new AdminCaseSearchRequestDto();

    if (institutionId != null && !institutionId.isEmpty()) {
      try {
        dto.setInstitutionId(UUID.fromString(institutionId));
      } catch (Exception e) {
        return ResponseEntity.badRequest().body(null);
      }
    }

    if (classId != null && !classId.isEmpty()) {
      try {
        dto.setClassId(UUID.fromString(classId));
      } catch (Exception e) {
        return ResponseEntity.badRequest().body(null);
      }
    }

    dto.setCaseNumber(caseNumber);
    dto.setApplicantNationalId(applicantNationalId);
    dto.setIdentityType(identityType);
    dto.setCaseStatus(caseStatus);

    return adminSearchCases(dto);
  }

  /**
   * 取得案件列表（分頁）
   *個案管理列表 以及 其查詢卡片使用
   * @param offset 分頁起始位置（預設: 0）
   * @param size 每頁筆數（預設: 10）
   * @param status 審核狀態篩選（可選）
   * @param institutionId 機構ID篩選（可選）
   * @param applicationId 案件ID篩選（可選）
   * @param classId 班級ID篩選（可選）
   * @param applicantNationalId 申請人身分證字號篩選（可選）
   * @param caseNumber 案件流水號篩選（可選）
   * @param identityType 身分別篩選（可選）
   * @return 包含分頁資訊和案件列表的回應
   */
  @GetMapping("/cases/list")
  public ResponseEntity<Map<String, Object>> getCasesList(
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String institutionId,
      @RequestParam(required = false) String applicationId,
      @RequestParam(required = false) String classId,
      @RequestParam(required = false) String applicantNationalId,
      @RequestParam(required = false) Integer caseNumber,
      @RequestParam(required = false) String identityType) {

    // 基本驗證
    if (offset < 0) {
      return ResponseEntity.badRequest().build();
    }
    if (size <= 0) {
      return ResponseEntity.badRequest().build();
    }

    // 限制最大頁面大小防止濫用
    final int MAX_SIZE = 100;
    if (size > MAX_SIZE) {
      size = MAX_SIZE;
    }

    // 轉換 institutionId 參數
    UUID institutionUUID = null;
    if (institutionId != null && !institutionId.isEmpty()) {
      try {
        institutionUUID = UUID.fromString(institutionId);
      } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid institutionId format"));
      }
    }

    // 轉換 applicationId 參數
    UUID applicationUUID = null;
    if (applicationId != null && !applicationId.isEmpty()) {
      try {
        applicationUUID = UUID.fromString(applicationId);
      } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid applicationId format"));
      }
    }

    // 轉換 classId 參數
    UUID classUUID = null;
    if (classId != null && !classId.isEmpty()) {
      try {
        classUUID = UUID.fromString(classId);
      } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid classId format"));
      }
    }

    // 取得案件列表和總筆數
    List<CaseOffsetListDTO> content = service.getCaseListWithOffset(offset, size, status, institutionUUID,
                                                                     applicationUUID, classUUID, applicantNationalId,
                                                                     caseNumber, identityType);
    long totalElements = service.countCaseList(status, institutionUUID, applicationUUID, classUUID,
                                               applicantNationalId, caseNumber, identityType);
    int totalPages = (int) Math.ceil((double) totalElements / size);
    boolean hasNext = offset + size < totalElements;

    // 構建回應
    Map<String, Object> response = Map.of(
        "content", content,
        "offset", offset,
        "size", size,
        "totalElements", totalElements,
        "totalPages", totalPages,
        "hasNext", hasNext
    );

    return ResponseEntity.ok(response);
  }

  /**
   * 根據 UserID 取得使用者申請詳細資料
   * 使用 JDBC 查詢 applications、application_participants、cancellation、users 表
   *
   * @param userID 使用者ID
   * @return 包含申請詳細資料的清單
   */
  @GetMapping("/user/{userID}/details")
  public ResponseEntity<List<UserApplicationDetailsDTO>> getUserApplicationDetails(@PathVariable UUID userID) {
    if (userID == null) {
      return ResponseEntity.badRequest().build();
    }

    try {
      List<UserApplicationDetailsDTO> result = service.getUserApplicationDetails(userID);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      return ResponseEntity.status(500).build();
    }
  }


}

