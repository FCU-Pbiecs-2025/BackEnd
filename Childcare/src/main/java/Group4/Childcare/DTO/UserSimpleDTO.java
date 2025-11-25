package Group4.Childcare.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleDTO {
  //使用在CaseEditUpdateDTO，前端是個案管理編輯.vue作為使用
    private String UserID;
    private String Name;
    private String Gender;
    private String BirthDate;
    private String MailingAddress;
    private String email;
    private String PhoneNumber;
    private String NationalID;



}
