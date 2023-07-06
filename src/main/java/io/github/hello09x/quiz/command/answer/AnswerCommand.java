package io.github.hello09x.quiz.command.answer;

import io.github.hello09x.quiz.command.AdminCommand;
import io.github.tanyaofei.plugin.toolkit.command.ParentCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class AnswerCommand extends ParentCommand {

    public final static AdminCommand instance = new AdminCommand();

    static {
        instance.register("add", AnswerAddCommand.instance);
        instance.register("delete", AnswerDeleteCommand.instance);
    }

    @Override
    public @NotNull Component getHelp() {
        return Component.textOfChildren(
                Component.text("答案相关命令", NamedTextColor.YELLOW),
                Component.text("add", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("添加答案\n"),
                Component.text("delete", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("删除答案")
        );
    }

}
