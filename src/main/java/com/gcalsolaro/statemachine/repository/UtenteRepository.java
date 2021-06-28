package com.gcalsolaro.statemachine.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gcalsolaro.statemachine.domain.entity.Utente;

public interface UtenteRepository extends PagingAndSortingRepository<Utente, Integer> {
}
