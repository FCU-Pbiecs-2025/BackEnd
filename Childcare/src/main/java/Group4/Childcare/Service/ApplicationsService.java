package Group4.Childcare.Service;

import Group4.Childcare.Model.Applications;
import Group4.Childcare.DTO.ApplicationSummaryDTO;
import Group4.Childcare.Repository.ApplicationsRepository;
import Group4.Childcare.Repository.ApplicationsJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import Group4.Childcare.DTO.ApplicationApplyDTO;
import Group4.Childcare.DTO.ApplicationParticipantDTO;
import Group4.Childcare.Model.ApplicationParticipants;
import Group4.Childcare.Repository.ApplicationParticipantsRepository;
import java.time.LocalDate;
import Group4.Childcare.DTO.ApplicationSummaryWithDetailsDTO;
import Group4.Childcare.DTO.ApplicationCaseDTO;
import Group4.Childcare.DTO.CaseOffsetListDTO;
import Group4.Childcare.DTO.CaseOffsetListDTO;
import Group4.Childcare.DTO.CaseEditUpdateDTO;
import Group4.Childcare.DTO.UserApplicationDetailsDTO;

@Service
public class ApplicationsService {
  @Autowired
  private ApplicationsRepository applicationsRepository;
  @Autowired
  private ApplicationParticipantsRepository applicationParticipantsRepository;
  @Autowired
  private ApplicationsJdbcRepository applicationsJdbcRepository;
  @Autowired
  private Group4.Childcare.Service.FileService fileService;

  public Applications create(Applications entity) {
    return applicationsRepository.save(entity);
  }

  public Optional<Applications> getById(UUID id) {
    return applicationsRepository.findById(id);
  }

  public List<Applications> getAll() {
    return applicationsRepository.findAll();
  }

  public Applications update(UUID id, Applications entity) {
    entity.setApplicationID(id);
    return applicationsRepository.save(entity);
  }

  public List<ApplicationSummaryDTO> getSummaryByUserID(UUID userID) {
    return applicationsRepository.findSummaryByUserID(userID);
  }

  // New: expose JDBC offset query
  public List<ApplicationSummaryWithDetailsDTO> getSummariesWithOffset(int offset, int limit) {
    return applicationsJdbcRepository.findSummariesWithOffset(offset, limit);
  }

  public void apply(ApplicationApplyDTO dto) {
    Applications app = new Applications();
    app.setApplicationID(UUID.randomUUID());
    app.setApplicationDate(LocalDate.now());
    // 身分類別
    app.setIdentityType((byte)("低收入戶".equals(dto.identityType) ? 1 : "中低收入戶".equals(dto.identityType) ? 2 : 0));
    // 附件路徑（多檔名以逗號分隔）
    if (dto.attachmentFiles != null && !dto.attachmentFiles.isEmpty()) {
      app.setAttachmentPath(String.join(",", dto.attachmentFiles));
    }
    applicationsRepository.save(app);
    // 申請人與家長資料
    if (dto.participants != null) {
      for (ApplicationParticipantDTO p : dto.participants) {
        ApplicationParticipants entity = new ApplicationParticipants();
        entity.setApplicationID(app.getApplicationID());
        // 支持 "家長"/"幼兒" 文字或 1/0 數字格式
        boolean isParent = "家長".equals(p.participantType) || "1".equals(p.participantType);
        entity.setParticipantType(isParent);
        entity.setNationalID(p.nationalID);
        entity.setName(p.name);
        entity.setGender("男".equals(p.gender));
        entity.setRelationShip(p.relationShip);
        entity.setOccupation(p.occupation);
        entity.setPhoneNumber(p.phoneNumber);
        entity.setHouseholdAddress(p.householdAddress);
        entity.setMailingAddress(p.mailingAddress);
        entity.setEmail(p.email);
        entity.setBirthDate(p.birthDate != null && !p.birthDate.isEmpty() ? LocalDate.parse(p.birthDate) : null);
        entity.setIsSuspended(p.isSuspended);
        entity.setSuspendEnd(p.suspendEnd != null && !p.suspendEnd.isEmpty() ? LocalDate.parse(p.suspendEnd) : null);
        entity.setCurrentOrder(p.currentOrder);
        entity.setStatus(p.status);
        entity.setClassID(p.classID != null && !p.classID.isEmpty() ? UUID.fromString(p.classID) : null);
        // participant-level review fields
        entity.setReviewDate(p.reviewDate);
        applicationParticipantsRepository.save(entity);
      }
    }
  }

  // New method to get total applications count
  public long getTotalApplicationsCount() {
    return applicationsRepository.count();
  }

  // New method to map an Applications entity to ApplicationSummaryWithDetailsDTO
  public Optional<ApplicationSummaryWithDetailsDTO> getApplicationSummaryWithDetailsById(UUID id) {
    return applicationsJdbcRepository.findApplicationSummaryWithDetailsById(id);

  }

