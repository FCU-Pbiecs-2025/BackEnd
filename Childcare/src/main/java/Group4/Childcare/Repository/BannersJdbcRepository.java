package Group4.Childcare.Repository;

import Group4.Childcare.Model.Banners;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class BannersJdbcRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TABLE_NAME = "banners";

    // RowMapper for Banners entity
    private static final RowMapper<Banners> BANNERS_ROW_MAPPER = new RowMapper<Banners>() {
        @Override
        public Banners mapRow(ResultSet rs, int rowNum) throws SQLException {
            Banners banner = new Banners();
            banner.setSortOrder(rs.getInt("SortOrder"));

            if (rs.getTimestamp("StartTime") != null) {
                banner.setStartTime(rs.getTimestamp("StartTime").toLocalDateTime());
            }

            if (rs.getTimestamp("EndTime") != null) {
                banner.setEndTime(rs.getTimestamp("EndTime").toLocalDateTime());
            }

            banner.setImageUrl(rs.getString("ImageUrl"));
            banner.setLinkUrl(rs.getString("LinkUrl"));
            banner.setStatus(rs.getBoolean("Status"));

            return banner;
        }
    };

    // Save method
    public Banners save(Banners banner) {
        if (existsById(banner.getSortOrder())) {
            return update(banner);
        } else {
            return insert(banner);
        }
    }

    // Insert method
    private Banners insert(Banners banner) {
        String sql = "INSERT INTO " + TABLE_NAME +
                    " (SortOrder, StartTime, EndTime, ImageUrl, LinkUrl, Status) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
            banner.getSortOrder(),
            banner.getStartTime(),
            banner.getEndTime(),
            banner.getImageUrl(),
            banner.getLinkUrl(),
            banner.getStatus()
        );

        return banner;
    }

    // Update method
    private Banners update(Banners banner) {
        String sql = "UPDATE " + TABLE_NAME +
                    " SET StartTime = ?, EndTime = ?, ImageUrl = ?, LinkUrl = ?, Status = ? " +
                    "WHERE SortOrder = ?";

        jdbcTemplate.update(sql,
            banner.getStartTime(),
            banner.getEndTime(),
            banner.getImageUrl(),
            banner.getLinkUrl(),
            banner.getStatus(),
            banner.getSortOrder()
        );

        return banner;
    }

    // Find by ID
    public Optional<Banners> findById(Integer id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE SortOrder = ?";
        try {
            Banners banner = jdbcTemplate.queryForObject(sql, BANNERS_ROW_MAPPER, id);
            return Optional.ofNullable(banner);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Find all
    public List<Banners> findAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        return jdbcTemplate.query(sql, BANNERS_ROW_MAPPER);
    }

    // Delete by ID
    public void deleteById(Integer id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE SortOrder = ?";
        jdbcTemplate.update(sql, id);
    }

    // Delete entity
    public void delete(Banners banner) {
        deleteById(banner.getSortOrder());
    }

    // Check if exists by ID
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE SortOrder = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    // Count all
    public long count() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }
}
