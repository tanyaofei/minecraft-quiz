package io.github.hello09x.quiz.listener;

import io.github.hello09x.quiz.manager.QuizManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerChatListener implements Listener {

    public static PlayerChatListener instance = new PlayerChatListener();

    @EventHandler(ignoreCancelled = true)
    public void onAsyncChatEvent(AsyncChatEvent event) {
        var asking = QuizManager.instance.getCurrent();
        if (asking == null) {
            return;
        }

        if (event.message() instanceof TextComponent answer) {
            QuizManager.instance.answer(event.getPlayer(), asking, answer.content());
        }
    }

}
