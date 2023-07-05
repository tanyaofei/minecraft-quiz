package io.github.hello09x.quiz.repository;

import io.github.hello09x.quiz.Quiz;
import io.github.hello09x.quiz.repository.model.Statistic;
import io.github.hello09x.quiz.utils.database.AbstractRepository;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StatisticRepository extends AbstractRepository<Statistic> {

    public final static StatisticRepository instance = new StatisticRepository();

    public int addCorrect(
            @NotNull Player player,
            int delta
    ) {
        var playerName = player.getName();
        var sql = """
                INSERT OR
                 REPLACE
                 INTO statistic(player_name, corrects)
                 VALUES (?,
                         ifnull((SELECT corrects FROM statistic WHERE player_name = ?), 0) + ?);
                """;
        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, playerName);
            stm.setObject(2, playerName);
            stm.setObject(3, delta);
            return stm.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Nullable
    public Statistic selectTop(int ordinal) {
        if (ordinal < 1) {
            throw new IllegalArgumentException("invalid ordinal: " + ordinal);
        }

        var sql = """
                SELECT * FROM statistic
                ORDER BY corrects
                LIMIT ?, ?
                """;
        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, ordinal - 1);
            stm.setObject(2, 1);
            return mapOne(stm.executeQuery());
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @NotNull
    public List<Statistic> selectTops(int top) {
        if (top < 1) {
            throw new IllegalArgumentException("invalid top: " + top);
        }
        var sql = """
                SELECT * FROM statistic
                ORDER BY corrects
                LIMIT ?
                """;

        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, top);
            return mapMany(stm.executeQuery());
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected void createTableIfNotExists() throws SQLException {
        try (var stm = getConnection().createStatement()) {
            stm.execute("""
                    -- auto-generated definition
                     create table if not exists statistic
                     (
                         player_name text              not null
                             primary key
                             unique,
                         corrects    integer default 0 not null
                     );
                    """);
            stm.execute("""
                    create index if not exists statistic_corrects_index
                        on statistic (corrects desc);
                    """);
        }
    }

    @Nullable
    @Override
    protected Statistic mapOne(@NotNull ResultSet rs) {
        try {
            if (!rs.next()) {
                return null;
            }
            return new Statistic(
                    rs.getString("player_name"),
                    rs.getInt("corrects"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
