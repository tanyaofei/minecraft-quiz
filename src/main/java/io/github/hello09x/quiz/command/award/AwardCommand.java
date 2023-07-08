package io.github.hello09x.quiz.command.award;

import io.github.tanyaofei.plugin.toolkit.command.ParentCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AwardCommand extends ParentCommand {

    public static AwardCommand instance = new AwardCommand(
            "奖励相关命令",
            null
    );

    static {
        instance.register("list", AwardListCommand.instance);
        instance.register("add", AwardAddCommand.instance);
        instance.register("delete", AwardDeleteCommand.instance);
        instance.register("test", AwardTestCommand.instance);
    }

    protected AwardCommand(@NotNull String description, @Nullable String permission) {
        super(description, permission);
    }

}
