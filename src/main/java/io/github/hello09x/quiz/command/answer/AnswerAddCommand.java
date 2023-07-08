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

import java.util.Arrays;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class AnswerAddCommand extends ExecutableCommand {

    private final QuestionRepository repository = QuestionRepository.instance;

    public final static AnswerAddCommand instance = new AnswerAddCommand();

    public final static Component help = Helps.help(
            "添加答案",
            "你可以多次执行来添加多个答案",
            List.of(
                    new Helps.Content("用法", "/quizadmin answer add <id> <答案>")
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
        if (args.length < 2) {
            return false;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(text("错误的 ID", NamedTextColor.RED));
            return false;
        }

        var answer = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        var success = repository.addAnswerById(id, answer);
        if (success == 0) {
            sender.sendMessage(text(String.format("ID 为 %d 的问题不存在", id), NamedTextColor.RED));
            return true;
        }

        sender.sendMessage(text(String.format("你成功为 ID 为 %d 的问题添加了一个新的答案", id), NamedTextColor.GREEN));
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
