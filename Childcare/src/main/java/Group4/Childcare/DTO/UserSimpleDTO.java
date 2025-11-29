package Group4.Childcare.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleDTO {
  //使用在CaseEditUpdateDTO，前端是個案管理編輯.vue作為使用
  @JsonProperty("UserID")
  private String UserID;
  @JsonProperty("Name")
  private String Name;
  @JsonProperty("Gender")
  private String Gender;
  @JsonProperty("BirthDate")
  private String BirthDate;
  @JsonProperty("MailingAddress")
  private String MailingAddress;
  @JsonProperty("email")
  private String email;
  @JsonProperty("PhoneNumber")
  private String PhoneNumber;
  @JsonProperty("NationalID")
  private String NationalID;
}
