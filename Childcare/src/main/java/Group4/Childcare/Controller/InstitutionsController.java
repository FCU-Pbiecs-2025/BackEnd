package Group4.Childcare.Controller;

import Group4.Childcare.Model.Institutions;
import Group4.Childcare.Service.InstitutionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/institutions")
public class InstitutionsController {
    @Autowired
    private InstitutionsService institutionsService;

    @GetMapping("")
    public List<Institutions> getAllInstitutions() {
        return institutionsService.getAllInstitutions();
    }

    @GetMapping("/{id}")
    public Institutions getInstitutionById(@PathVariable UUID id) {
        return institutionsService.getInstitutionById(id);
    }

    @PostMapping("/create")
    public Institutions createInstitution(@RequestBody Institutions institution) {
        return institutionsService.createInstitution(institution);
    }

    @PutMapping("/update")
    public Institutions updateInstitution(@RequestBody Institutions institution) {
        return institutionsService.updateInstitution(institution);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteInstitution(@PathVariable UUID id) {
        return institutionsService.deleteInstitution(id);
    }
}

