package org.gso.profiles.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gso.profiles.dto.ProfileDto;
import org.gso.profiles.dto.ProfileDtoForAdmin;
import org.gso.profiles.dto.ProfileDtoForUser;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class ProfileModel {

    @Id
    private String id;
    private String userId;

    @Email
    private String mail;
    private String phoneNumber;  // Ajout du champ pour le numéro de téléphone

    private int age;
    private String firstName;
    private String lastName;

    @CreatedDate
    private LocalDateTime created;

    @LastModifiedDate
    private LocalDateTime modified;

    // Convertir ProfileModel en ProfileDtoForUser (accès utilisateur)
    public ProfileDtoForUser toDtoForUser() {
        return ProfileDtoForUser.builder()
                .id(this.id)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .age(this.age)
                .created(this.created)
                .modified(this.modified)
                .build();
    }

    // Convertir ProfileModel en ProfileDtoForAdmin (accès admin)
    public ProfileDtoForAdmin toDtoForAdmin() {
        return ProfileDtoForAdmin.builder()
                .id(this.id)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .mail(this.mail)
                .phoneNumber(this.phoneNumber)
                .age(this.age)
                .created(this.created)
                .modified(this.modified)
                .build();
    }

    // Convertir ProfileModel en ProfileDto général
    public ProfileDto toDto() {
        return ProfileDto.builder()
                .id(this.id)
                .userId(this.userId)
                .mail(this.mail)
                .age(this.age)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .created(this.created)
                .modified(this.modified)
                .build();
    }
}
