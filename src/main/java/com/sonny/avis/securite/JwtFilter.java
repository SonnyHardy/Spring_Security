package com.sonny.avis.securite;

import com.sonny.avis.entite.Jwt;
import com.sonny.avis.repository.JwtRepository;
import com.sonny.avis.service.UtilisateurService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private UtilisateurService utilisateurService;
    private JwtService jwtService;
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        Jwt tokenDansLaBDD = null;
        String username = null;
        boolean isTokenExpired = true;

        try {

        // Bearer eyJhbGciOiJIUzI1NiJ9.eyJub20iOiJzb25ueSIsImVtYWlsIjoic29ubnlAZ21haWwuY29tIn0.gEkyJ_8kWUa8X3lhQ8ReG8ESCS1t5xsxyEfYuIEJjvA
        final String authorization = request.getHeader("Authorization");
        if(authorization != null && authorization.startsWith("Bearer")){
            token = authorization.substring(7);
            tokenDansLaBDD = this.jwtService.tokenByValue(token);
            isTokenExpired = this.jwtService.isTokenExpired(token);
            username = jwtService.extractUsername(token);
        }

        if(!isTokenExpired && username != null && tokenDansLaBDD.getUtilisateur().getEmail().equals(username)
                && SecurityContextHolder.getContext().getAuthentication() == null){

            UserDetails userDetails = this.utilisateurService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);

        } catch (final Exception exception) {
            this.handlerExceptionResolver.resolveException(request, response, null, exception);
        }

    }


}
