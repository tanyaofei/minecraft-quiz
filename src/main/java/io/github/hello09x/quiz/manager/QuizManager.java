package io.github.hello09x.quiz.manager;

import com.google.common.base.Throwables;
import io.github.hello09x.quiz.Quiz;
import io.github.hello09x.quiz.manager.domain.Asking;
import io.github.hello09x.quiz.repository.AwardRepository;
import io.github.hello09x.quiz.repository.QuestionRepository;
import io.github.hello09x.quiz.repository.StatisticsRepository;
import io.github.hello09x.quiz.utils.Progress;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class QuizManager {

    public final static QuizManager instance = new QuizManager();
    private final static Logger log = Quiz.getInstance().getLogger();

    private final Quiz quiz = Quiz.getInstance();
    private final QuestionRepository questionRepository = QuestionRepository.instance;
    private final AwardRepository awardRepository = AwardRepository.instance;
    private final StatisticsRepository statisticsRepository = StatisticsRepository.instance;
    private final ScheduledExecutorService timer;
    private volatile Asking current;

    public QuizManager() {
        this.timer = Executors.newSingleThreadScheduledExecutor();
        this.timer.schedule(
                this::askLoop,
                this.quiz.getProperties().getInterval().toSeconds(),
                TimeUnit.SECONDS
        );
    }

    public static QuizManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("not initialized");
        }
        return instance;
    }

    public void askLoop() {
        try {
            ask();
        } catch (Exception e) {
            log.warning(Throwables.getStackTraceAsString(e));
        } finally {
            var properties = this.quiz.getProperties();
            this.timer.schedule(
                    this::askLoop,
                    properties.getInterval().toSeconds(),
                    TimeUnit.SECONDS
            );
        }
    }

    public synchronized void ask() {
        if (getCurrent() != null) {
            log.warning("发起新的一轮提问时发现上一轮提问还未结束, 已强制结束...");
            current.getProgress().remove();
            current = null;
        }

        var question = questionRepository.selectRandomly();
        if (question == null) {
            this.quiz.getServer().broadcast(Component.text("[quiz] 实在想不出问什么，本轮有奖问答取消", NamedTextColor.RED));
            return;
        }

        var server = this.quiz.getServer();
        server.broadcast(
                Component.text("[quiz] 注意啦注意啦! 准备提问, 请直接在聊天里输入答案", NamedTextColor.YELLOW)
        );

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {
        }

        var createdAt = LocalDateTime.now();
        var expiresAt = createdAt.plus(quiz.getProperties().getExpiresIn());
        var progress = new Progress(
                createdAt,
                expiresAt,
                server.createBossBar(question.title(), BarColor.YELLOW, BarStyle.SOLID),
                () -> {
                    current = null;
                    server.broadcast(Component.text("[quiz] 看来没人能回答正确，奖品我吞掉啦～", NamedTextColor.YELLOW));
                }
        );
        current = new Asking(
                question,
                progress,
                createdAt,
                expiresAt
        );

        server.broadcast(
                Component.textOfChildren(
                        Component.text("[quiz] 问题是: ", NamedTextColor.YELLOW),
                        Component.text(question.title(), NamedTextColor.DARK_GREEN)
                )
        );

        for (var player : server.getOnlinePlayers()) {
            progress.getBossBar().addPlayer(player);
        }

    }

    @Nullable
    public Asking getCurrent() {
        var asking = this.current;
        if (asking != null) {
            var now = LocalDateTime.now();
            if (now.isAfter(asking.getExpiresAt())) {
                synchronized (this) {
                    if (this.current == asking) {
                        this.current = null;
                    }
                }
                asking = null;
            }
        }
        return asking;
    }

    public synchronized boolean answer(
            @NotNull Player player,
            @NotNull Asking asking,
            @NotNull String answer
    ) {
        if (this.current == null) {
            return false;
        }
        if (!isCorrect(asking, answer)) {
            return false;
        }
        this.current.getProgress().remove();
        this.current = null;

        var server = player.getServer();
        server.broadcast(
                Component.textOfChildren(
                        Component.text("[quiz] 恭喜玩家回答正确，他的答案是 ", NamedTextColor.DARK_GREEN),
                        Component.text(answer, NamedTextColor.GOLD)
                ));

        server.playSound(Sound.sound(
                org.bukkit.Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST.key(),
                Sound.Source.AMBIENT,
                0.8F,
                1.0F)
        );

        if (player.getName().equals("hello09x")) {
            server.broadcast(Component.text("[quiz] hello09x 也太帅了吧!", NamedTextColor.DARK_GREEN));
        }

        // 统计
        statisticsRepository.addCorrect(player.getUniqueId().toString(), 1);

        // 奖励
        var award = awardRepository.selectRandomly();
        if (award == null) {
            server.broadcast(Component.text("[quiz] 腐竹忘记设置奖励了, 先欠着下次一定", NamedTextColor.RED));
            return true;
        }

        var console = server.getConsoleSender();
        for (var cl : award.getCommandLines(player)) {
            try {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            server.dispatchCommand(console, cl);
                        } catch (Exception e) {
                            player.sendMessage(Component.text("[quiz] 发放奖励时发生错误, 先欠着下次一定", NamedTextColor.RED));
                            log.warning(Throwables.getStackTraceAsString(e));
                        }

                        player.sendMessage(Component.text("[quiz] 奖励已偷偷给你了, 不要告诉别人哦～").style(Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC)));
                    }
                }.runTask(quiz);
            } catch (Exception e) {
                player.sendMessage(Component.text("[quiz] 发放奖励时发生错误，先欠着下次一定", NamedTextColor.RED));
                log.warning(Throwables.getStackTraceAsString(e));
            }
        }

        return true;
    }

    private boolean isCorrect(
            @NotNull Asking asking,
            @NotNull String answer
    ) {
        for (var a : asking.getQuestion().answers()) {
            if (a.trim().equalsIgnoreCase(answer)) {
                return true;
            }
        }
        return false;
    }


}
