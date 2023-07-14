package io.github.hello09x.quiz.manager;

import com.google.common.base.Throwables;
import io.github.hello09x.quiz.Quiz;
import io.github.hello09x.quiz.manager.domain.Asking;
import io.github.hello09x.quiz.repository.AwardRepository;
import io.github.hello09x.quiz.repository.QuestionRepository;
import io.github.hello09x.quiz.repository.StatisticRepository;
import io.github.tanyaofei.plugin.toolkit.progress.ProgressType;
import io.github.tanyaofei.plugin.toolkit.progress.TimeProgress;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
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
    private final StatisticRepository statisticRepository = StatisticRepository.instance;
    private final AwardManager awardManager = AwardManager.instance;
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
            current.getProgress().cancel();
            current = null;
        }

        var question = questionRepository.selectRandomly();
        if (question == null) {
            this.quiz.getServer().broadcast(Component.text("[quiz] 实在想不出问什么，本轮有奖问答取消", NamedTextColor.GRAY));
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
        var bossBar = server.createBossBar(question.title(), BarColor.GREEN, BarStyle.SOLID);
        var progress = new TimeProgress(
                createdAt,
                expiresAt,
                bossBar,
                1000,
                ProgressType.REWARD,
                p -> {
                    if (p > 0.75) {
                        bossBar.setColor(BarColor.GREEN);
                    } else if (p > 0.5) {
                        bossBar.setColor(BarColor.BLUE);
                    } else if (p > 0.25) {
                        bossBar.setColor(BarColor.YELLOW);
                    } else {
                        bossBar.setColor(BarColor.RED);
                    }
                },
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
            bossBar.addPlayer(player);
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
        this.current.getProgress().cancel();
        this.current = null;

        var server = player.getServer();
        server.broadcast(
                Component.textOfChildren(
                        Component.text("[quiz] 恭喜玩家 ", NamedTextColor.YELLOW),
                        Component.text(player.getName(), NamedTextColor.GOLD),
                        Component.text(" 蒙对了，正确答案是: ", NamedTextColor.YELLOW),
                        Component.text(answer).style(Style.style(NamedTextColor.GOLD, TextDecoration.ITALIC))
                ));

        new BukkitRunnable() {
            @Override
            public void run() {
                server.playSound(Sound.sound(
                        org.bukkit.Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST.key(),
                        Sound.Source.MASTER,
                        1.0F,
                        1.0F)
                );
            }
        }.runTask(quiz);

        player.showTitle(Title.title(Component.text("Bingo, 蒙对了！", NamedTextColor.YELLOW), Component.empty()));

        if (player.getName().equals("hello09x")) {
            server.broadcast(Component.text("[quiz] hello09x 也太帅了吧!", NamedTextColor.DARK_GREEN));
        }

        // 统计
        CompletableFuture.runAsync(() -> {
            try {
                statisticRepository.addCorrect(player, 1);
            } catch (Throwable e) {
                log.warning(Throwables.getStackTraceAsString(e));
            }
        });

        // 奖励
        var award = awardRepository.selectRandomly();
        if (award == null) {
            server.broadcast(Component.text("[quiz] 腐竹忘记设置奖励了, 先欠着下次一定", NamedTextColor.RED));
            return true;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                awardManager.issue(award, player);
            }
        }.runTask(quiz);
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
