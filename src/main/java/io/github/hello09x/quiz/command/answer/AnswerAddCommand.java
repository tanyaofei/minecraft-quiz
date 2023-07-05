package io.github.hello09x.quiz.command.answer;

import io.github.hello09x.quiz.repository.QuestionRepository;
import io.github.hello09x.quiz.utils.command.ExecutableCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AnswerAddCommand extends ExecutableCommand {

    private final QuestionRepository repository = QuestionRepository.instance;

    public final static AnswerAddCommand instance = new AnswerAddCommand();

    @Override
    public @NotNull Component getHelp() {
        return Component.textOfChildren(
                Component.text("功能: 添加答案; 你可以通过多次执行来为一道题添加多个答案\n", NamedTextColor.YELLOW),
                Component.text("用法: ", NamedTextColor.GOLD), Component.text("/quizadmin answer add <题目ID> <答案>\n"),
                Component.text("例子: ", NamedTextColor.GOLD), Component.text("/quizadmin answer add 1 这是答案\n")
        );
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
            sender.sendMessage(Component.text("错误的 ID", NamedTextColor.RED));
            return false;
        }

        var answer = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        var success = repository.addAnswerById(id, answer);
        if (success == 0) {
            sender.sendMessage(Component.text(String.format("ID 为 %d 的问题不存在", id), NamedTextColor.RED));
            return true;
        }

        sender.sendMessage(Component.text(String.format("你成功为 ID 为 %d 的问题添加了一个新的答案", id), NamedTextColor.GREEN));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
