package Group4.Childcare.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 發送簡單文字郵件
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("發送簡單郵件失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 發送HTML格式郵件
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true 表示是HTML格式

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new MessagingException("發送HTML郵件失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 發送註冊確認郵件
     */
    public void sendRegistrationConfirmation(String to, String username) {
        String subject = "歡迎加入托育管理系統";
        String htmlContent = String.format(
            "<html>" +
            "<body>" +
            "<h2>歡迎加入托育管理系統</h2>" +
            "<p>親愛的 %s，</p>" +
            "<p>您的帳戶已成功建立！</p>" +
            "<p>歡迎使用我們的托育管理系統。</p>" +
            "<br>" +
            "<p>祝好，</p>" +
            "<p>托育管理系統團隊</p>" +
            "</body>" +
            "</html>",
            username
        );

        try {
            sendHtmlEmail(to, subject, htmlContent);
        } catch (MessagingException e) {
            // 如果HTML郵件發送失敗，嘗試發送純文字郵件
            String textContent = String.format(
                "歡迎加入托育管理系統\n\n親愛的 %s，\n\n您的帳戶已成功建立！\n\n歡迎使用我們的托育管理系統。\n\n祝好，\n托育管理系統團隊",
                username
            );
            sendSimpleEmail(to, subject, textContent);
        }
    }

    /**
     * 發送密碼重設郵件
     */
    public void sendPasswordResetEmail(String to, String resetToken) {
        String subject = "密碼重設請求";
        String htmlContent = String.format(
            "<html>" +
            "<body>" +
            "<h2>密碼重設</h2>" +
            "<p>您好，</p>" +
            "<p>我們收到了您的密碼重設請求。</p>" +
            "<p>請點擊以下連結重設您的密碼：</p>" +
            "<p><a href='http://localhost:5173/reset-password?token=%s'>重設密碼</a></p>" +
            "<p>此連結將在24小時後過期。</p>" +
            "<p>如果您沒有請求重設密碼，請忽略此郵件。</p>" +
            "<br>" +
            "<p>祝好，</p>" +
            "<p>托育管理系統團隊</p>" +
            "</body>" +
            "</html>",
            resetToken
        );

        try {
            sendHtmlEmail(to, subject, htmlContent);
        } catch (MessagingException e) {
            // 如果HTML郵件發送失敗，嘗試發送純文字郵件
            String textContent = String.format(
                "密碼重設\n\n您好，\n\n我們收到了您的密碼重設請求。\n\n重設連結：http://localhost:5173/reset-password?token=%s\n\n此連結將在24小時後過期。\n\n如果您沒有請求重設密碼，請忽略此郵件。\n\n祝好，\n托育管理系統團隊",
                resetToken
            );
            sendSimpleEmail(to, subject, textContent);
        }
    }

    /**
     * 發送申請狀態通知郵件
     */
    public void sendApplicationStatusEmail(String to, String applicantName, String status, String details) {
        String subject = "申請狀態更新通知";
        String htmlContent = String.format(
            "<html>" +
            "<body>" +
            "<h2>申請狀態更新</h2>" +
            "<p>親愛的 %s，</p>" +
            "<p>您的申請狀態已更新為：<strong>%s</strong></p>" +
            "<p>詳細資訊：%s</p>" +
            "<p>如有任何疑問，請聯繫我們。</p>" +
            "<br>" +
            "<p>祝好，</p>" +
            "<p>托育管理系統團隊</p>" +
            "</body>" +
            "</html>",
            applicantName, status, details
        );

        try {
            sendHtmlEmail(to, subject, htmlContent);
        } catch (MessagingException e) {
            // 如果HTML郵件發送失敗，嘗試發送純文字郵件
            String textContent = String.format(
                "申請狀態更新\n\n親愛的 %s，\n\n您的申請狀態已更新為：%s\n\n詳細資訊：%s\n\n如有任何疑問，請聯繫我們。\n\n祝好，\n托育管理系統團隊",
                applicantName, status, details
            );
            sendSimpleEmail(to, subject, textContent);
        }
    }
}
