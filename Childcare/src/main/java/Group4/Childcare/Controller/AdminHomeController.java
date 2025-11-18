package Group4.Childcare.Controller;

import Group4.Childcare.DTO.AnnouncementSummaryDTO;
import Group4.Childcare.Repository.AnnouncementsJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.*;

@RestController
@RequestMapping("/adminhome")
public class AdminHomeController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AnnouncementsJdbcRepository announcementsJdbcRepository;

    // 取得後台公告（回傳 DTO）
    @GetMapping("/announcements")
    public ResponseEntity<List<AnnouncementSummaryDTO>> getAdminAnnouncements() {
        List<AnnouncementSummaryDTO> result = announcementsJdbcRepository.findAdminActiveSummaries();
        return ResponseEntity.ok(result);
    }

    // 取得代辦事項數量 (status=1:聲請中, status=5:撤銷聲請中)
    @GetMapping("/todo-counts")
    public ResponseEntity<Map<String, Integer>> getTodoCounts() {
        Integer count1 = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM dbo.application_participants WHERE Status = ?", Integer.class, 1);
        Integer count5 = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM dbo.application_participants WHERE Status = ?", Integer.class, 5);

        Map<String, Integer> result = new HashMap<>();
        result.put("pending", count1 != null ? count1 : 0);
        result.put("revoke", count5 != null ? count5 : 0);
        return ResponseEntity.ok(result);
    }
}
