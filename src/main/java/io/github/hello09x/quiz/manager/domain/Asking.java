package io.github.hello09x.quiz.manager.domain;

import io.github.hello09x.quiz.repository.model.Question;
import io.github.tanyaofei.plugin.toolkit.progress.TimeProgress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class Asking {

    /**
     * 问题
     */
    private Question question;

    /**
     * 进度条
     */
    private TimeProgress progress;

    /**
     * 提问时间
     */
    private LocalDateTime createdAt;

    /**
     * 结束提问时间
     */
    private LocalDateTime expiresAt;

}
