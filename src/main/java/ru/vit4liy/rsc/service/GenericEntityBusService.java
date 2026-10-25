package ru.vit4liy.rsc.service;

import ru.vit4liy.perm.Permissible;
import ru.vit4liy.rsc.entity.DatabaseEntity;

import java.util.Optional;

public interface GenericEntityBusService<T extends DatabaseEntity> extends GenericDatabaseBusService<T> {
    long create(Permissible executor, T entity) throws Exception;
    Optional<T> getById(Permissible executor, long id) throws Exception;
    boolean update(Permissible executor, T entity) throws Exception;
    boolean delete(Permissible executor, long id) throws Exception;
}
