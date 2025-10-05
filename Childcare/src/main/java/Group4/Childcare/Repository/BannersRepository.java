package Group4.Childcare.Repository;

import Group4.Childcare.Model.Announcements;
import Group4.Childcare.Model.Banners;
import Group4.Childcare.Service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class BannersRepository  {


  @Autowired
  private DatabaseService databaseService;

  public List<Banners> findAll() {
    List<Banners> banners = new ArrayList<>();
    String sql = "SELECT * FROM banners";
    try (Connection conn = databaseService.connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        Banners banner = new Banners();
        banner.setSortOrder(rs.getInt("SortOrder"));
        banner.setStartTime(rs.getObject("StartTime", LocalDateTime.class));
        banner.setEndTime(rs.getObject("EndTime", LocalDateTime.class));
        banner.setImageUrl(rs.getString("ImageUrl"));
        banner.setLinkUrl(rs.getString("LinkUrl"));
        banner.setStatus(rs.getBoolean("Status"));
        banners.add(banner);

      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return banners;
  }


  public Banners save(Banners banner) {
      //先去FindbyAll查詢現在有幾個banner
      List<Banners> banners = findAll();
      int newSortOrder = banners.size() + 1; //新的SortOrder是目前數量+1
      banner.setSortOrder(newSortOrder); //設定新的SortOrder
      String sql = "INSERT INTO banners (SortOrder, StartTime, EndTime, ImageUrl, LinkUrl, Status) VALUES (?, ?, ?, ?, ?, ?)";
      try (Connection conn = databaseService.connect();
           PreparedStatement pstmt = conn.prepareStatement(sql)) {
          pstmt.setInt(1, banner.getSortOrder());
          pstmt.setObject(2, banner.getStartTime());
          pstmt.setObject(3, banner.getEndTime());
          pstmt.setString(4, banner.getImageUrl());
          pstmt.setString(5, banner.getLinkUrl());
          pstmt.setBoolean(6, banner.getStatus());
          pstmt.executeUpdate();
      } catch (SQLException e) {
          e.printStackTrace();
      }
      return banner;
  }

  //刪除
  public void deleteById(Integer sortOrder) {
      String sql = "DELETE FROM banners WHERE SortOrder = ?";
      try (Connection conn = databaseService.connect();
           PreparedStatement pstmt = conn.prepareStatement(sql)) {
        if (sortOrder!= null){
          pstmt.setInt(1, sortOrder);
          pstmt.executeUpdate();
        }else {
          System.out.println("SortOrder is null, cannot delete.");
        }
      } catch (SQLException e) {
          e.printStackTrace();
      }
  }

  //更新
  public Banners update(Banners banner) {
      String sql = "UPDATE banners SET StartTime = ?, EndTime = ?, ImageUrl = ?, LinkUrl = ?, Status = ? WHERE SortOrder = ?";
      try (Connection conn = databaseService.connect();
           PreparedStatement pstmt = conn.prepareStatement(sql)) {
          pstmt.setObject(1, banner.getStartTime());
          pstmt.setObject(2, banner.getEndTime());
          pstmt.setString(3, banner.getImageUrl());
          pstmt.setString(4, banner.getLinkUrl());
          pstmt.setBoolean(5, banner.getStatus());
          pstmt.setInt(6, banner.getSortOrder());
          pstmt.executeUpdate();
      } catch (SQLException e) {
          e.printStackTrace();
      }
      return banner;
  }

}
