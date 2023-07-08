package io.github.hello09x.quiz.command.answer;

import io.github.tanyaofei.plugin.toolkit.command.ParentCommand;
import io.github.tanyaofei.plugin.toolkit.command.help.Helps;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnswerCommand extends ParentCommand {

    public final static AnswerCommand instance = new AnswerCommand();

    private final static Component help = Helps.help(
            "答案相关命令",
            null,
            List.of(
                    new Helps.Content("add <id> [答案]", "添加答案"),
                    new Helps.Content("delete <id> [序号]", "删除答案")
            )
    );

    static {
        instance.register("add", AnswerAddCommand.instance);
        instance.register("delete", AnswerDeleteCommand.instance);
    }

    @Override
    public @NotNull Component getHelp() {
        return help;
    }

}
