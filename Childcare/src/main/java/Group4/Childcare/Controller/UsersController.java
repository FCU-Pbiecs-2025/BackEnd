package Group4.Childcare.Controller;

import Group4.Childcare.Model.Users;
import Group4.Childcare.Service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UsersService usersService;

    @PostMapping
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        return ResponseEntity.ok(usersService.createUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable UUID id) {
        Optional<Users> user = usersService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Users> getAllUsers() {
        return usersService.getAllUsers();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable UUID id, @RequestBody Users user) {
        return ResponseEntity.ok(usersService.updateUser(id, user));
    }
}

