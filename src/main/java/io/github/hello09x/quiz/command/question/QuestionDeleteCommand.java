package io.github.hello09x.quiz.command.question;

import io.github.hello09x.quiz.repository.QuestionRepository;
import io.github.hello09x.quiz.utils.command.ExecutableCommand;
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

    @Override
    public @NotNull Component getHelp() {
        return Component.textOfChildren(
                Component.text("删除一道题目\n", NamedTextColor.YELLOW),
                Component.text("用法: ", NamedTextColor.GOLD), Component.text("/quizadmin question delete <ID>\n"),
                Component.text("例子: ", NamedTextColor.GOLD), Component.text("/quizadmin question delete 1")
        );
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