  // New method to search applications with optional parameters
  public List<ApplicationSummaryWithDetailsDTO> searchApplications(String institutionID, String institutionName, String applicationID) {
    return applicationsJdbcRepository.searchApplications(institutionID, institutionName, applicationID);
  }

  // JDBC 方式查詢單一個案 - changed to return ApplicationCaseDTO
  public Optional<ApplicationCaseDTO> getApplicationByIdJdbc(UUID id, String nationalID) {
        return applicationsJdbcRepository.findApplicationCaseById(id, nationalID);
    }

  // Update single participant's status and reason, optionally set reviewDate
  public void updateParticipantStatusReason(UUID id, String nationalID, String status, String reason, java.time.LocalDateTime reviewDate) {
    applicationsJdbcRepository.updateParticipantStatusReason(id, nationalID, status, reason, reviewDate);
  }

  // New: update application case (participants + review fields)
  public void updateApplicationCase(UUID id, ApplicationCaseDTO dto) {
    applicationsJdbcRepository.updateApplicationCase(id, dto);
  }

  /**
   * 查詢案件列表（分頁）
   * @param offset 分頁起始位置
   * @param limit 每頁筆數
   * @param status 審核狀態（可選）
   * @param institutionId 機構ID（可選）
   * @param applicationId 案件ID（可選）
   * @param classId 班級ID（可選）
   * @param applicantNationalId 申請人身分證字號（可選）
   * @param caseNumber 案件流水號（可選）
   * @param identityType 身分別（可選）
   * @return List<CaseOffsetListDTO>
   */
  public List<CaseOffsetListDTO> getCaseListWithOffset(int offset, int limit, String status, UUID institutionId,
                                                        UUID applicationId, UUID classId, String applicantNationalId,
                                                        Long caseNumber, String identityType) {
    return applicationsJdbcRepository.findCaseListWithOffset(offset, limit, status, institutionId,
                                                             applicationId, classId, applicantNationalId,
                                                             caseNumber, identityType);
  }

  /**
   * 查詢案件列表的總筆數
   * @param status 審核狀態（可選）
   * @param institutionId 機構ID（可選）
   * @param applicationId 案件ID（可選）
   * @param classId 班級ID（可選）
   * @param applicantNationalId 申請人身分證字號（可選）
   * @param caseNumber 案件流水號（可選）
   * @param identityType 身分別（可選）
   * @return 總筆數
   */
  public long countCaseList(String status, UUID institutionId, UUID applicationId, UUID classId,
                            String applicantNationalId, Long caseNumber, String identityType) {
    return applicationsJdbcRepository.countCaseList(status, institutionId, applicationId, classId,
                                                    applicantNationalId, caseNumber, identityType);
  }

  /**
   * 根據幼兒身分證字號查詢案件並自動讀取檔案列表
   * @param childrenNationalID 幼兒身分證字號
   * @return CaseEditUpdateDTO（包含檔案列表和參與者信息）或 Optional.empty()
   */
  public Optional<CaseEditUpdateDTO> getCaseByChildrenNationalId(String childrenNationalID) {
    // 查詢是否存在這個身分證字號對應的案件
    List<CaseEditUpdateDTO> results = applicationsJdbcRepository.findByNationalID(childrenNationalID);

    if (results.isEmpty()) {
      return Optional.empty();
    }

    // 取第一個找到的案件記錄
    CaseEditUpdateDTO result = results.getFirst();

    // 自動讀取檔案列表並設置到四個路徑字段
    if (result.getApplicationID() != null) {
      List<String> files = fileService.getFilesByApplicationId(result.getApplicationID());
      // 將檔案列表對應到 attachmentPath, attachmentPath1, attachmentPath2, attachmentPath3
      if (files.size() > 0) {
        result.setAttachmentPath(files.get(0));
      }
      if (files.size() > 1) {
        result.setAttachmentPath1(files.get(1));
      }
      if (files.size() > 2) {
        result.setAttachmentPath2(files.get(2));
      }
      if (files.size() > 3) {
        result.setAttachmentPath3(files.get(3));
      }
    }

    // 查詢該案件的所有參與者（家長和幼兒）
    Optional<ApplicationCaseDTO> caseDto = applicationsJdbcRepository.findApplicationCaseById(result.getApplicationID(), childrenNationalID);
    if (caseDto.isPresent()) {
      ApplicationCaseDTO applicationCase = caseDto.get();
      result.setParents(applicationCase.parents);
      result.setChildren(applicationCase.children);
      // User 信息已經在 findByNationalID 中正確設置，無需覆蓋
    }

    return Optional.of(result);
  }

  /**
   * 根據 UserID 查詢使用者申請詳細資料
   * 包含 applications、application_participants、cancellation、user 表的聯合查詢
   * @param userID 使用者ID
   * @return 包含申請詳細資料的清單
   */
  public List<UserApplicationDetailsDTO> getUserApplicationDetails(UUID userID) {
    return applicationsJdbcRepository.findUserApplicationDetails(userID);
  }

}
