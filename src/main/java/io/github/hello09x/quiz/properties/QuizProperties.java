package io.github.hello09x.quiz.properties;


import io.github.hello09x.quiz.Quiz;
import io.github.tanyaofei.plugin.toolkit.io.IOUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.logging.Logger;

@Data
@Accessors(chain = true)
public class QuizProperties {

    private final static String VERSION = "1";

    private final static Logger log = Quiz.getInstance().getLogger();

    /**
     * 多久发起一次问答
     */
    private Duration interval;

    /**
     * 在正确答案出现前问答等待的时间
     */
    private Duration expiresIn;

    public QuizProperties(@NotNull JavaPlugin plugin) {
        var folder = plugin.getDataFolder();
        if (!folder.exists() && !folder.mkdirs()) {
            throw new ExceptionInInitializerError("创建配置文件目录失败");
        }

        var file = Path.of(folder.getAbsolutePath(), "config.yml").toFile();
        if (!file.exists()) {
            try (var in = Quiz.class.getClassLoader().getResource("config.yml").openStream()) {
                IOUtil.copy(in, new FileWriter(file));
                log.info("已创建默认配置文件");
            } catch (IOException e) {
                throw new UncheckedIOException("生成默认配置文件失败", e);
            }
        }

        this.reload(plugin);
    }

    public void reload(@NotNull Plugin plugin) {
        plugin.reloadConfig();
        this.reload(plugin.getConfig());
    }

    public void reload(@NotNull FileConfiguration configuration) {
        var version = configuration.getString("version");
        if (!VERSION.equals(version)) {
            log.warning(String.format("当前插件的配置文件已更新, 请删除原有的配置文件重新配置。当前版本: %s, 插件版本: %s", version, VERSION));
        }

        var interval = configuration.getLong("interval", 1800);
        if (interval < 0) {
            throw new ExceptionInInitializerError("interval 不能为负数");
        }
        var expiresIn = configuration.getLong("expires-in", 600);
        if (expiresIn < 0) {
            throw new ExceptionInInitializerError("expires-in 不能为负数");
        }

        this.interval = Duration.ofSeconds(interval);
        this.expiresIn = Duration.ofSeconds(expiresIn);
    }

}
