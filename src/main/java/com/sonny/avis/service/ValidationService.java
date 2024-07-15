package com.sonny.avis.service;

import com.sonny.avis.entite.Utilisateur;
import com.sonny.avis.entite.Validation;
import com.sonny.avis.repository.ValidationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ValidationService {

    private ValidationRepository validationRepository;
    private NotificationService notificationService;

    public void enregistrer(Utilisateur utilisateur) {
        Validation validation = new Validation();
        validation.setUtilisateur(utilisateur);
        Instant creation = Instant.now();
        Instant expiration = creation.plus(10, ChronoUnit.MINUTES);
        validation.setCreation(creation);
        validation.setExpiration(expiration);

        Random random = new Random();
        int randomInteger = random.nextInt(999999);
        String code = String.format("%06d",randomInteger);
        validation.setCode(code);

        this.validationRepository.save(validation);
        this.notificationService.envoyerCode(validation);
    }

    public Validation lireEnFonctionDuCode(String code) {
        return this.validationRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Votre code est invalide"));
    }

    @Scheduled(cron = "0 */1 * * * *")     // Apres chaque minute
    public void nettoyerTable() {
        log.info("Suppression des validations expirees a {}", Instant.now());
        this.validationRepository.deleteAllByExpirationBefore(Instant.now());
    }

}
