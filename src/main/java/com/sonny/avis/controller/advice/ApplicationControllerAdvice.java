package com.sonny.avis.controller.advice;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = BadCredentialsException.class)
    public @ResponseBody ProblemDetail badCredentialsException(BadCredentialsException exception) {
        log.error(exception.getMessage(), exception);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        problemDetail.setProperty("Erreur", "Nous n'avons pas pu vous identifier");
        return problemDetail;
        /*return Map.of("Status", String.valueOf(problemDetail.getStatus()),
                "Detail", problemDetail.getDetail().toString());*/
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = {SignatureException.class, MalformedJwtException.class})
    public @ResponseBody ProblemDetail signatureException(final Exception exception) {
        ApplicationControllerAdvice.log.error(exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Token Invalide");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = ExpiredJwtException.class)
    public @ResponseBody ProblemDetail expiredJwtException(final Exception exception) {
        ApplicationControllerAdvice.log.error(exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Token Expired");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = AccessDeniedException.class)
    public @ResponseBody ProblemDetail accessDeniedException(final Exception exception) {
        ApplicationControllerAdvice.log.error(exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Vous n'avez pas le droit d'effectuer cette action");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = Exception.class)
    public Map<String, String> exceptionsHandler() {
        return Map.of("Erreur", "Description");
    }


}
