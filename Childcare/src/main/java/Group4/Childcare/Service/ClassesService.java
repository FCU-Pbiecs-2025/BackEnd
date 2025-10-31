package Group4.Childcare.Service;

import Group4.Childcare.Model.Classes;
import Group4.Childcare.Repository.ClassesJdbcRepository;
import Group4.Childcare.Repository.ClassesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClassesService {
    @Autowired
    private ClassesRepository repository;

    @Autowired
    private ClassesJdbcRepository jdbcRepository;

    /**
     * 新增一筆班級資料
     * @param entity Classes 實體
     * @return 新增後的 Classes
     */
    public Classes create(Classes entity) {
        return repository.save(entity);
    }

    /**
     * 依班級ID查詢班級資料
     * @param id 班級ID
     * @return 查詢結果 Optional<Classes>
     */
    public Optional<Classes> getById(UUID id) {
        return repository.findById(id);
    }

    /**
     * 查詢所有班級資料
     * @return 班級列表
     */
    public List<Classes> getAll() {
        return repository.findAll();
    }

    /**
     * 更新班級資料
     * @param id 班級ID
     * @param entity 更新內容
     * @return 更新後的 Classes
     */
    public Classes update(UUID id, Classes entity) {
        entity.setClassID(id);
        return repository.save(entity);
    }

    /**
     * 依機構ID查詢班級資料
     * @param institutionId 機構ID
     * @return 查詢結果 List<Classes>
     */
    public List<Classes> getByInstitutionId(UUID institutionId) {
        return jdbcRepository.findByInstitutionId(institutionId);
    }
}
