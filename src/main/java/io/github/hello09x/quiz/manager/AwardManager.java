package io.github.hello09x.quiz.manager;

import io.github.hello09x.quiz.repository.model.Award;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AwardManager {

    public final static AwardManager instance = new AwardManager();

    @NotNull
    public List<String> issue(@NotNull Award award, @NotNull Player player) {
        var commands = award.getCommandLines(player);
        var server = player.getServer();
        for (var command : commands) {
            try {
                server.dispatchCommand(server.getConsoleSender(), command);
            } catch (Exception e) {
                player.sendMessage(Component.text("发放奖励时发生错误，先欠着下次一定"));
            }
        }
        return commands;
    }

}
