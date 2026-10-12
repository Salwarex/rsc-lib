package ru.vit4liy.rsc.service;

import ru.vit4liy.perm.MissingPermissionException;
import ru.vit4liy.perm.Permissible;
import ru.vit4liy.perm.PermissionService;
import ru.vit4liy.rsc.entity.Entity;
import ru.vit4liy.rsc.repository.Repository;

import java.sql.SQLException;
import java.util.List;

public abstract class Service implements GenericBusService{
    protected final PermissionService permissionService;

    public Service(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    protected void requirePermissions(Permissible permissible, String permission) throws MissingPermissionException {
        if(permissionService == null) return;
        permissionService.checkPermissions(permissible, permission);
    }
}
