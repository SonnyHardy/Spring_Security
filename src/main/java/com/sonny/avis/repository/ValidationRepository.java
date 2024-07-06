package com.sonny.avis.repository;

import com.sonny.avis.entite.Validation;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface ValidationRepository extends CrudRepository<Validation, Integer> {

    Optional<Validation> findByCode(String code);
}
