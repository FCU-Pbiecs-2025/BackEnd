package Group4.Childcare.Repository;

import Group4.Childcare.Model.Classes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ClassesJdbcRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TABLE_NAME = "classes";

    // RowMapper for Classes entity
    private static final RowMapper<Classes> CLASSES_ROW_MAPPER = new RowMapper<Classes>() {
        @Override
        public Classes mapRow(ResultSet rs, int rowNum) throws SQLException {
            Classes classes = new Classes();
            classes.setClassID(UUID.fromString(rs.getString("ClassID")));
            classes.setClassName(rs.getString("ClassName"));
            classes.setCapacity(rs.getByte("Capacity"));
            classes.setCurrentStudents(rs.getByte("CurrentStudents"));
            classes.setMinAgeDescription(rs.getString("MinAgeDescription"));
            classes.setMaxAgeDescription(rs.getString("MaxAgeDescription"));
            classes.setAdditionalInfo(rs.getString("AdditionalInfo"));

            if (rs.getString("InstitutionID") != null) {
                classes.setInstitutionID(UUID.fromString(rs.getString("InstitutionID")));
            }

            return classes;
        }
    };

    // Save method
    public Classes save(Classes classes) {
        if (classes.getClassID() == null) {
            classes.setClassID(UUID.randomUUID());
            return insert(classes);
        } else {
            return update(classes);
        }
    }

    // Insert method
    private Classes insert(Classes classes) {
        String sql = "INSERT INTO " + TABLE_NAME +
                    " (ClassID, ClassName, Capacity, CurrentStudents, MinAgeDescription, MaxAgeDescription, AdditionalInfo, InstitutionID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
            classes.getClassID().toString(),
            classes.getClassName(),
            classes.getCapacity(),
            classes.getCurrentStudents(),
            classes.getMinAgeDescription(),
            classes.getMaxAgeDescription(),
            classes.getAdditionalInfo(),
            classes.getInstitutionID() != null ? classes.getInstitutionID().toString() : null
        );

        return classes;
    }

    // Update method
    private Classes update(Classes classes) {
        String sql = "UPDATE " + TABLE_NAME +
                    " SET ClassName = ?, Capacity = ?, CurrentStudents = ?, MinAgeDescription = ?, " +
                    "MaxAgeDescription = ?, AdditionalInfo = ?, InstitutionID = ? WHERE ClassID = ?";

        jdbcTemplate.update(sql,
            classes.getClassName(),
            classes.getCapacity(),
            classes.getCurrentStudents(),
            classes.getMinAgeDescription(),
            classes.getMaxAgeDescription(),
            classes.getAdditionalInfo(),
            classes.getInstitutionID() != null ? classes.getInstitutionID().toString() : null,
            classes.getClassID().toString()
        );

        return classes;
    }

    // Find by ID
    public Optional<Classes> findById(UUID id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ClassID = ?";
        try {
            Classes classes = jdbcTemplate.queryForObject(sql, CLASSES_ROW_MAPPER, id.toString());
            return Optional.ofNullable(classes);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Find by InstitutionID
    public List<Classes> findByInstitutionId(UUID id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE InstitutionID = ?";
        return jdbcTemplate.query(sql, CLASSES_ROW_MAPPER, id.toString());
    }

    // Find all
    public List<Classes> findAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        return jdbcTemplate.query(sql, CLASSES_ROW_MAPPER);
    }

    // Delete by ID
    public void deleteById(UUID id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE ClassID = ?";
        jdbcTemplate.update(sql, id.toString());
    }

    // Delete entity
    public void delete(Classes classes) {
        deleteById(classes.getClassID());
    }

    // Check if exists by ID
    public boolean existsById(UUID id) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE ClassID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id.toString());
        return count != null && count > 0;
    }

    // Count all
    public long count() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }
}
