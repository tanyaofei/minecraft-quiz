package io.github.hello09x.quiz.repository;

import io.github.hello09x.quiz.repository.model.Award;
import io.github.hello09x.quiz.utils.database.AbstractRepository;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AwardRepository extends AbstractRepository<Award> {

    public final static AwardRepository instance = new AwardRepository();

    public int insert(@NotNull Award award) {
        try (var stm = getConnection().prepareStatement("INSERT INTO award(commands) values (?)")) {
            stm.setObject(1, award.commands());
            return stm.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    protected Award mapOne(@NotNull ResultSet rs) {
        try {
            if (!rs.next()) {
                return null;
            }
            return new Award(
                    rs.getInt("id"),
                    rs.getString("commands")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void createTableIfNotExists() throws SQLException {
        try (var stm = getConnection().createStatement()) {
            stm.execute("""
                    create table if not exists award
                    (
                        id       integer not null primary key autoincrement,
                        commands text    not null
                    );
                    """);
        }
    }
}
