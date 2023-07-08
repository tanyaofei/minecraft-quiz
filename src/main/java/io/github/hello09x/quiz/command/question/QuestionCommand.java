package io.github.hello09x.quiz.command.question;

import io.github.tanyaofei.plugin.toolkit.command.ParentCommand;
import io.github.tanyaofei.plugin.toolkit.command.help.Helps;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QuestionCommand extends ParentCommand {


    public static final QuestionCommand instance = new QuestionCommand();

    private final static Component help = Helps.help(
            "题库相关命令",
            null,
            List.of(
                    new Helps.Content("list [页码] [数量]", "查看所有题目"),
                    new Helps.Content("add <题目>", "添加题目"),
                    new Helps.Content("delete <ID>", "删除题目"),
                    new Helps.Content("query <ID>", "查看题目"),
                    new Helps.Content("search <关键字>", "搜索题目"),
                    new Helps.Content("import <文件名>", "导入题目")
            )
    );

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
        return help;
    }

}
