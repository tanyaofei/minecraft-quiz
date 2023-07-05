package io.github.hello09x.quiz.support;

import io.github.hello09x.quiz.repository.StatisticRepository;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class StatisticPlaceholder extends PlaceholderExpansion {

    /**
     * top.1.name
     * top.1.corrects
     * top.2.corrects
     */
    private final static Pattern PATTERN = Pattern.compile(
            "^top\\.(?<ordinal>\\d+)\\.(?<type>name|corrects)$"
    );

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
        var matcher = PATTERN.matcher(params);
        if (!matcher.find()) {
            return params;
        }

        var ordinal = Integer.parseInt(matcher.group("ordinal"));
        var type = matcher.group("type");

        var stat = repository.selectTop(ordinal);
        if (stat == null) {
            return "";
        }

        return switch (type) {
            case "name" -> stat.playerName();
            case "corrects" -> String.valueOf(stat.corrects());
            default -> "Invalid type: " + type;
        };
    }


}
