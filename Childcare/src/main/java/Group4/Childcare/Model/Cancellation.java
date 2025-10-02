package Group4.Childcare.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cancellation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cancellation {

    @Id
    @Column(name = "CancellationId", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID cancellationId;

    @Column(name = "ApplicationId", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID applicationId;

    @Column(name = "AbandonReasonCode")
    private Byte abandonReasonCode;

    @Column(name = "CancellationDate")
    private LocalDate cancellationDate;

    @Column(name = "ConfirmDate")
    private LocalDate confirmDate;

    @Column(name = "ResultDescription", length = 500)
    private String resultDescription;
}
