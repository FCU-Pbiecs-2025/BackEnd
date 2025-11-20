package Group4.Childcare.DTO;

import java.time.LocalDateTime;

public class ApplicationParticipantDTO {

  //ap、revoke共用，絕對不能改
    public String participantType;
    public String nationalID;
    public String name;
    public String gender;
    public String relationShip;
    public String occupation;
    public String phoneNumber;
    public String householdAddress;
    public String mailingAddress;
    public String email;
    public String birthDate;
    public Boolean isSuspended;
    public String suspendEnd;
    public Integer currentOrder;
    public String status;
    public String reason;
    public String classID;
    public String revieweUser;
    public LocalDateTime reviewDate;
}

