package com.gcalsolaro.statemachine.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gcalsolaro.statemachine.domain.entity.Instance;

public interface InstanceRepository extends PagingAndSortingRepository<Instance, Integer> {

}
