package Group4.Childcare.DTO;

import java.util.UUID;
import java.time.LocalDateTime;

public class RevokeApplicationDTO {
    private UUID applicationID;
    private LocalDateTime revokeTime;
    private UUID userID;
    private String userName;
    private UUID institutionID;
    private String institutionName;
    private String status;
    private String nationalID;

    public RevokeApplicationDTO(UUID applicationID, LocalDateTime revokeTime, UUID userID, String userName, UUID institutionID, String institutionName, String status, String nationalID ) {
        this.applicationID = applicationID;
        this.revokeTime = revokeTime;
        this.userID = userID;
        this.userName = userName;
        this.institutionID = institutionID;
        this.institutionName = institutionName;
        this.status = status;
        this.nationalID = nationalID;
    }

    public UUID getApplicationID() { return applicationID; }
    public LocalDateTime getRevokeTime() { return revokeTime; }
    public UUID getUserID() { return userID; }
    public String getUserName() { return userName; }
    public UUID getInstitutionID() { return institutionID; }
    public String getInstitutionName() { return institutionName; }
    public String getStatus() { return status; }
    public String getNationalID() { return nationalID; }
}

