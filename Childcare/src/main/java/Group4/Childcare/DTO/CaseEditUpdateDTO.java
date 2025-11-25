package Group4.Childcare.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaseEditUpdateDTO {

  /** 案件編號 (applications.CaseNumber) */
  private Integer caseNumber;

  /** 聲請日期 (applications.ApplicationDate) */
  private LocalDate applyDate;

  /** 身分別 (applications.IdentityType) */
  private Integer identityType;

  /** 機構ID (applications.InstitutionID) */
  private UUID institutionId;

  /** 機構名稱 (institutions.InstitutionName) */
  private String institutionName;


  /** 選定的班級名稱 (classes.ClassName) */
  private String selectedClass;

  /** 候補序號 (application_participants.CurrentOrder) */
  private Integer currentOrder;

  /** 審核日期 (application_participants.ReviewDate) */
  private LocalDateTime reviewDate;

  /** 案件ID (applications.ApplicationID) */
  private UUID applicationID;

  /** 申請人信息 (users model) */
  private UserSimpleDTO User;

  /** 家長信息陣列 */
  private List<ApplicationParticipantDTO> parents;

  /** 幼兒信息陣列 */
  private List<ApplicationParticipantDTO> children;

  /** 附件檔案路徑陣列 (applications.AttachmentPath) */
  private List<String> files;
}

