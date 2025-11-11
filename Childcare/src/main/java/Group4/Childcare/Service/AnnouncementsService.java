package Group4.Childcare.Service;

import Group4.Childcare.Model.Announcements;
import Group4.Childcare.DTO.AnnouncementSummaryDTO;
import Group4.Childcare.Repository.AnnouncementsRepository;
import Group4.Childcare.Repository.AnnouncementsJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AnnouncementsService {
    @Autowired
    private AnnouncementsRepository repository;

    @Autowired
    private AnnouncementsJdbcRepository jdbcRepository;

    public Announcements create(Announcements entity) {
        return repository.save(entity);
    }

    public Optional<Announcements> getById(UUID id) {
        return repository.findById(id);
    }

    public List<Announcements> getAll() {
        return repository.findAll();
    }

    public Announcements update(UUID id, Announcements entity) {
        entity.setAnnouncementID(id);
        return repository.save(entity);
    }

    public List<AnnouncementSummaryDTO> getSummaryAll() {
        return repository.findSummaryData();
    }

    // 使用JDBC的offset分頁方法 - 一次取8筆
    public List<Announcements> getAnnouncementsWithOffsetJdbc(int offset) {
        return jdbcRepository.findWithOffset(offset, 8);
    }

    // 取得總筆數用於分頁計算
    public long getTotalCount() {
        return jdbcRepository.countTotal();
    }
}
