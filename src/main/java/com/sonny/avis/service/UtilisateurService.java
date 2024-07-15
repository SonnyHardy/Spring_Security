package com.sonny.avis.service;

import com.sonny.avis.TypeDeRole;
import com.sonny.avis.entite.Role;
import com.sonny.avis.entite.Utilisateur;
import com.sonny.avis.entite.Validation;
import com.sonny.avis.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UtilisateurService implements UserDetailsService {

    private UtilisateurRepository utilisateurRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private ValidationService validationService;

    public void inscription(Utilisateur utilisateur) {
        final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        // Valider l'adresse e-mail
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher email = pattern.matcher(utilisateur.getEmail());
        if(!email.matches()) {
            throw new RuntimeException("Votre mail est invalide");
        }
        Optional<Utilisateur> optionalUtilisateur = this.utilisateurRepository.findByEmail(utilisateur.getEmail());
        if(optionalUtilisateur.isPresent()) {
            throw new RuntimeException("Votre mail est deja utilise");
        }

        String mdpCrypte = this.passwordEncoder.encode(utilisateur.getMdp());
        utilisateur.setMdp(mdpCrypte);

        Role roleUtilisateur = new Role();
        roleUtilisateur.setLibelle(TypeDeRole.UTILISATEUR);
        utilisateur.setRole(roleUtilisateur);

        utilisateur = this.utilisateurRepository.save(utilisateur);
        this.validationService.enregistrer(utilisateur);
    }

    public void activation(Map<String, String> activation) {
        Validation validation = this.validationService.lireEnFonctionDuCode(activation.get("code"));
        if (Instant.now().isAfter(validation.getExpiration())){
            throw new RuntimeException("Votre code a expire");
        }
        Utilisateur utilisateur = this.utilisateurRepository.findById(validation.getUtilisateur().getId()).orElseThrow(() ->
                new RuntimeException("Utilisateur inconnu"));

        utilisateur.setActif(true);
        this.utilisateurRepository.save(utilisateur);
    }

    @Override
    public Utilisateur loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.utilisateurRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur ne correspond a cet identifiant"));
    }

    public void modifierMotDePasse(Map<String, String> parametres) {
        Utilisateur utilisateur = this.loadUserByUsername(parametres.get("email"));
        this.validationService.enregistrer(utilisateur);
    }

    public void nouveauMotDePasse(Map<String, String> parametres) {
        Utilisateur utilisateur = this.loadUserByUsername(parametres.get("email"));
        final Validation validation = validationService.lireEnFonctionDuCode(parametres.get("code"));
        if (validation.getUtilisateur().getEmail().equals(utilisateur.getEmail())) {
            String mdpCrypte = this.passwordEncoder.encode(parametres.get("password"));
            utilisateur.setMdp(mdpCrypte);
            this.utilisateurRepository.save(utilisateur);
        }

    }


}
