package ru.vit4liy.rsc.service;

import ru.vit4liy.rsc.entity.IntersectionEntity;
import ru.vit4liy.rsc.misc.MultiId;

import java.util.List;
import java.util.Optional;

public interface GenericIntersectionBusService<T extends IntersectionEntity> extends GenericBusService<T> {
    MultiId<Long> create(T entity) throws Exception;
    Optional<T> getById(MultiId<Long> id) throws Exception;
    List<T> findAllByIdA(long id) throws Exception;
    List<T> findAllByIdB(long id) throws Exception;
    boolean update(T entity) throws Exception;
    boolean delete(MultiId<Long> id) throws Exception;
}
