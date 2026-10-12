package ru.vit4liy.rsc.service;

import ru.vit4liy.perm.Permissible;
import ru.vit4liy.rsc.entity.DatabaseEntity;

import java.util.List;
import java.util.Optional;

public interface GenericDatabaseBusService<T extends DatabaseEntity> extends GenericBusService {
    long create(Permissible executor, T entity) throws Exception;
    Optional<T> getById(Permissible executor, long id) throws Exception;
    boolean update(Permissible executor, T entity) throws Exception;
    boolean delete(Permissible executor, long id) throws Exception;
    List<T> getAll(Permissible executor) throws Exception;
}
