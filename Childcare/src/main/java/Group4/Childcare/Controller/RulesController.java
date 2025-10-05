package Group4.Childcare.Controller;

import Group4.Childcare.Model.Rules;
import Group4.Childcare.Service.RulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rules")
public class RulesController {
    @Autowired
    private RulesService rulesService;

    @GetMapping("")
    public Rules getRule() {
        return rulesService.getRule();
    }

    @PutMapping("/update")
    public Rules updateRule(@RequestBody Rules rule) {
        return rulesService.updateRule(rule);
    }
}
