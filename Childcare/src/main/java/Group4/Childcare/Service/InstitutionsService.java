package Group4.Childcare.Service;

import Group4.Childcare.Model.Institutions;
import Group4.Childcare.DTO.InstitutionSummaryDTO;
import Group4.Childcare.DTO.InstitutionSimpleDTO;
import Group4.Childcare.DTO.InstitutionOffsetDTO;
import Group4.Childcare.Repository.InstitutionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InstitutionsService {
    @Autowired
    private InstitutionsRepository repository;

    @Autowired
    private FileService fileService;

    public Institutions create(Institutions entity) {
        return repository.save(entity);
    }

    public Optional<Institutions> getById(UUID id) {
        return repository.findById(id);
    }

    public List<Institutions> getAll() {
        return repository.findAll();
    }

    public Institutions update(UUID id, Institutions entity) {
        entity.setInstitutionID(id);
        return repository.save(entity);
    }

    /**
     * 更新機構資訊並上傳圖片
     * @param id 機構ID
     * @param entity 機構資料
     * @param imageFile 圖片檔案
     * @return 更新後的機構資訊
     * @throws IOException 如果圖片上傳失敗
     */
    public Institutions updateWithImage(UUID id, Institutions entity, MultipartFile imageFile) throws IOException {
        entity.setInstitutionID(id);

        // 如果有上傳新圖片，則儲存並更新 imagePath
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = fileService.saveInstitutionImage(imageFile, id);
            entity.setImagePath(imagePath);
        }

        return repository.save(entity);
    }

    public List<InstitutionSummaryDTO> getSummaryAll() {
        return repository.findSummaryData();
    }

    /**
     * 取得所有機構的 ID 和 name
     * @return List<InstitutionSimpleDTO>
     */
    public List<InstitutionSimpleDTO> getAllSimple() {
        return repository.findAllSimple();
    }

    /**
     * 取得機構分頁資料
     * @param offset 起始項目索引
     * @param size 每頁大小
     * @param institutionID 機構 ID（可選，admin 角色使用）
     * @return InstitutionOffsetDTO
     */
    public InstitutionOffsetDTO getOffset(int offset, int size, UUID institutionID) {
        // 驗證參數
        if (size <= 0) size = 10;
        if (offset < 0) offset = 0;

        // 將 offset 轉換為 page
        int page = offset / size;

        // 根據是否有 institutionID 決定查詢方式
        Page<Institutions> pageResult;
        if (institutionID != null) {
            // admin 角色：只查詢指定機構的資料
            pageResult = repository.findByInstitutionID(institutionID, PageRequest.of(page, size));
        } else {
            // super_admin 角色：查詢所有機構
            pageResult = repository.findAll(PageRequest.of(page, size));
        }

        // 組建 DTO
        InstitutionOffsetDTO dto = new InstitutionOffsetDTO();
        dto.setOffset(offset);
        dto.setSize(size);
        dto.setTotalPages(pageResult.getTotalPages());
        dto.setHasNext(pageResult.hasNext());
        dto.setContent(pageResult.getContent());
        dto.setTotalElements(pageResult.getTotalElements());

        return dto;
    }
}

