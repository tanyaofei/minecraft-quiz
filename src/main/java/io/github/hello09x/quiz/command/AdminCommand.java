package io.github.hello09x.quiz.command;

import io.github.hello09x.quiz.command.answer.AnswerCommand;
import io.github.hello09x.quiz.command.award.AwardCommand;
import io.github.hello09x.quiz.command.question.QuestionCommand;
import io.github.hello09x.quiz.command.reload.ReloadCommand;
import io.github.tanyaofei.plugin.toolkit.command.ParentCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class AdminCommand extends ParentCommand {

    public static final AdminCommand instance = new AdminCommand("quizadmin.*");

    public AdminCommand(String permission) {
        super(permission);
    }

    static {
        instance.register("award", AwardCommand.instance);
        instance.register("question", QuestionCommand.instance);
        instance.register("answer", AnswerCommand.instance);
        instance.register("reload", ReloadCommand.instance);
    }

    @Override
    public @NotNull Component getHelp() {
        return Component.textOfChildren(
                Component.text("有奖问答管理权相关命令\n", NamedTextColor.YELLOW),
                Component.text("award", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("奖励相关命令\n"),
                Component.text("question", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("问题相关命令\n"),
                Component.text("answer", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("答案相关命令\n"),
                Component.text("reload", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("重载插件")
        );
    }

}
