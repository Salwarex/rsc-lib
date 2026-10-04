package ru.vit4liy.rsc.repository;

import ru.vit4liy.rsc.DatabaseManager;
import ru.vit4liy.rsc.entity.IntersectionEntity;
import ru.vit4liy.rsc.misc.MultiId;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class IntersectionRepository <T extends IntersectionEntity> extends Repository<T> {
    public IntersectionRepository(Class<T> clazz, DatabaseManager databaseManager) {
        super(clazz, databaseManager);
    }

    public MultiId<Long> create(T entity) throws SQLException {
        String sql = sqlInsert();
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            statementPrepare(stmt, entity);
            stmt.executeUpdate();
            return new MultiId<>(entity.getAId(), entity.getBId());
        }
    }

    public Optional<T> findById(MultiId<Long> ids) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM %s WHERE %s = ? AND %s = ?".formatted(sqlTableName(), sqlTableAttributes()[0], sqlTableAttributes()[2]))) {
            stmt.setLong(1, ids.getByIdx(0));
            stmt.setLong(2, ids.getByIdx(1));
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
        }
        return Optional.empty();
    }

    private List<T> findAllById(boolean first, long id) throws SQLException{
        List<T> result = new ArrayList<>();
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM %s WHERE %s = ?".formatted(sqlTableName(), sqlTableAttributes()[first ? 0 : 2]))) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) result.add(map(rs));
            }
        }
        return result;
    }

    public List<T> findAllByIdA(long id) throws SQLException {
        return findAllById(true, id);
    }

    public List<T> findAllByIdB(long id) throws SQLException {
        return findAllById(false, id);
    }


    public boolean delete(MultiId<Long> ids) throws SQLException {
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM %s WHERE %s = ? AND %s = ?".formatted(sqlTableName(), sqlTableAttributes()[0], sqlTableAttributes()[2]))) {
            stmt.setLong(1, ids.getByIdx(0));
            stmt.setLong(2, ids.getByIdx(1));
            return stmt.executeUpdate() > 0;
        }
    }

    protected String sqlUpdate(){
        String[] attributes = sqlTableAttributes();
        StringBuilder lineAttributesBuilder = new StringBuilder();

        for (int i = 4; i < attributes.length; i += 2) {
            lineAttributesBuilder.append(attributes[i]).append(" = ?, ");
        }

        String lineAttributes = lineAttributesBuilder.toString();
        if (lineAttributes.length() > 2) lineAttributes = lineAttributes.substring(0, lineAttributes.length() - 2);

        return "UPDATE %s SET %s WHERE %s = ? AND %s = ?".formatted(
                sqlTableName(), lineAttributes, attributes[0], attributes[2]);
    }

    @Override
    protected String sqlInsert() {
        String[] attributes = sqlTableAttributes();
        StringBuilder lineAttributesBuilder = new StringBuilder();
        StringBuilder linePlaceholdersBuilder = new StringBuilder();

        for (int i = 0; i < attributes.length; i += 2) {
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
