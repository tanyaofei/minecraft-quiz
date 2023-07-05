package io.github.hello09x.quiz.support;

import io.github.hello09x.quiz.Quiz;
import io.github.hello09x.quiz.repository.StatisticRepository;
import io.github.hello09x.quiz.repository.model.Statistic;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class StatisticPlaceholder extends PlaceholderExpansion {

    private final StatisticRepository repository = StatisticRepository.instance;

    private final static String VERSION = "1";


    @Override
    public @NotNull String getIdentifier() {
        return "quiz";
    }

    @Override
    public @NotNull String getAuthor() {
        return "hello09x";
    }

    @Override
    public @NotNull String getVersion() {
        return VERSION;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (!params.startsWith("corrects.top.")) {
            return params;
        }

        var n = params.substring("corrects.top.".length()).trim();
        if (n.isBlank()) {
            return params;
        }

        int ordinal;
        try {
            ordinal = Integer.parseInt(n);
        } catch (NumberFormatException e) {
            return params;
        }

        return Optional
                .ofNullable(repository.selectTop(ordinal))
                .map(Statistic::playerName)
                .orElse("");
    }
}
