package io.github.hello09x.quiz.command;

import io.github.hello09x.quiz.command.answer.AnswerCommand;
import io.github.hello09x.quiz.command.award.AwardCommand;
import io.github.hello09x.quiz.command.question.QuestionCommand;
import io.github.hello09x.quiz.command.reload.ReloadCommand;
import io.github.tanyaofei.plugin.toolkit.command.ParentCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdminCommand extends ParentCommand {

    public static final AdminCommand instance = new AdminCommand("有奖问答管理员相关命令", "quizadmin.*");

    static {
        instance.register("award", AwardCommand.instance);
        instance.register("question", QuestionCommand.instance);
        instance.register("answer", AnswerCommand.instance);
        instance.register("reload", ReloadCommand.instance);
    }

    protected AdminCommand(@NotNull String description, @Nullable String permission) {
        super(description, permission);
    }

}
