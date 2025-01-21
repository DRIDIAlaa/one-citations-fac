package org.gso.citations_api.repository;

import org.gso.citations_api.model.CitationModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CitationRepository extends MongoRepository<CitationModel, String> {
    CitationModel findTopByOrderBySubmissionDateDesc(); // Exemple de méthode de recherche
    List<CitationModel> findByValidatedFalse();
}
