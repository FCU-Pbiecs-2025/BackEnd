package Group4.Childcare.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateRevokeRequest {
    @JsonProperty("NationalID")
    public String nationalID;

    @JsonProperty("AbandonReason")
    public String abandonReason;

    @JsonProperty("ApplicationID")
    public String applicationID;

    public CreateRevokeRequest() {}

    public CreateRevokeRequest(String nationalID, String abandonReason, String applicationID) {
        this.nationalID = nationalID;
        this.abandonReason = abandonReason;
        this.applicationID = applicationID;
    }

    public String getNationalID() { return nationalID; }
    public String getAbandonReason() { return abandonReason; }
    public String getApplicationID() { return applicationID; }

    public void setNationalID(String nationalID) { this.nationalID = nationalID; }
    public void setAbandonReason(String abandonReason) { this.abandonReason = abandonReason; }
    public void setApplicationID(String applicationID) { this.applicationID = applicationID; }
}
