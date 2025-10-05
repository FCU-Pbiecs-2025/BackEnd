package Group4.Childcare.Controller;


import Group4.Childcare.Model.Banners;
import Group4.Childcare.Service.BannersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
public class BannersController {

  @Autowired
  private BannersService bannersService;

  @GetMapping("")
  public List<Banners> getAllBanners() {
    return bannersService.getAllBanners();
  }

  @PostMapping("/create")
  public Banners createBanner(@RequestBody Banners banner) {
    return bannersService.createBanner(banner);
  }

  // 更新 Banner
  @PutMapping("/update")
  public Banners updateBanner(@RequestBody Banners banner) {
    return bannersService.updateBanner(banner);
  }

  @DeleteMapping("/delete/{sortOrder}")
  public boolean deleteBanner(@PathVariable int sortOrder) {
    return bannersService.deleteBanner(sortOrder);
  }
}
