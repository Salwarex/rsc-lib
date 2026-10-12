package ru.vit4liy.rsc.service;

import ru.vit4liy.perm.PermissionService;
import ru.vit4liy.rsc.entity.Entity;
import ru.vit4liy.rsc.repository.Repository;

public abstract class RepositoryService<T extends Entity> extends Service{
    protected final Repository<T> repository;

    public RepositoryService(Repository<T> repository, PermissionService permissionService) {
        super(permissionService);
        this.repository = repository;
    }
}
