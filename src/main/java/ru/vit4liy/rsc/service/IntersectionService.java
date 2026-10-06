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

public abstract class IntersectionService<T extends IntersectionEntity> extends Service<T> implements GenericIntersectionBusService<T>{
    public IntersectionService(IntersectionRepository<T> intersectionRepository, PermissionService permissionService) {
        super(intersectionRepository, permissionService);
    }

    public MultiId<Long> create(T entity) throws SQLException, MissingPermissionException {
        IntersectionRepository<T> intersectionRepository = (IntersectionRepository<T>) repository;
        return intersectionRepository.create(entity);
    }

    public Optional<T> getById(MultiId<Long> id) throws SQLException, MissingPermissionException{
        IntersectionRepository<T> intersectionRepository = (IntersectionRepository<T>) repository;
        return intersectionRepository.findById(id);
    }

    public List<T> findAllByIdA(long id) throws SQLException, MissingPermissionException{
        IntersectionRepository<T> intersectionRepository = (IntersectionRepository<T>) repository;
        return intersectionRepository.findAllByIdA(id);
    }

    public List<T> findAllByIdB(long id) throws SQLException, MissingPermissionException{
        IntersectionRepository<T> intersectionRepository = (IntersectionRepository<T>) repository;
        return intersectionRepository.findAllByIdB(id);
    }

    public boolean update(T entity) throws SQLException, MissingPermissionException{
        IntersectionRepository<T> intersectionRepository = (IntersectionRepository<T>) repository;
        return intersectionRepository.update(entity);
    }

    public boolean delete(MultiId<Long> id) throws SQLException, MissingPermissionException{
        IntersectionRepository<T> intersectionRepository = (IntersectionRepository<T>) repository;
        return intersectionRepository.delete(id);
    }
}

