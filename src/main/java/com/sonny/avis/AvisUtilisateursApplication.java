package com.sonny.avis;

import com.sonny.avis.entite.Role;
import com.sonny.avis.entite.Utilisateur;
import com.sonny.avis.enums.TypeDeRole;
import com.sonny.avis.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableScheduling
@AllArgsConstructor
@SpringBootApplication
public class AvisUtilisateursApplication implements CommandLineRunner {

    private UtilisateurRepository utilisateurRepository;
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(AvisUtilisateursApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Utilisateur admin = Utilisateur.builder()
                .actif(true)
                .nom("Admin")
                .mdp(passwordEncoder.encode("admin"))
                .email("admin@gmail.com")
                .role(
                    Role.builder()
                            .libelle(TypeDeRole.ADMIN)
                            .build()
                )
                .build();
         if (this.utilisateurRepository.findByEmail("admin@gmail.com").isEmpty()) {
             this.utilisateurRepository.save(admin);
         }

        Utilisateur manager = Utilisateur.builder()
                .actif(true)
                .nom("Manager")
                .mdp(passwordEncoder.encode("manager"))
                .email("manager@gmail.com")
                .role(
                    Role.builder()
                            .libelle(TypeDeRole.MANAGER)
                            .build()
                )
                .build();
        if (this.utilisateurRepository.findByEmail("manager@gmail.com").isEmpty()) {
            this.utilisateurRepository.save(manager);
        }
    }
}
