package ru.vit4liy.rsc.service;

import ru.vit4liy.rsc.entity.Entity;

import java.util.List;

public interface GenericBusService<T extends Entity>{
    List<T> getAll() throws Exception;
}
