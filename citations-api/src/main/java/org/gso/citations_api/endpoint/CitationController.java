package org.gso.citations_api.endpoint;

import com.github.rutledgepaulv.rqe.pipes.QueryConversionPipeline;
import lombok.extern.slf4j.Slf4j;
import org.gso.citations_api.dto.CitationDto;
import org.gso.citations_api.model.CitationModel;
import org.gso.citations_api.service.CitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8888")

@Slf4j
@RestController
@RequestMapping(
        value = CitationController.PATH,
        produces = MediaType.APPLICATION_JSON_VALUE
)

public class CitationController {
    public static final String PATH = "/api/v1/citations";
    public static int MAX_PAGE_SIZE = 200;
    private QueryConversionPipeline pipeline = QueryConversionPipeline.defaultPipeline();

    private final CitationService citationService;

    public CitationController(CitationService citationService) {
        this.citationService = citationService;
    }


    @GetMapping("/random")
    public ResponseEntity<CitationDto> getRandomCitation() {
        return ResponseEntity.ok(citationService.getRandomCitation().toDto());
    }

    @PostMapping
    public ResponseEntity<CitationModel> submitCitation(@RequestBody CitationDto citationDto, Authentication authentication) {
        System.out.println("User Authorities: " + authentication.getAuthorities());

        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("writer"))) {
            throw new AccessDeniedException("Forbidden");
        }
        CitationModel createdCitation = citationService.submitCitation(citationDto.toModel(), authentication.getName());
        return ResponseEntity
                .created(
                        ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/api/v1/citations/{id}")
                                .buildAndExpand(createdCitation.getId())
                                .toUri()
                ).body(createdCitation);
    }

    @GetMapping("/unvalidated")
    public ResponseEntity<List<CitationDto>> getUnvalidatedCitations(Authentication authentication) {
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("moderator"))) {
            throw new AccessDeniedException("Forbidden");
        }
        List<CitationDto> citations = citationService.getUnvalidatedCitations();
        return ResponseEntity.ok(citations);
    }

    @PutMapping("/{id}/validate")
    public ResponseEntity<CitationDto> validateCitation(@PathVariable String id,
                                                        Authentication authentication) {
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("moderator"))) {
            throw new AccessDeniedException("Forbidden");
        }
        String moderatorId = authentication.getName();
        CitationDto validatedCitation = citationService.validateCitation(id, moderatorId);
        return ResponseEntity.ok(validatedCitation);
    }

}

