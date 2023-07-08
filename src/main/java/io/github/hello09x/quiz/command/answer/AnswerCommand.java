package io.github.hello09x.quiz.command.answer;

import io.github.tanyaofei.plugin.toolkit.command.ParentCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnswerCommand extends ParentCommand {

    public final static AnswerCommand instance = new AnswerCommand("答案相关命令", null);

    static {
        instance.register("add", AnswerAddCommand.instance);
        instance.register("delete", AnswerDeleteCommand.instance);
    }

    protected AnswerCommand(@NotNull String description, @Nullable String permission) {
        super(description, permission);
    }

}
