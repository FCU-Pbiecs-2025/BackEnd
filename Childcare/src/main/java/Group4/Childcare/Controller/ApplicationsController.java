package Group4.Childcare.Controller;

import Group4.Childcare.Model.Applications;
import Group4.Childcare.DTO.ApplicationSummaryDTO;
import Group4.Childcare.DTO.ApplicationSummaryWithDetailsDTO;
import Group4.Childcare.Service.ApplicationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/applications")
public class ApplicationsController {
    private final ApplicationsService service;

    @Autowired
    public ApplicationsController(ApplicationsService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Applications> create(@RequestBody Applications entity) {
        return ResponseEntity.ok(service.create(entity));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Applications> update(@PathVariable UUID id, @RequestBody Applications entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @GetMapping("/application-status/{userID}")
    public ResponseEntity<List<ApplicationSummaryDTO>> getSummaryByUserID(@PathVariable UUID userID) {
        return ResponseEntity.ok(service.getSummaryByUserID(userID));
    }

    // New endpoint to expose JDBC offset API
    @GetMapping("/offset")
    public ResponseEntity<Object> getWithOffset(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int size) {
        // basic validation and sanitization
        if (offset < 0) {
            return ResponseEntity.badRequest().build();
        }
        if (size <= 0) {
            return ResponseEntity.badRequest().build();
        }
        // cap size to prevent abuse
        final int MAX_SIZE = 100;
        if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }

        // Fetch content and total count
        List<ApplicationSummaryWithDetailsDTO> content = service.getSummariesWithOffset(offset, size);
        long totalElements = service.getTotalApplicationsCount(); // Assume this method exists in the service
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean hasNext = offset + size < totalElements;

        // Build response with corrected field placement
        Map<String, Object> response = Map.of(
                "totalPages", totalPages,
                "hasNext", hasNext,
                "offset", offset,
                "size", size,
                "content", content,
                "totalElements", totalElements
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ApplicationSummaryWithDetailsDTO>> searchApplications(
            @RequestParam(required = false) String institutionID,
            @RequestParam(required = false) String institutionName,
            @RequestParam(required = false) String applicationID) {
        List<ApplicationSummaryWithDetailsDTO> result = service.searchApplications(institutionID, institutionName, applicationID);
        return ResponseEntity.ok(result);
    }
}
