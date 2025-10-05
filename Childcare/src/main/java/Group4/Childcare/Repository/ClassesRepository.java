package Group4.Childcare.Repository;

import Group4.Childcare.Model.Classes;
import Group4.Childcare.Service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ClassesRepository {
    @Autowired
    private DatabaseService databaseService;

    // 取得所有班級
    public List<Classes> findAll() {
        List<Classes> classesList = new ArrayList<>();
        String sql = "SELECT * FROM classes";
        try (Connection conn = databaseService.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Classes cls = new Classes();
                cls.setClassId(UUID.fromString(rs.getString("ClassId")));
                cls.setClassName(rs.getString("ClassName"));
                cls.setCapacity(rs.getByte("Capacity"));
                cls.setCurrentStudents(rs.getByte("CurrentStudents"));
                cls.setMinAgeDescription(rs.getString("MinAgeDescription"));
                cls.setMaxAgeDescription(rs.getString("MaxAgeDescription"));
                cls.setAdditionalInfo(rs.getString("AdditionalInfo"));
                cls.setInstitutionId(UUID.fromString(rs.getString("InstitutionId")));
                classesList.add(cls);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classesList;
    }

    // 依ID取得班級
    public Classes findById(UUID id) {
        String sql = "SELECT * FROM classes WHERE ClassId = ?";
        try (Connection conn = databaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Classes cls = new Classes();
                    cls.setClassId(UUID.fromString(rs.getString("ClassId")));
                    cls.setClassName(rs.getString("ClassName"));
                    cls.setCapacity(rs.getByte("Capacity"));
                    cls.setCurrentStudents(rs.getByte("CurrentStudents"));
                    cls.setMinAgeDescription(rs.getString("MinAgeDescription"));
                    cls.setMaxAgeDescription(rs.getString("MaxAgeDescription"));
                    cls.setAdditionalInfo(rs.getString("AdditionalInfo"));
                    cls.setInstitutionId(UUID.fromString(rs.getString("InstitutionId")));
                    return cls;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 新增班級
    public Classes save(Classes cls) {
        String sql = "INSERT INTO classes (ClassId, ClassName, Capacity, CurrentStudents, MinAgeDescription, MaxAgeDescription, AdditionalInfo, InstitutionId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cls.getClassId().toString());
            pstmt.setString(2, cls.getClassName());
            pstmt.setByte(3, cls.getCapacity());
            pstmt.setByte(4, cls.getCurrentStudents());
            pstmt.setString(5, cls.getMinAgeDescription());
            pstmt.setString(6, cls.getMaxAgeDescription());
            pstmt.setString(7, cls.getAdditionalInfo());
            pstmt.setString(8, cls.getInstitutionId().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cls;
    }

    // 更新班級
    public Classes update(Classes cls) {
        String sql = "UPDATE classes SET ClassName = ?, Capacity = ?, CurrentStudents = ?, MinAgeDescription = ?, MaxAgeDescription = ?, AdditionalInfo = ?, InstitutionId = ? WHERE ClassId = ?";
        try (Connection conn = databaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cls.getClassName());
            pstmt.setByte(2, cls.getCapacity());
            pstmt.setByte(3, cls.getCurrentStudents());
            pstmt.setString(4, cls.getMinAgeDescription());
            pstmt.setString(5, cls.getMaxAgeDescription());
            pstmt.setString(6, cls.getAdditionalInfo());
            pstmt.setString(7, cls.getInstitutionId().toString());
            pstmt.setString(8, cls.getClassId().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cls;
    }

    // 刪除班級
    public boolean delete(UUID id) {
        String sql = "DELETE FROM classes WHERE ClassId = ?";
        try (Connection conn = databaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

