package com.sonny.avis.controller;

import com.sonny.avis.dto.AuthentificationDTO;
import com.sonny.avis.entite.Utilisateur;
import com.sonny.avis.securite.JwtService;
import com.sonny.avis.service.UtilisateurService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class UtilisateurController {

    private UtilisateurService utilisateurService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @PostMapping(path = "inscription")
    public void inscription(@RequestBody Utilisateur utilisateur) {
        this.utilisateurService.inscription(utilisateur);
        log.info("Inscription");
    }

    @PostMapping(path = "activation")
    public void activation(@RequestBody Map<String, String> activation) {
        this.utilisateurService.activation(activation);
        log.info("Activation");
    }

    @PostMapping(path = "connexion")
    public Map<String, String> connexion(@RequestBody AuthentificationDTO authentificationDTO) {
        final Authentication authenticate = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authentificationDTO.username(), authentificationDTO.password())
        );

        if (authenticate.isAuthenticated()){
            log.info("Resultat {}", authenticate.isAuthenticated());
            log.info("Connexion");
            return this.jwtService.generateToken(authentificationDTO.username());
        }

        return null;
    }

    @PostMapping(path = "deconnexion")
    public void deconnexion() {
        this.jwtService.deconnexion();
        log.info("Deconnexion");
    }

    @PostMapping(path = "modifier-password")
    public void modifierMotDePasse(@RequestBody Map<String, String > activation) {
        this.utilisateurService.modifierMotDePasse(activation);
        log.info("Modification du mot de passe");
    }

    @PostMapping(path = "nouveau-password")
    public void nouveauMotDePasse(@RequestBody Map<String, String > activation) {
        this.utilisateurService.nouveauMotDePasse(activation);
        log.info("Nouveau du mot de passe");
    }

}
