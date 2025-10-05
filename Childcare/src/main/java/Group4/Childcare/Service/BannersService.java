package Group4.Childcare.Service;


import Group4.Childcare.Model.Banners;
import Group4.Childcare.Repository.BannersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannersService {

  @Autowired
  private BannersRepository bannersRepository;
  //findAll, save, update, delete
  public List<Banners> getAllBanners() {
    return bannersRepository.findAll();
  }

  public Banners createBanner(Banners banner) {
    return bannersRepository.save(banner);
  }

  // 更新 Banner 資料
  public Banners updateBanner(Banners banner) {
    return bannersRepository.update(banner);
  }

  public boolean deleteBanner(int sortOrder) {
    bannersRepository.deleteById(sortOrder);
    return true;
  }
}
