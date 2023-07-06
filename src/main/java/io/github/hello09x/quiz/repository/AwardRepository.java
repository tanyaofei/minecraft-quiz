package io.github.hello09x.quiz.repository;

import io.github.hello09x.quiz.Quiz;
import io.github.hello09x.quiz.repository.model.Award;
import io.github.tanyaofei.plugin.toolkit.database.AbstractRepository;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class AwardRepository extends AbstractRepository<Award> {

    public final static AwardRepository instance = new AwardRepository(Quiz.getInstance());

    public AwardRepository(Plugin plugin) {
        super(plugin);
    }

    public int insert(@NotNull Award award) {
        try (var stm = getConnection().prepareStatement("INSERT INTO award(commands) values (?)")) {
            stm.setObject(1, award.commands());
            return stm.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected void initTables() throws SQLException {
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
