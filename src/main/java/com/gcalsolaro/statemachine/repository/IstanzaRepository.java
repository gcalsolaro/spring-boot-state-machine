package com.gcalsolaro.statemachine.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gcalsolaro.statemachine.domain.entity.Istanza;

public interface IstanzaRepository extends PagingAndSortingRepository<Istanza, Integer> {

}
