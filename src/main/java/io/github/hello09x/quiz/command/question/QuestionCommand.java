package io.github.hello09x.quiz.command.question;

import io.github.tanyaofei.plugin.toolkit.command.ParentCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuestionCommand extends ParentCommand {


    public static final QuestionCommand instance = new QuestionCommand("问题相关命令", null);

    static {
        instance.register("list", QuestionListCommand.instance);
        instance.register("add", QuestionAddCommand.instance);
        instance.register("delete", QuestionDeleteCommand.instance);
        instance.register("query", QuestionQueryCommand.instance);
        instance.register("search", QuestionSearchCommand.instance);
        instance.register("import", QuestionImportCommand.instance);
    }

    protected QuestionCommand(@NotNull String description, @Nullable String permission) {
        super(description, permission);
    }

}
