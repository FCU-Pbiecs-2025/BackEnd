package Group4.Childcare.Service;

import Group4.Childcare.DTO.RevokeApplicationDTO;
import Group4.Childcare.DTO.RevokeDetailResponse;
import Group4.Childcare.DTO.ApplicationParticipantDTO;
import Group4.Childcare.Repository.RevokesJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RevokeService {
    @Autowired
    private RevokesJdbcRepository revokesJdbcRepository;

    public List<RevokeApplicationDTO> getRevokedApplications(int page, int size) {
        return revokesJdbcRepository.findRevokedApplications(page, size);
    }

    public long getTotalRevokedApplications() {
        return revokesJdbcRepository.countRevokedApplications();
    }

    // 分頁搜尋撤銷申請
    public List<RevokeApplicationDTO> searchRevokedApplicationsPaged(String cancellationID, String nationalID, int page, int size) {
        return revokesJdbcRepository.searchRevokedApplicationsPaged(cancellationID, nationalID, page, size);
    }

    // 搜尋撤銷申請總數
    public long countSearchRevokedApplications(String cancellationID, String nationalID) {
        return revokesJdbcRepository.countSearchRevokedApplications(cancellationID, nationalID);
    }

    // 取得撤銷資料
    public RevokeApplicationDTO getRevokeByCancellationID(String cancellationID) {
        return revokesJdbcRepository.getRevokeByCancellationID(cancellationID);
    }

    // 取得指定撤銷的家長資料 (participantType == 2)
    public List<ApplicationParticipantDTO> getParentsByCancellation(String cancellationID) {
        return revokesJdbcRepository.getParentsByCancellation(cancellationID);
    }

    // 取得 applications 與 application_participants 的詳細資料
    public ApplicationParticipantDTO getApplicationDetailByCancellationAndNationalID(String cancellationID, String nationalID) {
        return revokesJdbcRepository.getApplicationDetailByCancellationAndNationalID(cancellationID, nationalID);
    }
}
