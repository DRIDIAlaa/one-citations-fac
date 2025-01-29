package org.gso.profiles.endpoint;

import java.util.List;
import com.github.rutledgepaulv.qbuilders.builders.GeneralQueryBuilder;
import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import com.github.rutledgepaulv.qbuilders.visitors.MongoVisitor;
import com.github.rutledgepaulv.rqe.pipes.QueryConversionPipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gso.profiles.dto.PageDto;
import org.gso.profiles.dto.ProfileDto;
import org.gso.profiles.model.ProfileModel;
import org.gso.profiles.service.ProfileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping(
        value = ProfileController.PATH,
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class ProfileController {

    public static final String PATH = "/api/v1/profiles";
    public static int MAX_PAGE_SIZE = 200;

    private final ProfileService profileService;
    private QueryConversionPipeline pipeline = QueryConversionPipeline.defaultPipeline();

    // Méthode de création de profil, modifiée pour éviter de passer des informations redondantes
    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<ProfileDto> createProfile(@RequestBody @NonNull ProfileDto profileDto, JwtAuthenticationToken principal) {
        // L'ID de l'utilisateur authentifié est récupéré depuis le token JWT
        profileDto.setUserId(principal.getName());  // Assurez-vous que le ProfileDto a un champ pour l'ID utilisateur

        ProfileDto createdProfile = profileService.createProfile(profileDto.toModel()).toDto();
        return ResponseEntity
                .created(
                        ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/{id}")
                                .buildAndExpand(createdProfile.getId())
                                .toUri()
                ).body(createdProfile);
    }

    // Méthode de récupération du profil d'un utilisateur
    @GetMapping("/{id}")
    public ResponseEntity<ProfileDto> getProfile(@PathVariable("id") @NonNull String profileId, JwtAuthenticationToken principal) {
        // Validation de l'accès à ce profil en fonction de l'utilisateur authentifié
        ProfileModel profile = profileService.getProfile(profileId);
        if (!profile.getUserId().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(profile.toDto());
    }

    // Mise à jour du profil, modifiée pour que l'utilisateur puisse modifier son propre profil uniquement
    @PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<ProfileDto> updateProfile(@PathVariable @NonNull String profileId,
                                                    @RequestBody @NonNull ProfileDto profileDto, JwtAuthenticationToken principal) {
        // Vérification que l'utilisateur authentifié peut modifier ce profil
        if (!profileDto.getUserId().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        profileDto.setId(profileId);
        ProfileDto updatedProfile = profileService.updateProfile(profileDto.toModel()).toDto();
        return ResponseEntity.ok(updatedProfile);
    }

    // Recherche de profils - ici, on peut ajouter une vérification des droits d'accès en fonction du JWT
    @GetMapping("/search")
    public ResponseEntity<PageDto<ProfileDto>> searchProfile(@RequestParam(required = false) String query,
                                                             @PageableDefault(size = 20) Pageable pageable, JwtAuthenticationToken principal) {
        Pageable checkedPageable  = checkPageSize(pageable);
        Criteria criteria = convertQuery(query);
        Page<ProfileModel> results = profileService.searchProfiles(criteria, checkedPageable);
        PageDto<ProfileDto> pageResults = toPageDto(results);
        return ResponseEntity.status(HttpStatus.OK).body(pageResults);
    }

    // Recherche par e-mail - si l'utilisateur ne peut pas voir certains profils, ajoutez une logique de permission ici
    @GetMapping("/search/mail")
    public ResponseEntity<PageDto<ProfileDto>> searchByMail(@RequestParam String mail,
                                                            @PageableDefault(size = 20) Pageable pageable, JwtAuthenticationToken principal) {
        // Optionnel : vérifier que l'utilisateur a la permission de rechercher par e-mail
        Page<ProfileModel> results = profileService.searchByMail(mail, pageable);
        PageDto<ProfileDto> pageResults = toPageDto(results);
        return ResponseEntity.status(HttpStatus.OK).body(pageResults);
    }

    // Récupérer le profil de l'utilisateur actuellement connecté
    @GetMapping("/current")
    public ResponseEntity<ProfileDto> getCurrentUserProfile(JwtAuthenticationToken principal) {
        String userId = principal.getName();
        ProfileDto currentUserProfile = profileService.getProfile(userId).toDto();
        return ResponseEntity.ok(currentUserProfile);
    }

    // Convertir la requête RSQL en critères MongoDB
    private Criteria convertQuery(String stringQuery) {
        Criteria criteria;
        if (StringUtils.hasText(stringQuery)) {
            Condition<GeneralQueryBuilder> condition = pipeline.apply(stringQuery, ProfileModel.class);
            criteria = condition.query(new MongoVisitor());
        } else {
            criteria = new Criteria();
        }
        return criteria;
    }

    // Vérifier la taille de la page pour ne pas dépasser la taille maximale
    private Pageable checkPageSize(Pageable pageable) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            return PageRequest.of(pageable.getPageNumber(), MAX_PAGE_SIZE);
        }
        return pageable;
    }

    // Convertir une page de résultats en DTO pour la pagination
    private PageDto<ProfileDto> toPageDto(Page<ProfileModel> results) {
        List<ProfileDto> profiles = results.map(ProfileModel::toDto).toList();
        PageDto<ProfileDto> pageResults = new PageDto<>();
        pageResults.setData(profiles);
        pageResults.setTotalElements(results.getTotalElements());
        pageResults.setPageSize(results.getSize());
        if (results.hasNext()) {
            pageResults.setNext(
                    ServletUriComponentsBuilder.fromCurrentContextPath()
                            .queryParam("page", results.nextOrLastPageable().getPageNumber())
                            .queryParam("size", results.nextOrLastPageable().getPageSize())
                            .build().toUri());
        }
        pageResults.setFirst(
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .queryParam("page", results.previousOrFirstPageable().getPageNumber())
                        .queryParam("size", results.previousOrFirstPageable().getPageSize())
                        .build().toUri());
        pageResults.setLast(
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .queryParam("page", results.nextOrLastPageable().getPageNumber())
                        .queryParam("size", results.nextOrLastPageable().getPageSize())
                        .build().toUri());
        return pageResults;
    }
}
