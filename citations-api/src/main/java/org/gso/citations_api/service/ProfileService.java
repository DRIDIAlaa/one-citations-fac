package org.gso.citations_api.service;

import lombok.RequiredArgsConstructor;
import org.gso.citations_api.model.ProfileModel;
import org.gso.citations_api.repository.ProfileRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    public ProfileModel getProfile(String profileId) {
        return profileRepository.findById(profileId).orElseThrow();
    }
}
