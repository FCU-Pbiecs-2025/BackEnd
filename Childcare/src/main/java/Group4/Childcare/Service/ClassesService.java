package Group4.Childcare.Service;

import Group4.Childcare.Model.Classes;
import Group4.Childcare.Repository.ClassesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ClassesService {
    @Autowired
    private ClassesRepository classesRepository;

    public List<Classes> getAllClasses() {
        return classesRepository.findAll();
    }

    public Classes getClassById(UUID id) {
        return classesRepository.findById(id);
    }

    public Classes createClass(Classes cls) {
        return classesRepository.save(cls);
    }

    public Classes updateClass(Classes cls) {
        return classesRepository.update(cls);
    }

    public boolean deleteClass(UUID id) {
        return classesRepository.delete(id);
    }
}

