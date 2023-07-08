package io.github.hello09x.quiz;

import io.github.hello09x.quiz.command.AdminCommand;
import io.github.hello09x.quiz.listener.PlayerChatListener;
import io.github.hello09x.quiz.listener.PlayerJoinListener;
import io.github.hello09x.quiz.manager.QuizManager;
import io.github.hello09x.quiz.optional.StatisticPlaceholder;
import io.github.hello09x.quiz.properties.QuizProperties;
import io.github.hello09x.quiz.repository.QuestionRepository;
import io.github.tanyaofei.plugin.toolkit.database.AbstractRepository;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Quiz extends JavaPlugin {

    private static Quiz instance;

    @Getter
    private QuizProperties properties;

    private static Logger log;

    public static Quiz getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        log = getLogger();

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

        // optional
        {
            supportPlaceholderAPI();
        }

    }


    public void supportPlaceholderAPI() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return;
        }

        if (!new StatisticPlaceholder().register()) {
            log.warning("支援 PlaceholderAPI 失败");
            return;
        }

        log.info("已支援 PlaceholderAPI");
    }

    @Override
    public void onDisable() {
        AbstractRepository.closeConnection(this);

        {
            QuestionRepository.instance.cacheQueue();
            log.info("已缓存出题表");
        }

    }
}
