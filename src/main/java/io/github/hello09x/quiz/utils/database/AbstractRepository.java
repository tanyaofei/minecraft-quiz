package io.github.hello09x.quiz.utils.database;

import io.github.hello09x.quiz.Quiz;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Stream;

public abstract class AbstractRepository<T> {

    private final static String DATABASE_FILE = new File(Quiz.getInstance().getDataFolder(), "data.db").getPath();

    private final static Logger log = Quiz.getInstance().getLogger();
    private final static Connection connection;

    static {
        log.info("正在初始化数据库");
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final String tableName;

    private final String idColumnName;

    public AbstractRepository() {
        try {
            this.createTableIfNotExists();
        } catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
        this.tableName = parseTableName();
        this.idColumnName = parseIdColumnName();
    }

    public static void closeConnections() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {

        }
    }

    protected void createTableIfNotExists() throws SQLException {

    }

    protected Connection getConnection() {
        return connection;
    }

    @Nullable
    protected abstract T mapOne(@NotNull ResultSet rs);

    @NotNull
    protected List<T> mapMany(@NotNull ResultSet rs) {
        var entities = new ArrayList<T>();
        T entity;
        while ((entity = mapOne(rs)) != null) {
            entities.add(entity);
        }
        return entities;
    }


    @NotNull
    private String parseTableName() {
        try {
            var clazz = this.getClass().getDeclaredMethod("mapOne", ResultSet.class).getReturnType();
            var table = clazz.getAnnotation(Table.class);
            if (table == null) {
                throw new IllegalArgumentException(clazz + " missing @Table annotation");
            }
            var tableName = table.value();
            if (tableName.isBlank()) {
                throw new IllegalArgumentException(clazz + " has a blank @Table.value()");
            }
            return tableName;
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private String parseIdColumnName() {
        try {
            var clazz = this.getClass().getDeclaredMethod("mapOne", ResultSet.class).getReturnType();
            var id = Stream
                    .concat(Stream.of(clazz.getRecordComponents()), Stream.of(clazz.getDeclaredFields()))
                    .map(e -> e.getAnnotation(Id.class))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .map(Id::value)
                    .orElseThrow(() -> new IllegalArgumentException(clazz + " missing @Id annotation"));

            if (id.isBlank()) {
                throw new IllegalArgumentException(clazz + " has a blank @Id.value()");
            }
            return id;
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Nullable
    public T selectById(@NotNull Serializable id) {
        var sql = "SELECT * FROM " + tableName + " WHERE " + idColumnName + " = ?";
        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, id);
            return mapOne(stm.executeQuery());
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Nullable
    public T selectRandomly() {
        var sql = "SELECT * FROM " + tableName + " ORDER BY random() LIMIT 1";
        try (var stm = getConnection().prepareStatement(sql)) {
            var rs = stm.executeQuery();
            return mapOne(rs);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public int deleteById(@NotNull Serializable id) {
        var sql = "DELETE FROM " + tableName + " WHERE " + idColumnName + " = ?";
        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, id);
            return stm.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public int count() {
        var sql = "SELECT count(*) FROM " + tableName;
        try (var stm = getConnection().prepareStatement(sql)) {
            var r = stm.executeQuery();
            return r.getInt(1);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public Page<T> selectPage(
            int current,
            int size
    ) {
        if (current < 1 || size < 1) {
            return Page.emptyPage();
        }
        var total = count();
        if (total == 0) {
            return Page.emptyPage();
        }

        var offset = (current - 1) * size;
        var sql = "SELECT * FROM " + tableName + " LIMIT ?, ? ";
        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, offset);
            stm.setObject(2, size);
            return new Page<>(
                    mapMany(stm.executeQuery()),
                    total,
                    (int) Math.ceil((double) total / (double) size),
                    current,
                    size
            );
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }


}
