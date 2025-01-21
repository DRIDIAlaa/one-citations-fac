package org.gso.citations_api.endpoint;

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

@RestController
@RequestMapping("/api/v1/citations")
public class CitationController {

    private final CitationService citationService;

    @Autowired
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

