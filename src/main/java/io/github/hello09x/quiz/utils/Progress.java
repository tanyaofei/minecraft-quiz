package io.github.hello09x.quiz.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.boss.BossBar;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

@Getter
@AllArgsConstructor
public class Progress {

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private double total;

    private BossBar bossBar;

    private TimerTask task;

    @Nullable
    private Runnable onEnd;

    private final static Timer timer = new Timer("progress");

    public Progress(LocalDateTime startAt, LocalDateTime endAt, BossBar bossBar, @Nullable Runnable onEnd) {
        this.startAt = startAt;
        this.endAt = endAt;
        this.bossBar = bossBar;
        this.onEnd = onEnd;
        this.total = (double) Duration.between(startAt, endAt).toSeconds();
        this.task = new TimerTask() {
            @Override
            public void run() {
                var now = LocalDateTime.now();
                var remains = total - (double) Duration.between(startAt, now).toSeconds();
                if (remains < 0) {
                    try {
                        remove();
                        if (onEnd != null) {
                            onEnd.run();
                        }
                    } finally {
                        cancel();
                    }
                } else {
                    bossBar.setProgress(remains / total);
                }
            }
        };
        timer.schedule(this.task, 0, 1000);
    }

    public void remove() {
        this.task.cancel();
        this.bossBar.removeAll();
    }

}
