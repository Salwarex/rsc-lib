package ru.vit4liy.rsc.service;

import ru.vit4liy.rsc.entity.DatabaseEntity;

import java.util.Optional;

public interface GenericDatabaseBusService<T extends DatabaseEntity> extends GenericBusService<T> {
    long create(T entity) throws Exception;
    Optional<T> getById(long id) throws Exception;
    boolean update(T entity) throws Exception;
    boolean delete(long id) throws Exception;
}
