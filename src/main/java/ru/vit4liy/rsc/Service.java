package ru.vit4liy.rsc;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public abstract class Service<T extends DatabaseEntity> {
    protected final Repository<T> repository;

    public Service(Repository<T> repository) {
        this.repository = repository;
    }

    public long create(T entity) throws SQLException{
        return repository.create(entity);
    }

    public Optional<T> getById(long id) throws SQLException{
        return repository.findById(id);
    }

    public List<T> getAll() throws SQLException{
        return repository.findAll();
    }

    public boolean update(T entity) throws SQLException{
        return repository.update(entity);
    }

    public boolean delete(long id) throws SQLException {
        return repository.delete(id);
    }
}
