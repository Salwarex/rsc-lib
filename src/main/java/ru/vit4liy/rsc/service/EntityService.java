package ru.vit4liy.rsc.service;

import ru.vit4liy.perm.MissingPermissionException;
import ru.vit4liy.perm.Permissible;
import ru.vit4liy.perm.PermissionService;
import ru.vit4liy.rsc.entity.DatabaseEntity;
import ru.vit4liy.rsc.repository.EntityRepository;
import ru.vit4liy.rsc.repository.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public abstract class EntityService<T extends DatabaseEntity> extends Service<T> implements GenericDatabaseBusService<T> {
    public EntityService(EntityRepository<T> entityRepository, PermissionService permissionService) {
        super(entityRepository, permissionService);
    }

    public long create(T entity) throws SQLException, MissingPermissionException{
        EntityRepository<T> entityRepository = (EntityRepository<T>) repository;
        return entityRepository.create(entity);
    }

    public Optional<T> getById(long id) throws SQLException, MissingPermissionException{
        EntityRepository<T> entityRepository = (EntityRepository<T>) repository;
        return entityRepository.findById(id);
    }

    public boolean update(T entity) throws SQLException, MissingPermissionException{
        EntityRepository<T> entityRepository = (EntityRepository<T>) repository;
        return entityRepository.update(entity);
    }

    public boolean delete(long id) throws SQLException, MissingPermissionException{
        EntityRepository<T> entityRepository = (EntityRepository<T>) repository;
        return entityRepository.delete(id);
    }
}
