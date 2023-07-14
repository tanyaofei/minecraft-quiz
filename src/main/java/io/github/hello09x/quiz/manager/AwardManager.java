package io.github.hello09x.quiz.manager;

import com.google.common.base.Throwables;
import io.github.hello09x.quiz.Quiz;
import io.github.hello09x.quiz.repository.model.Award;
import net.kyori.adventure.text.format.Style;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

public class AwardManager {

    public final static AwardManager instance = new AwardManager();

    private final static Logger log = Quiz.getInstance().getLogger();

    @NotNull
    public List<String> issue(@NotNull Award award, @NotNull Player player) {
        var server = player.getServer();
        var commands = award.getCommandLines(player);
        for (var command : commands) {
            try {
                server.dispatchCommand(server.getConsoleSender(), command);
            } catch (Exception e) {
                player.sendMessage(text("发放奖励时发生错误，先欠着下次一定"));
                log.warning(Throwables.getStackTraceAsString(e));
            }
            player.sendMessage(text("[quiz] 奖励已偷偷给你了, 不要告诉别人哦").style(Style.style(GRAY, ITALIC)));
        }
        return commands;
    }

}
