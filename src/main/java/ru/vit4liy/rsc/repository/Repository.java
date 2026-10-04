package ru.vit4liy.rsc.repository;

import ru.vit4liy.rsc.DatabaseManager;
import ru.vit4liy.rsc.entity.Entity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Repository<T extends Entity> {
    protected final DatabaseManager db;
    protected final Class<T> clazz;

    public Repository(Class<T> clazz, DatabaseManager databaseManager) {
        this.clazz = clazz;
        this.db = databaseManager;
        db.addCreationQuery(getCreateIfNotExistsStatement());
        db.addIndexQueries(getCreateIndexesStatements());
    }

    protected String sqlTableName(){
        return clazz.getSimpleName();
    }

    protected abstract String[] sqlTableAttributes();
    protected abstract T map(ResultSet rs) throws SQLException;
    protected abstract void statementPrepare(PreparedStatement stmt, T entity) throws SQLException;

    protected abstract String sqlInsert();

    protected abstract String sqlUpdate();

    public List<T> findAll() throws SQLException {
        List<T> list = new ArrayList<>();
        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM %s".formatted(sqlTableName()))) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean update(T entity) throws SQLException {
        String sql = sqlUpdate();
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            statementPrepare(stmt, entity);
            return stmt.executeUpdate() > 0;
        }
    }

    protected String getCreateIfNotExistsStatement(){
        String[] attributes = sqlTableAttributes();
        StringBuilder allConditionsBuilder = new StringBuilder();

        for (int i = 0; i < attributes.length - 1; i += 2) {
            String attribute = attributes[i];
            String conditions = attributes[i+1];
            allConditionsBuilder.append(attribute).append(" ").append(conditions).append(", ");
        }

        String[] multiPrimaryKeys = getMultiPrimaryQueries();
        if (multiPrimaryKeys != null && multiPrimaryKeys.length > 0) {
            for (String pk : multiPrimaryKeys) {
                allConditionsBuilder.append(pk).append(", ");
            }
        }

        String[] foreignKeys = getForeignKeysQueries();
        if (foreignKeys != null && foreignKeys.length > 0) {
            for (String fk : foreignKeys) {
                allConditionsBuilder.append(fk).append(", ");
            }
        }

        String allConditions = allConditionsBuilder.toString();
        if (allConditions.endsWith(", ")) {
            allConditions = allConditions.substring(0, allConditions.length() - 2);
        }

        return "CREATE TABLE IF NOT EXISTS %s (%s)".formatted(sqlTableName(), allConditions);
    }

    protected String[] getMultiPrimaryQueries(){
        return new String[0];
    }

    protected abstract String[] getForeignKeysQueries();

    protected abstract String[] getCreateIndexesStatements();
}
