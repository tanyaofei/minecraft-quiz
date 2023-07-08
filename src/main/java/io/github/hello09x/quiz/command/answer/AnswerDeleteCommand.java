package io.github.hello09x.quiz.command.answer;

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

public class AnswerDeleteCommand extends ExecutableCommand {

    public final static AnswerDeleteCommand instance = new AnswerDeleteCommand();
    private final QuestionRepository repository = QuestionRepository.instance;

    public final static Component help = Helps.help(
            "删除答案",
            null,
            List.of(
                    new Helps.Content("用法", "/quizadmin answer delete <ID> [答案序号]")
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
        if (args.length != 1 && args.length != 2) {
            return false;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return false;
        }

        int n = 0;
        if (args.length > 1) {
            try {
                n = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        int success;
        try {
            success = repository.deleteAnswerById(id, n == 0 ? null : n - 1);
        } catch (IndexOutOfBoundsException e) {
            sender.sendMessage(Component.text("序号有误", NamedTextColor.RED));
            return true;
        }

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
