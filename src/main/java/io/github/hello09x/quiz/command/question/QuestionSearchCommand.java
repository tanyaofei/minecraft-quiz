package io.github.hello09x.quiz.command.question;

import io.github.hello09x.quiz.repository.QuestionRepository;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuestionSearchCommand extends ExecutableCommand {

    public final static QuestionSearchCommand instance = new QuestionSearchCommand(
            "搜索问题",
            "/quizadmin question search <关键字>",
            "quizadmin.*"
    );

    private final QuestionRepository repository = QuestionRepository.instance;

    public QuestionSearchCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
        super(description, usage, permission);
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
