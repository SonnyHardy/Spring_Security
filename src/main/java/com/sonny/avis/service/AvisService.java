package com.sonny.avis.service;

import com.sonny.avis.entite.Avis;
import com.sonny.avis.entite.Utilisateur;
import com.sonny.avis.repository.AvisRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AvisService {

    private final AvisRepository avisRepository;

    public void creer(Avis avis) {
        Utilisateur utilisateur = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        avis.setUtilisateur(utilisateur);
        this.avisRepository.save(avis);
    }
}
