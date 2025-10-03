package Group4.Childcare.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "banners")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Banners {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SortOrder")
    private Integer sortOrder;

    @Column(name = "StartTime")
    private LocalDateTime startTime;

    @Column(name = "EndTime")
    private LocalDateTime endTime;

    @Column(name = "ImageUrl", length = 500)
    private String imageUrl;

    @Column(name = "LinkUrl", length = 500)
    private String linkUrl;

    @Column(name = "Status")
    private Boolean status;
}

