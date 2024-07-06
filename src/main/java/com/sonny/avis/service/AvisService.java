package com.sonny.avis.service;

import com.sonny.avis.entite.Avis;
import com.sonny.avis.repository.AvisRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AvisService {

    private final AvisRepository avisRepository;

    public void creer(Avis avis) {
        this.avisRepository.save(avis);
    }
}
