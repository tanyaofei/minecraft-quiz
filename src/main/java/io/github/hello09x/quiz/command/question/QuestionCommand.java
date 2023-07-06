package io.github.hello09x.quiz.command.question;

import io.github.tanyaofei.plugin.toolkit.command.ParentCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class QuestionCommand extends ParentCommand {


    public static final QuestionCommand instance = new QuestionCommand();

    static {
        instance.register("list", QuestionListCommand.instance);
        instance.register("add", QuestionAddCommand.instance);
        instance.register("delete", QuestionDeleteCommand.instance);
        instance.register("query", QuestionQueryCommand.instance);
        instance.register("search", QuestionSearchCommand.instance);
        instance.register("import", QuestionImportCommand.instance);
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("quizadmin.*");
    }

    @Override
    public @NotNull Component getHelp() {
        return Component.textOfChildren(
                Component.text("题库相关命令", NamedTextColor.YELLOW),
                Component.text("list", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("查看题库\n"),
                Component.text("add", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("添加题目\n"),
                Component.text("delete", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("删除题目\n"),
                Component.text("query", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("查看题目\n"),
                Component.text("search", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("搜索题目\n"),
                Component.text("import", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("导入题目")
        );
    }

}
