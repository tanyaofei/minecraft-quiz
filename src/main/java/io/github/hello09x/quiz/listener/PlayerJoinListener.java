package io.github.hello09x.quiz.listener;

import io.github.hello09x.quiz.manager.QuizManager;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static net.kyori.adventure.text.format.NamedTextColor.DARK_GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class PlayerJoinListener implements Listener {

    private final static QuizManager manager = QuizManager.instance;

    public final static PlayerJoinListener instance = new PlayerJoinListener();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        var asking = manager.getCurrent();
        if (asking != null) {
            var player = event.getPlayer();
            asking.getProgress().getBossBar().addPlayer(player);
            player.sendMessage(Component.textOfChildren(
                    Component.text("[quiz] 正在提问，快回答！题目是: ", YELLOW),
                    Component.text(asking.getQuestion().title(), DARK_GREEN))
            );
        }
    }

}
