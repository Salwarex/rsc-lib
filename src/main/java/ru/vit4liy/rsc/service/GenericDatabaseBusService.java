package ru.vit4liy.rsc.service;

import ru.vit4liy.perm.Permissible;
import ru.vit4liy.rsc.entity.Entity;

import java.util.List;

public interface GenericDatabaseBusService <T extends Entity> extends GenericBusService{
    List<T> getAll(Permissible executor) throws Exception;
}
