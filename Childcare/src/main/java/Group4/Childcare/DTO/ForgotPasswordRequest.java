package Group4.Childcare.DTO;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email; // Using email directly per frontend implementation
    private String recaptchaToken;
}

