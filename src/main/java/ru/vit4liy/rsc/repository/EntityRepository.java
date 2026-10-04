package ru.vit4liy.rsc.repository;

import ru.vit4liy.rsc.entity.DatabaseEntity;
import ru.vit4liy.rsc.DatabaseManager;
import ru.vit4liy.rsc.entity.Entity;
import ru.vit4liy.rsc.entity.IntersectionEntity;

import java.sql.*;
import java.util.Optional;

public abstract class EntityRepository<T extends DatabaseEntity> extends Repository<T> {
    public EntityRepository(Class<T> clazz, DatabaseManager databaseManager) {
        super(clazz, databaseManager);
    }

    public long create(T entity) throws SQLException {
        String sql = sqlInsert();
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statementPrepare(stmt, entity);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1)); return entity.getId();
                }
            }
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

    public boolean delete(long id) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM %s WHERE %s = ?".formatted(sqlTableName(), sqlTableAttributes()[0]))) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    protected String sqlUpdate(){
        String[] attributes = sqlTableAttributes();
        StringBuilder lineAttributesBuilder = new StringBuilder();

        for (int i = 2; i < attributes.length; i += 2) {
            lineAttributesBuilder.append(attributes[i]).append(" = ?, ");
        }

        String lineAttributes = lineAttributesBuilder.toString();
        if (lineAttributes.length() > 2) lineAttributes = lineAttributes.substring(0, lineAttributes.length() - 2);

        return "UPDATE %s SET %s WHERE %s = ?".formatted(sqlTableName(), lineAttributes, attributes[0]);
    }

    protected String sqlInsert(){
        String[] attributes = sqlTableAttributes();
        StringBuilder lineAttributesBuilder = new StringBuilder();
        StringBuilder linePlaceholdersBuilder = new StringBuilder();

        for (int i = 2; i < attributes.length; i += 2) {
            lineAttributesBuilder.append(attributes[i]).append(", ");
            linePlaceholdersBuilder.append("?, ");
        }

        String lineAttributes = lineAttributesBuilder.toString();
        String linePlaceholders = linePlaceholdersBuilder.toString();

        if (lineAttributes.length() > 2) lineAttributes = lineAttributes.substring(0, lineAttributes.length() - 2);
        if (linePlaceholders.length() > 2) linePlaceholders = linePlaceholders.substring(0, linePlaceholders.length() - 2);

        return "INSERT INTO %s (%s) VALUES (%s)".formatted(sqlTableName(), lineAttributes, linePlaceholders);
    }
}
