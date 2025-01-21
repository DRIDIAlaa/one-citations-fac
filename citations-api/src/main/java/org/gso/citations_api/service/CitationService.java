package org.gso.citations_api.service;

import org.gso.citations_api.dto.CitationDto;
import org.gso.citations_api.dto.ProfileDto;
import org.gso.citations_api.model.CitationModel;
import org.gso.citations_api.model.ProfileModel;
import org.gso.citations_api.repository.CitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitationService {

    private final CitationRepository citationRepository;
    private final ProfileService profileService;

    @Autowired
    public CitationService(CitationRepository citationRepository, ProfileService profileService) {
        this.citationRepository = citationRepository;
        this.profileService = profileService;
    }

    public CitationModel getRandomCitation() {
        return citationRepository.findTopByOrderBySubmissionDateDesc();
    }

    @Secured("ROLE_WRITER")
    public CitationModel submitCitation(CitationModel citationModel) {
        CitationModel citation = new CitationModel();
        citation.setText(citationModel.getText());

        // Récupérer l'ID de l'auteur via le service ProfileService
        ProfileModel author = profileService.getProfile(citationModel.getAuthorId());
        citation.setAuthorId(author.getId()); // Stocker l'ID de l'auteur

        citation.setSubmissionDate(LocalDateTime.now());
        return citationRepository.save(citation);
    }


    public List<CitationDto> getUnvalidatedCitations() {
        return citationRepository.findByValidatedFalse()
                .stream()
                .map(CitationModel::toDto)
                .collect(Collectors.toList());
    }

    public CitationDto validateCitation(String citationId, String moderatorId) {
        CitationModel citation = citationRepository.findById(citationId)
                .orElseThrow(() -> new IllegalArgumentException("Citation not found"));

        citation.setValidated(true);
        citation.setValidatorId(moderatorId);
        citation.setModificationDate(LocalDateTime.now());
        citationRepository.save(citation);

        return citation.toDto();
    }
}

