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
public class ProfileDtoForAdmin {

    private String id;           // Identifiant unique du profil
    private String firstName;    // Prénom de l'utilisateur
    private String lastName;     // Nom de famille de l'utilisateur
    private String mail;         // Adresse e-mail de l'utilisateur (visible pour l'admin)
    private String phoneNumber;  // Numéro de téléphone de l'utilisateur (visible pour l'admin)
    private int age;             // Âge de l'utilisateur
    private LocalDateTime created;   // Date de création du profil
    private LocalDateTime modified;  // Date de dernière modification
}
