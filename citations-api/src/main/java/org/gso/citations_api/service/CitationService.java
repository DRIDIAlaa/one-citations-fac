package org.gso.citations_api.service;

import lombok.RequiredArgsConstructor;
import org.gso.citations_api.dto.CitationDto;
import org.gso.citations_api.model.CitationModel;
import org.gso.citations_api.repository.CitationRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitationService {

    private final CitationRepository citationRepository;
    public CitationModel getRandomCitation() {
        return citationRepository.findTopByOrderBySubmissionDateDesc();
    }

    public CitationModel submitCitation(CitationModel citationModel, String writerName) {
        citationModel.setSubmissionDate(LocalDateTime.now());
        citationModel.setWriterName(writerName);
        return citationRepository.save(citationModel);
    }


    public List<CitationDto> getUnvalidatedCitations() {
        return citationRepository.findByValidatedFalse()
                .stream()
                .map(CitationModel::toDto)
                .collect(Collectors.toList());
    }

    public CitationDto validateCitation(String citationId, String moderatorName) {
        CitationModel citation = citationRepository.findById(citationId)
                .orElseThrow(() -> new IllegalArgumentException("Citation not found"));

        citation.setValidated(true);
        citation.setValidatorName(moderatorName);
        citation.setModificationDate(LocalDateTime.now());
        citationRepository.save(citation);

        return citation.toDto();
    }
}

