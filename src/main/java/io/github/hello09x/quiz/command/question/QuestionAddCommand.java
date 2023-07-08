package io.github.hello09x.quiz.command.question;

import io.github.hello09x.quiz.repository.QuestionRepository;
import io.github.hello09x.quiz.repository.model.Question;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;

public class QuestionAddCommand extends ExecutableCommand {

    public static final QuestionAddCommand instance = new QuestionAddCommand(
            "新增题目",
            "/quizadmin question add <问题>",
            "quizadmin.*"
    );

    private final QuestionRepository repository = QuestionRepository.instance;

    public QuestionAddCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
        super(description, usage, permission);
    }

    @Override
    protected boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length < 1) {
            return false;
        }

        var title = String.join(" ", args);

        var existed = repository.selectByTitle(title);
        if (existed != null) {
            sender.sendMessage(textOfChildren(
                    text("该问题已存在"), text("<--[此处]").style(Style.style(RED, ITALIC)).clickEvent(ClickEvent.runCommand("/quizadmin question query " + existed.id()))
            ));
            return true;
        }

        var question = new Question(
                null,
                title,
                Collections.emptyList()
        );

        var id = repository.insert(question);
        var addAnswerCommand = String.format("/quizadmin answer add %d ", id);
        sender.sendMessage(textOfChildren(
                text("你成功创建了一道题目, 继续使用 ", GREEN),
                text(addAnswerCommand).style(Style.style(UNDERLINED)).clickEvent(ClickEvent.suggestCommand(addAnswerCommand)),
                text("为它创建答案吧～", GREEN)
        ));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
