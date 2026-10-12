package ru.vit4liy.rsc.service;

import ru.vit4liy.perm.Permissible;
import ru.vit4liy.rsc.entity.IntersectionEntity;
import ru.vit4liy.rsc.misc.MultiId;

import java.util.List;
import java.util.Optional;

public interface GenericIntersectionBusService<T extends IntersectionEntity> extends GenericBusService {
    MultiId<Long> create(Permissible executor, T entity) throws Exception;
    Optional<T> getById(Permissible executor, MultiId<Long> id) throws Exception;
    List<T> findAllByIdA(Permissible executor, long id) throws Exception;
    List<T> findAllByIdB(Permissible executor, long id) throws Exception;
    boolean update(Permissible executor, T entity) throws Exception;
    boolean delete(Permissible executor, MultiId<Long> id) throws Exception;
    List<T> getAll(Permissible executor) throws Exception;
}
