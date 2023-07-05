package io.github.hello09x.quiz.repository;

import io.github.hello09x.quiz.repository.model.Statistics;
import io.github.hello09x.quiz.utils.database.AbstractRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;

public class StatisticsRepository extends AbstractRepository<Statistics> {

    public final static StatisticsRepository instance = new StatisticsRepository();

    public void addCorrect(
            @NotNull String playerId,
            int delta
    ) {
    }

    @Nullable
    @Override
    protected Statistics mapOne(@NotNull ResultSet rs) {
        return null;
    }
}
