package ru.vit4liy.rsc;

import ru.vit4liy.perm.MissingPermissionException;
import ru.vit4liy.perm.Permissible;
import ru.vit4liy.perm.PermissionService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public abstract class Service<T extends DatabaseEntity> {
    protected final Repository<T> repository;
    protected final PermissionService permissionService;

    public Service(Repository<T> repository, PermissionService permissionService) {
        this.repository = repository;
        this.permissionService = permissionService;
    }

    public long create(T entity) throws SQLException, MissingPermissionException{
        return repository.create(entity);
    }

    public Optional<T> getById(long id) throws SQLException, MissingPermissionException{
        return repository.findById(id);
    }

    public List<T> getAll() throws SQLException, MissingPermissionException{
        return repository.findAll();
    }

    public boolean update(T entity) throws SQLException, MissingPermissionException{
        return repository.update(entity);
    }

    public boolean delete(long id) throws SQLException, MissingPermissionException{
        return repository.delete(id);
    }

    public void requirePermissions(Permissible permissible, String permission) throws MissingPermissionException {
        if(permissionService == null) return;
        permissionService.checkPermissions(permissible, permission);
    }
}
