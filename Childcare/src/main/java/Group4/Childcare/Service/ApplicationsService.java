package Group4.Childcare.Service;

import Group4.Childcare.Model.Applications;
import Group4.Childcare.DTO.ApplicationSummaryDTO;
import Group4.Childcare.Repository.ApplicationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import Group4.Childcare.DTO.ApplicationApplyDTO;
import Group4.Childcare.DTO.ApplicationParticipantDTO;
import Group4.Childcare.Model.ApplicationParticipants;
import Group4.Childcare.Repository.ApplicationParticipantsRepository;
import java.time.LocalDate;

@Service
public class ApplicationsService {
    @Autowired
    private ApplicationsRepository applicationsRepository;
    @Autowired
    private ApplicationParticipantsRepository applicationParticipantsRepository;

    public Applications create(Applications entity) {
        return applicationsRepository.save(entity);
    }

    public Optional<Applications> getById(UUID id) {
        return applicationsRepository.findById(id);
    }

    public List<Applications> getAll() {
        return applicationsRepository.findAll();
    }

    public Applications update(UUID id, Applications entity) {
        entity.setApplicationID(id);
        return applicationsRepository.save(entity);
    }

    public List<ApplicationSummaryDTO> getSummaryByUserID(UUID userID) {
        return applicationsRepository.findSummaryByUserID(userID);
    }

    public void apply(ApplicationApplyDTO dto) {
        Applications app = new Applications();
        app.setApplicationID(UUID.randomUUID());
        app.setApplicationDate(LocalDate.now());
        // 身分類別
        app.setIdentityType((byte)("低收入戶".equals(dto.identityType) ? 1 : "中低收入戶".equals(dto.identityType) ? 2 : 0));
        // 附件路徑（多檔名以逗號分隔）
        if (dto.attachmentFiles != null && !dto.attachmentFiles.isEmpty()) {
            app.setAttachmentPath(String.join(",", dto.attachmentFiles));
        }
        applicationsRepository.save(app);
        // 申請人與家長資料
        if (dto.participants != null) {
            for (ApplicationParticipantDTO p : dto.participants) {
                ApplicationParticipants entity = new ApplicationParticipants();
                entity.setApplicationID(app.getApplicationID());
                entity.setParticipantType("家長".equals(p.participantType));
                entity.setNationalID(p.nationalID);
                entity.setName(p.name);
                entity.setGender("男".equals(p.gender));
                entity.setRelationShip(p.relationShip);
                entity.setOccupation(p.occupation);
                entity.setPhoneNumber(p.phoneNumber);
                entity.setHouseholdAddress(p.householdAddress);
                entity.setMailingAddress(p.mailingAddress);
                entity.setEmail(p.email);
                entity.setBirthDate(p.birthDate != null && !p.birthDate.isEmpty() ? LocalDate.parse(p.birthDate) : null);
                entity.setIsSuspended(p.isSuspended);
                entity.setSuspendEnd(p.suspendEnd != null && !p.suspendEnd.isEmpty() ? LocalDate.parse(p.suspendEnd) : null);
                entity.setCurrentOrder(p.currentOrder);
                entity.setStatus(p.status);
                entity.setReason(p.reason);
                entity.setClassID(p.classID != null && !p.classID.isEmpty() ? UUID.fromString(p.classID) : null);
                applicationParticipantsRepository.save(entity);
            }
        }
    }
}
