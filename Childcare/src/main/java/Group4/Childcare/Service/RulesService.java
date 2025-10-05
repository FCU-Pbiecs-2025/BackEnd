package Group4.Childcare.Service;

import Group4.Childcare.Model.Rules;
import Group4.Childcare.Repository.RulesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RulesService {
    @Autowired
    private RulesRepository rulesRepository;

    public Rules getRule() {
        return rulesRepository.findSingle();
    }

    public Rules updateRule(Rules rule) {
        return rulesRepository.update(rule);
    }
}
