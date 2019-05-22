package com.revolut.task.api.services;

import com.revolut.task.api.dto.Dto;

import java.util.Optional;

public interface GenericService<T extends Dto> {

    Optional<T> getById(Integer id);

    Integer create(T dto);
}
