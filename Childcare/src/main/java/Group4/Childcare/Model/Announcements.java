package Group4.Childcare.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;//因為有@Table所以要import這個
import java.time.LocalDate;//因為有LocalDate型態所以要import
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "announcements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcements {

    @Id
    @Column(name = "AnnouncementId", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID announcementId;

    @Column(name = "Title", length = 100)
    private String title;

    @Column(name = "Content", columnDefinition = "NVARCHAR(MAX)")//如果不寫columnDefinition預設會是NVARCHAR(255)
    private String content;

    @Column(name = "Type")
    private Byte type;

    @Column(name = "StartDate")
    private LocalDate startDate;

    @Column(name = "EndDate")
    private LocalDate endDate;

    @Column(name = "Status")
    private Byte status;

    @Column(name = "CreatedUser", length = 50)
    private String createdUser;

    @Column(name = "CreatedTime")
    private LocalDateTime createdTime;

    @Column(name = "UpdatedUser", length = 50)
    private String updatedUser;

    @Column(name = "UpdatedTime")
    private LocalDateTime updatedTime;

    @Column(name = "AttachmentPath", columnDefinition = "NVARCHAR(MAX)")
    private String attachmentPath;
}
