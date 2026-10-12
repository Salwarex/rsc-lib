package ru.vit4liy.rsc.service;

import ru.vit4liy.perm.MissingPermissionException;
import ru.vit4liy.perm.Permissible;
import ru.vit4liy.perm.PermissionService;
import ru.vit4liy.rsc.entity.IntersectionEntity;
import ru.vit4liy.rsc.misc.MultiId;
import ru.vit4liy.rsc.repository.EntityRepository;
import ru.vit4liy.rsc.repository.IntersectionRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public abstract class IntersectionService<T extends IntersectionEntity> extends RepositoryService<T> implements GenericIntersectionBusService<T>{
    public IntersectionService(IntersectionRepository<T> intersectionRepository, PermissionService permissionService) {
        super(intersectionRepository, permissionService);
    }

    public MultiId<Long> create(Permissible executor, T entity) throws SQLException, MissingPermissionException {
        IntersectionRepository<T> intersectionRepository = (IntersectionRepository<T>) repository;
        return intersectionRepository.create(entity);
    }

    public Optional<T> getById(Permissible executor, MultiId<Long> id) throws SQLException, MissingPermissionException{
        IntersectionRepository<T> intersectionRepository = (IntersectionRepository<T>) repository;
        return intersectionRepository.findById(id);
    }

    public List<T> findAllByIdA(Permissible executor, long id) throws SQLException, MissingPermissionException{
        IntersectionRepository<T> intersectionRepository = (IntersectionRepository<T>) repository;
        return intersectionRepository.findAllByIdA(id);
    }

    public List<T> findAllByIdB(Permissible executor, long id) throws SQLException, MissingPermissionException{
        IntersectionRepository<T> intersectionRepository = (IntersectionRepository<T>) repository;
        return intersectionRepository.findAllByIdB(id);
    }

    public boolean update(Permissible executor, T entity) throws SQLException, MissingPermissionException{
        IntersectionRepository<T> intersectionRepository = (IntersectionRepository<T>) repository;
        return intersectionRepository.update(entity);
    }

    public boolean delete(Permissible executor, MultiId<Long> id) throws SQLException, MissingPermissionException{
        IntersectionRepository<T> intersectionRepository = (IntersectionRepository<T>) repository;
        return intersectionRepository.delete(id);
    }

    public List<T> getAll(Permissible executor) throws SQLException, MissingPermissionException {
        return repository.findAll();
    }
}

