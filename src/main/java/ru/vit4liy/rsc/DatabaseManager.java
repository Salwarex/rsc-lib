package ru.vit4liy.rsc;

import ru.vit4liy.it72h.lib.logger.log.Logger;
import ru.vit4liy.it72h.lib.logger.output.Output;
import ru.vit4liy.it72h.lib.logger.output.OutputPreset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseManager {
    private final ThreadLocal<Connection> threadConnection = new ThreadLocal<>();

    private final List<String> createQueries;
    private final List<String> indexesQueries;
    
    private final boolean isMySQL;

    private String jdbcUrl;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    private final Logger logger;

    public DatabaseManager(Logger logger, boolean isMySQL, String host, int port, String database, String username, String password) {
        this.logger = logger;

        createQueries = new ArrayList<>();
        indexesQueries = new ArrayList<>();

        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.isMySQL = isMySQL;
    }

    public void addCreationQuery(String query){
        createQueries.add(query);
    }

    public void addIndexQueries(String ... query){
        indexesQueries.addAll(List.of(query));
    }

    public void setIndexes(String ... indexesQueries){
        this.indexesQueries.addAll(Arrays.asList(indexesQueries));
    }

    public void startUp(){
        prepareConfig();
        createTables();
    }

    private void prepareConfig() {
        if (isMySQL) {
            this.jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database
                    + "?useSSL=false"
                    + "&autoReconnect=true"
                    + "&connectTimeout=10000"
                    + "&socketTimeout=60000"
                    + "&characterEncoding=UTF-8";

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                logger.out(Output.of(OutputPreset.DEFAULT, "MySQL драйвер успешно загружен."));
            } catch (ClassNotFoundException e) {
                logger.out(Output.of(OutputPreset.ERROR, "MySQL драйвер не найден! " + e.getMessage()));
            }
        } else {
            Path appDataDir = Paths.get(System.getProperty("user.dir"));
            try {
                Files.createDirectories(appDataDir);
            } catch (IOException e) {
                logger.out(Output.of(OutputPreset.ERROR, "Не удалось создать папку для данных! " + e.getMessage()));
                return;
            }

            File dbFile = appDataDir.resolve("%s.db".formatted(this.database)).toFile();
            this.jdbcUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

            try {
                Class.forName("org.sqlite.JDBC");
                logger.out(Output.of(OutputPreset.DEFAULT, "SQLite драйвер успешно загружен."));
            } catch (ClassNotFoundException e) {
                logger.out(Output.of(OutputPreset.ERROR, "SQLite драйвер не обнаружен! " + e.getMessage()));
            }
        }
    }

    private Connection createNewConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
        if (!isMySQL) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
        }
        return conn;
    }

    private void createTables() {
        String pk = isMySQL ? "BIGINT PRIMARY KEY AUTO_INCREMENT" : "INTEGER PRIMARY KEY AUTOINCREMENT";
        String bigint = isMySQL ? "BIGINT" : "INTEGER";
        String intType = isMySQL ? "INT" : "INTEGER";
        String boolType = isMySQL ? "BOOLEAN" : "INTEGER";

        try (Connection initConn = createNewConnection();
             Statement stmt = initConn.createStatement()) {
            for (String query : createQueries) {
                //preprocess
                query = query.replace("{primary}", pk)
                                .replace("{long}", bigint)
                                        .replace("{int}", intType)
                                                .replace("{bool}", boolType);

                stmt.execute(query);
            }
            logger.out(Output.of(OutputPreset.DEFAULT, "Таблицы БД созданы/проверены."));
        } catch (SQLException e) {
            logger.out(Output.of(OutputPreset.ERROR, "Ошибка создания таблиц! " + e.getMessage()));
            return;
        }

        try (Connection initConn = createNewConnection()) {
            for (String query : indexesQueries) {
                try (Statement stmt = initConn.createStatement()) {
                    stmt.execute(query);
                } catch (SQLException ignored) {}
            }
        } catch (SQLException e) {
            logger.out(Output.of(OutputPreset.WARN, "Ошибка создания индексов: " + e.getMessage()));
        }
    }

    public Connection getConnection() throws SQLException {
        Connection conn = threadConnection.get();

        if (conn == null || conn.isClosed() || !isConnectionValid(conn)) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {}
            }
            conn = createNewConnection();
            threadConnection.set(conn);
        }
        return conn;
    }

    private boolean isConnectionValid(Connection conn) {
        try {
            return conn != null && conn.isValid(3);
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean isMySQL() {
        return isMySQL;
    }

    public void closeCurrentThreadConnection() {
        Connection conn = threadConnection.get();
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.out(Output.of(OutputPreset.ERROR, "Ошибка закрытия connection: " + e.getMessage()));
            } finally {
                threadConnection.remove();
            }
        }
    }

    public void close() {
        closeCurrentThreadConnection();
    }
}