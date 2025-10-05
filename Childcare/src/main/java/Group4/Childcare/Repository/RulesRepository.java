package Group4.Childcare.Repository;

import Group4.Childcare.Model.Rules;
import Group4.Childcare.Service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RulesRepository {
    @Autowired
    private DatabaseService databaseService;

    public Rules findSingle() {
        String sql = "SELECT * FROM rules";
        try (Connection conn = databaseService.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Rules rule = new Rules();

                rule.setServiceContentAndTime(rs.getString("ServiceContentAndTime"));
                rule.setFeeAndRefundPolicy(rs.getString("FeeAndRefundPolicy"));
                rule.setAdmissionEligibility(rs.getString("AdmissionEligibility"));
                return rule;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Rules update(Rules rule) {
        String sql = "UPDATE rules SET ServiceContentAndTime = ?, FeeAndRefundPolicy = ?, AdmissionEligibility = ?";
        try (Connection conn = databaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rule.getServiceContentAndTime());
            pstmt.setString(2, rule.getFeeAndRefundPolicy());
            pstmt.setString(3, rule.getAdmissionEligibility());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rule;
    }
}
