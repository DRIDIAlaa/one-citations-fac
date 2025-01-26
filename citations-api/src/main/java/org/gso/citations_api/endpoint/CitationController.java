package org.gso.citations_api.endpoint;

import com.github.rutledgepaulv.rqe.pipes.QueryConversionPipeline;
import lombok.extern.slf4j.Slf4j;
import org.gso.citations_api.dto.CitationDto;
import org.gso.citations_api.service.CitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<CitationDto> submitCitation(@RequestBody CitationDto citationDto,
                                                      Authentication authentication) {
        // Get the current user's information from the Authentication object
        // Add submission date
        citationDto.setSubmissionDate(LocalDateTime.now());

        // Save the citation
        CitationDto createdCitation = citationService.submitCitation(citationDto.toModel()).toDto();

        return ResponseEntity
                .created(
                        ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/api/v1/citations/{id}")
                                .buildAndExpand(createdCitation.getId())
                                .toUri()
                ).body(createdCitation);
    }

    @GetMapping("/unvalidated")
    @PreAuthorize("hasRole('moderator')")
    public ResponseEntity<List<CitationDto>> getUnvalidatedCitations() {
        List<CitationDto> citations = citationService.getUnvalidatedCitations();
        return ResponseEntity.ok(citations);
    }

    @PutMapping("/{id}/validate")
    @PreAuthorize("hasRole('moderator')")
    public ResponseEntity<CitationDto> validateCitation(@PathVariable String id,
                                                        Authentication authentication) {
        String moderatorId = authentication.getName();
        CitationDto validatedCitation = citationService.validateCitation(id, moderatorId);
        return ResponseEntity.ok(validatedCitation);
    }

}

