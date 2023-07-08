package io.github.hello09x.quiz.command.question;

import io.github.hello09x.quiz.repository.QuestionRepository;
import io.github.hello09x.quiz.repository.model.Question;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class QuestionQueryCommand extends ExecutableCommand {

    public final static QuestionQueryCommand instance = new QuestionQueryCommand(
            "查看问题",
            "/quizadmin question query <ID>",
            "quizadmin.*"
    );

    private final QuestionRepository repository = QuestionRepository.instance;

    public QuestionQueryCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
        super(description, usage, permission);
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

        var question = repository.selectById(id);
        if (question == null) {
            sender.sendMessage(text(String.format("ID 为 %d 的问题不存在", id), NamedTextColor.RED));
            return true;
        }

        sender.sendMessage(Component.textOfChildren(
                text("____/ 问题 \\____\n", YELLOW),
                text("ID: ", GOLD), text(question.id()), newline(),
                text("题目: ", GOLD), question.getTitleComponent(), newline(),
                text("答案: \n", GOLD), getAnswersComponent(question)
        ));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        return Collections.emptyList();
    }

    private Component getTitleComponent(@NotNull Question question) {
        var full = text(question.title());
        if (question.title().length() > 30) {
            return text(question.title().substring(0, 30) + "...").style(Style.style(TextDecoration.UNDERLINED)).hoverEvent(HoverEvent.showText(full));
        }
        return full;
    }

    private Component getAnswersComponent(@NotNull Question question) {
        if (question.answers().isEmpty()) {
            return text("... 没有任何答案 ...", GRAY);
        }

        var c = Component.empty();
        var itr = question.answers().listIterator();
        while (itr.hasNext()) {
            var i = itr.nextIndex() + 1;
            var answer = itr.next();
            c = c.append(Component.textOfChildren(
                    text(i + ". ", DARK_GREEN),
                    text(answer + "\n")
            ));
        }
        return c;
    }


}
