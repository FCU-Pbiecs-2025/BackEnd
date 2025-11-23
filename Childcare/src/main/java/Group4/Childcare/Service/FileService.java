package Group4.Childcare.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileService {

  @Value("${attachment.upload.dir:IdentityResource}")
  private String uploadDir;

  /**
   * 讀取指定案件的所有檔案名稱
   * 檔案夾位置: IdentityResource/{ApplicationID}/
   * @param applicationId 案件ID
   * @return 檔案名稱列表
   */
  public List<String> getFilesByApplicationId(UUID applicationId) {
    try {
      Path folderPath = Paths.get(uploadDir, applicationId.toString());

      // 如果文件夾不存在，返回空列表
      if (!Files.exists(folderPath)) {
        return new ArrayList<>();
      }

      File folder = folderPath.toFile();
      if (!folder.isDirectory()) {
        return new ArrayList<>();
      }

      // 讀取文件夾中的所有檔案名稱
      File[] files = folder.listFiles(File::isFile);
      if (files == null || files.length == 0) {
        return new ArrayList<>();
      }

      return Arrays.stream(files)
                   .map(File::getName)
                   .sorted()
                   .collect(Collectors.toList());
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  /**
   * 檢查指定案件的文件夾是否存在
   * 檔案夾位置: IdentityResource/{ApplicationID}/
   * @param applicationId 案件ID
   * @return true 如果存在，false 否則
   */
  public boolean folderExists(UUID applicationId) {
    try {
      Path folderPath = Paths.get(uploadDir, applicationId.toString());
      return Files.exists(folderPath) && Files.isDirectory(folderPath);
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * 建立案件的文件夾
   * 檔案夾位置: IdentityResource/{ApplicationID}/
   * @param applicationId 案件ID
   * @return true 如果成功，false 否則
   */
  public boolean createFolder(UUID applicationId) {
    try {
      Path folderPath = Paths.get(uploadDir, applicationId.toString());
      if (!Files.exists(folderPath)) {
        Files.createDirectories(folderPath);
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 獲取案件文件夾的完整路徑
   * 檔案夾位置: IdentityResource/{ApplicationID}/
   * @param applicationId 案件ID
   * @return 文件夾路徑
   */
  public Path getFolderPath(UUID applicationId) {
    return Paths.get(uploadDir, applicationId.toString());
  }
}

