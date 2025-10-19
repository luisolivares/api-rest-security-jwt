package com.olivares.api_rest_security_jwt.repository;

import java.util.List;
import java.util.Optional;

public interface CRUDService<T> {

    List<T> listAll(int page, int size);

    Optional<T> getById(Long id);

    Optional<T> saveOrUpdate(T domainObject);

    void delete(Long id);
}
