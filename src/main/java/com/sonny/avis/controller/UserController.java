package com.sonny.avis.controller;

import com.sonny.avis.entite.Utilisateur;
import com.sonny.avis.service.UtilisateurService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "utilisateur")
public class UserController {

    private UtilisateurService utilisateurService;

    @PreAuthorize("hasAnyAuthority('ADMIN_READ', 'MANAGER_READ')")
    @GetMapping
    public List<Utilisateur> listeUtilisateurs() {
        log.info("Liste des utilisateurs");
        return this.utilisateurService.listeUtilisateurs();
    }
}
