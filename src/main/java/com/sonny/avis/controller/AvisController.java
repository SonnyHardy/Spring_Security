package com.sonny.avis.controller;

import com.sonny.avis.entite.Avis;
import com.sonny.avis.service.AvisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "avis", produces = MediaType.APPLICATION_JSON_VALUE)
public class AvisController {

    private final AvisService avisService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public void creer(@RequestBody Avis avis) {
        this.avisService.creer(avis);
        log.info("Avis cree");
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping
    public @ResponseBody Iterable<Avis> listeAvis() {
        log.info("Liste des avis");
        return this.avisService.listeAvis();
    }
}
