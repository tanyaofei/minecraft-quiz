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

public class QuestionQueryCommand extends ExecutableCommand {

    public final static QuestionQueryCommand instance = new QuestionQueryCommand();
    private final QuestionRepository repository = QuestionRepository.instance;

    @Override
    public @NotNull Component getHelp() {
        return Component.textOfChildren(
                Component.text("查看问题\n", NamedTextColor.YELLOW),
                Component.text("用法: ", NamedTextColor.GOLD), Component.text("/quizadmin question query <ID>\n"),
                Component.text("例子: ", NamedTextColor.GOLD), Component.text("/quizadmin question query 1 \n")
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

        var question = repository.selectById(id);
        if (question == null) {
            sender.sendMessage(Component.text(String.format("ID 为 %d 的问题不存在", id), NamedTextColor.RED));
            return true;
        }

        sender.sendMessage(Component.textOfChildren(
                Component.text("____/ 问题 \\____\n", NamedTextColor.YELLOW),
                Component.text("题目: ", NamedTextColor.GOLD),
                question.getTitleComponent(), Component.newline(),
                Component.text("答案: \n", NamedTextColor.GOLD),
                getAnswersComponent(question)
        ));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }

    private Component getTitleComponent(@NotNull Question question) {
        var full = Component.text(question.title());
        if (question.title().length() > 30) {
            return Component.text(question.title().substring(0, 30) + "...").style(Style.style(TextDecoration.UNDERLINED)).hoverEvent(HoverEvent.showText(full));
        }
        return full;
    }

    private Component getAnswersComponent(@NotNull Question question) {
        if (question.answers().isEmpty()) {
            return Component.text("... 没有任何答案 ...", NamedTextColor.GRAY);
        }

        var c = Component.empty();
        var itr = question.answers().listIterator();
        while (itr.hasNext()) {
            var i = itr.nextIndex() + 1;
            var answer = itr.next();
            c = c.append(Component.textOfChildren(
                    Component.text(i + ". ", NamedTextColor.DARK_GREEN),
                    Component.text(answer + "\n")
            ));
        }
        return c;
    }


}
