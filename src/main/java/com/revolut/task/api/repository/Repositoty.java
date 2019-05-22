package com.revolut.task.api.repository;

import java.util.Optional;

public interface Repositoty<T> {
    Optional<T> getById(Integer id);

    Integer create(T pojo);
}
