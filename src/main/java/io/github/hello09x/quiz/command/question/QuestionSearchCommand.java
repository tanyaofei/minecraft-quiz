package io.github.hello09x.quiz.command.question;

import io.github.hello09x.quiz.repository.QuestionRepository;
import io.github.hello09x.quiz.utils.command.ExecutableCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuestionSearchCommand extends ExecutableCommand {

    public final static QuestionSearchCommand instance = new QuestionSearchCommand();
    private final QuestionRepository repository = QuestionRepository.instance;

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("quizadmin.*");
    }

    @Override
    public @NotNull Component getHelp() {
        return Component.textOfChildren(
                Component.text("搜索问题\n", NamedTextColor.YELLOW),
                Component.text("用法: ", NamedTextColor.GOLD), Component.text("/quizadmin question search <关键字>\n"),
                Component.text("例子: ", NamedTextColor.GOLD), Component.text("/quizadmin question search 一加一等于几")
        );
    }

    @Override
    protected boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length == 0) {
            return false;
        }

        var title = String.join(" ", args);
        var questions = repository.selectListByTitleLike(title, 10);
        if (questions.isEmpty()) {
            sender.sendMessage(Component.text("没有搜索到相关的问题...", NamedTextColor.GRAY));
            return true;
        }

        var message = Component.empty();
        for (var question : questions) {
            message = message.append(Component.textOfChildren(
                    Component.text(question.id() + ". ", NamedTextColor.DARK_GREEN),
                    question.getTitleComponent().clickEvent(ClickEvent.runCommand("/quizadmin question query " + question.id())),
                    Component.newline()
            ));
        }
        sender.sendMessage(message);
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
