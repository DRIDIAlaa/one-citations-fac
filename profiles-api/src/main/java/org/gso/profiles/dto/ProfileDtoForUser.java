package org.gso.profiles.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDtoForUser {

    private String id;          // Identifiant unique du profil
    private String firstName;   // Prénom de l'utilisateur
    private String lastName;    // Nom de famille de l'utilisateur
    private int age;            // Âge de l'utilisateur
    private LocalDateTime created;  // Date de création du profil
    private LocalDateTime modified; // Date de dernière modification

}
