package com.gcalsolaro.statemachine.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gcalsolaro.statemachine.domain.entity.User;

public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
}
