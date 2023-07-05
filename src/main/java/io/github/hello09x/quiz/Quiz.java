package io.github.hello09x.quiz;

import io.github.hello09x.quiz.command.AdminCommand;
import io.github.hello09x.quiz.listener.PlayerChatListener;
import io.github.hello09x.quiz.listener.PlayerJoinListener;
import io.github.hello09x.quiz.manager.QuizManager;
import io.github.hello09x.quiz.properties.QuizProperties;
import io.github.hello09x.quiz.utils.database.AbstractRepository;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Quiz extends JavaPlugin {

    private static Quiz instance;

    @Getter
    private QuizProperties properties;

    public static Quiz getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        {
            this.properties = new QuizProperties(this);
        }

        {
            QuizManager.getInstance();
        }

        {
            var server = getServer();
            server.getPluginCommand("quizadmin").setExecutor(AdminCommand.instance);
            server.getPluginManager().registerEvents(PlayerChatListener.instance, this);
            server.getPluginManager().registerEvents(PlayerJoinListener.instance, this);
        }
    }

    @Override
    public void onDisable() {
        AbstractRepository.closeConnections();
    }
}
