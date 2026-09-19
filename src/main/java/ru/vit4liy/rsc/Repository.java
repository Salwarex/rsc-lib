package ru.vit4liy.rsc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Repository<T extends DatabaseEntity> {
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
    protected abstract T map(ResultSet rs) throws SQLException ;
    protected abstract void statementPrepare(PreparedStatement stmt, T entity) throws SQLException;


    private String sqlInsert(){
        String[] attributes = sqlTableAttributes();
        StringBuilder lineAttributesBuilder = new StringBuilder();
        StringBuilder linePlaceholdersBuilder = new StringBuilder();
        String lineAttributes;
        String linePlaceholders;

        int i = 0;
        for(String attribute : attributes){
            if(i % 2 == 1) continue;
            lineAttributesBuilder.append(attribute).append(", ");
            linePlaceholdersBuilder.append("?, ");
            i++;
        }
        lineAttributes = lineAttributesBuilder.toString();
        linePlaceholders = linePlaceholdersBuilder.toString();

        if (lineAttributes.length() > 2) lineAttributes = lineAttributes.substring(0, lineAttributes.length() - 2);
        if (linePlaceholders.length() > 2) linePlaceholders = linePlaceholders.substring(0, linePlaceholders.length() - 2);

        return "INSERT INTO %s (%s) VALUES (%s)".formatted(sqlTableName(), lineAttributes, linePlaceholders);
    }

    private String sqlUpdate(){
        String[] attributes = sqlTableAttributes();
        StringBuilder lineAttributesBuilder = new StringBuilder();
        String lineAttributes;

        int i = 0;
        for(String attribute : attributes){
            if (i % 2 == 1) continue;
            lineAttributesBuilder.append(attribute).append(" = ?, ");
            i++;
        }
        lineAttributes = lineAttributesBuilder.toString();
        if (lineAttributes.length() > 2) lineAttributes = lineAttributes.substring(0, lineAttributes.length() - 2);

        return "UPDATE %s SET %s WHERE %s = ?".formatted(sqlTableName(), lineAttributes, sqlTableAttributes()[0]);
    }

    public long create(T entity) throws SQLException {
        String sql = sqlInsert();
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statementPrepare(stmt, entity);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) { if (rs.next()) { entity.setId(rs.getLong(1)); return entity.getId(); } }
        }
        return -1;
    }

    public Optional<T> findById(long id) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM %s WHERE %s = ?".formatted(sqlTableName(), sqlTableAttributes()[0]))) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

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

    public boolean delete(long id) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM %s WHERE %s = ?".formatted(sqlTableName(), sqlTableAttributes()[0]))) {
            stmt.setLong(1, id);
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

    protected abstract String[] getForeignKeysQueries();

    protected abstract String[] getCreateIndexesStatements();
}
