package com.sonny.avis.securite;

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

import java.io.IOException;

@Service
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private UtilisateurService utilisateurService;
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        String username = null;
        boolean isTokenExpired = true;

        // Bearer eyJhbGciOiJIUzI1NiJ9.eyJub20iOiJzb25ueSIsImVtYWlsIjoic29ubnlAZ21haWwuY29tIn0.gEkyJ_8kWUa8X3lhQ8ReG8ESCS1t5xsxyEfYuIEJjvA
        final String authorization = request.getHeader("Authorization");
        if(authorization != null && authorization.startsWith("Bearer")){
            token = authorization.substring(7);
            isTokenExpired = this.jwtService.isTokenExpired(token);
            username = jwtService.extractUsername(token);
        }

        if(!isTokenExpired && username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = this.utilisateurService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);

    }
}
