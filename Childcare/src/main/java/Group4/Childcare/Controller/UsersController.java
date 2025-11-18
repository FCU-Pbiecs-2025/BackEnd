package Group4.Childcare.Controller;

import Group4.Childcare.Model.Users;
import Group4.Childcare.Service.UsersService;
import Group4.Childcare.DTO.UserSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

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

  @GetMapping("/offset")
  public ResponseEntity<Map<String, Object>> getUsersByOffsetJdbc(
          @RequestParam(defaultValue = "0") int offset,
          @RequestParam(defaultValue = "10") int size) {
    try {
      if (offset < 0 || size <= 0) {
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid offset/size"));
      }

      final int MAX_SIZE = 100;
      if (size > MAX_SIZE) size = MAX_SIZE;

      List<UserSummaryDTO> users = usersService.getUsersWithOffsetAndInstitutionNameJdbc(offset, size);
      long totalCount = usersService.getTotalCount();

      Map<String, Object> response = new HashMap<>();
      response.put("content", users);
      response.put("offset", offset);
      response.put("size", size);
      response.put("totalElements", totalCount);
      response.put("totalPages", (int) Math.ceil((double) totalCount / size));
      response.put("hasNext", offset + size < totalCount);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      System.err.println("Error in getUsersByOffsetJdbc: " + e.getMessage());
      e.printStackTrace();

      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("error", "Internal server error");
      errorResponse.put("message", e.getMessage());
      return ResponseEntity.status(500).body(errorResponse);
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Users> updateUser(@PathVariable UUID id, @RequestBody Users user) {
    return ResponseEntity.ok(usersService.updateUser(id, user));
  }

}
