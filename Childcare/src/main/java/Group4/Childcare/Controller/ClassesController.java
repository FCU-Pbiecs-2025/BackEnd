package Group4.Childcare.Controller;

import Group4.Childcare.Model.Classes;
import Group4.Childcare.Service.ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/classes")
public class ClassesController {
    @Autowired
    private ClassesService classesService;

    @GetMapping("")
    public List<Classes> getAllClasses() {
        return classesService.getAllClasses();
    }

    @GetMapping("/{id}")
    public Classes getClassById(@PathVariable UUID id) {
        return classesService.getClassById(id);
    }

    @PostMapping("/create")
    public Classes createClass(@RequestBody Classes cls) {
        return classesService.createClass(cls);
    }

    @PutMapping("/update")
    public Classes updateClass(@RequestBody Classes cls) {
        return classesService.updateClass(cls);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteClass(@PathVariable UUID id) {
        return classesService.deleteClass(id);
    }
}

