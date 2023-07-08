package io.github.hello09x.quiz.command.question;

import io.github.hello09x.quiz.repository.QuestionRepository;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import io.github.tanyaofei.plugin.toolkit.command.help.Helps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuestionDeleteCommand extends ExecutableCommand {

    public final static QuestionDeleteCommand instance = new QuestionDeleteCommand();
    private final QuestionRepository repository = QuestionRepository.instance;

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("quizadmin.*");
    }

    private final static Component help = Helps.help(
            "删除题目",
            null,
            List.of(
                    new Helps.Content("用法", "/quizadmin delete <ID>")
            )
    );

    @Override
    public @NotNull Component getHelp() {
        return help;
    }

    @Override
    protected boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length != 1) {
            return false;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return false;
        }

        var success = repository.deleteById(id);
                sender.sendMessage(success > 0
                ? Component.text("删除成功", NamedTextColor.GREEN)
                : Component.text("问题不存在", NamedTextColor.RED)
        );
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        return null;
    }
}
