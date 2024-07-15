package com.sonny.avis.securite;

import com.sonny.avis.entite.Jwt;
import com.sonny.avis.entite.Utilisateur;
import com.sonny.avis.repository.JwtRepository;
import com.sonny.avis.service.UtilisateurService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class JwtService {

    private final String ENCRYPTION_KEY = "48779b646e7ae8bf0ba932b3a4062fe6bb1e565ace402bca87bd783bc32fd581";
    private UtilisateurService utilisateurService;
    private JwtRepository jwtRepository;

    public Jwt tokenByValue(String value) {
        return this.jwtRepository.findByValeur(value)
                .orElseThrow(() -> new RuntimeException("Token inconnu"));
    }

    public Map<String, String> generateToken(String username) {
        Utilisateur utilisateur = this.utilisateurService.loadUserByUsername(username);
        disableTokens(utilisateur);
        final Map<String, String> jwtMap = this.generateJwt(utilisateur);

        final Jwt jwt = Jwt.builder()
                .valeur(jwtMap.get("bearer"))
                .desactive(false)
                .expire(false)
                .utilisateur(utilisateur)
                .build();

        this.jwtRepository.save(jwt);
        return jwtMap;
    }

    private Map<String, String> generateJwt(Utilisateur utilisateur) {
        final long currentTime = System.currentTimeMillis();
        final long expirationTime = currentTime + (30 * 60 * 1000);

        final Map<String, Object> claims = Map.of(
                "nom", utilisateur.getNom(),
                Claims.EXPIRATION, new Date(expirationTime),
                Claims.SUBJECT, utilisateur.getEmail()
        );

        String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .setSubject(utilisateur.getEmail())
                .setClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
        return Map.of("bearer", bearer);
    }

    private Key getKey(){
        final byte[] decoder = Decoders.BASE64.decode(ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }

    private void disableTokens(Utilisateur utilisateur) {
        final List<Jwt> jwtList = this.jwtRepository.findUtilisateur(utilisateur.getEmail()).peek(
                jwt -> {
                    jwt.setDesactive(true);
                    jwt.setExpire(true);
                }
        ).collect(Collectors.toList());

        this.jwtRepository.saveAll(jwtList);
    }

    public String extractUsername(String token) {
        return this.getClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = this.getClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    private <T> T getClaim(String token, Function<Claims, T> function) {
        Claims claims = getAllClaims(token);
        return function.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(this.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void deconnexion() {
        Utilisateur utilisateur = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //disableTokens(utilisateur);
        Jwt jwt = this.jwtRepository.findUtilisateurValidToken(utilisateur.getEmail(), false, false)
                .orElseThrow(() -> new RuntimeException("Token invalide"));
        jwt.setDesactive(true);
        jwt.setExpire(true);
        this.jwtRepository.save(jwt);
    }

    @Scheduled(cron = "0 */1 * * * *")   // Suppression des tokens inutiles chaque minute
    //@Scheduled(cron = "@daily")        // Suppression des tokens inutiles chaque jour
    public void removeUselessJwt() {
        log.info("Suppression des token a {}", Instant.now());
        this.jwtRepository.deleteAllByExpireAndDesactive(true, true);
    }

}
