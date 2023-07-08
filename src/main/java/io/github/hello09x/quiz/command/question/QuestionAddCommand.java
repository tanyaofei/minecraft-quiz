package io.github.hello09x.quiz.command.question;

import io.github.hello09x.quiz.repository.QuestionRepository;
import io.github.hello09x.quiz.repository.model.Question;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import io.github.tanyaofei.plugin.toolkit.command.help.Helps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class QuestionAddCommand extends ExecutableCommand {

    public static final QuestionAddCommand instance = new QuestionAddCommand();
    private final QuestionRepository repository = QuestionRepository.instance;

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("quizadmin.*");
    }

    private final static Component help = Helps.help(
            "新增题目",
            null,
            List.of(
                    new Helps.Content("用法", "/quizadmin question add <题目>")
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
        if (args.length < 1) {
            return false;
        }

        var title = String.join(" ", args);

        var existed = repository.selectByTitle(title);
        if (existed != null) {
            sender.sendMessage(Component.textOfChildren(
                    Component.text("该问题已存在，他的 ID 是 ", NamedTextColor.RED),
                    Component.text(existed.id(), NamedTextColor.GOLD)
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
        sender.sendMessage(Component.textOfChildren(
                Component.text("你成功创建了一道题目, 继续使用 ", NamedTextColor.GREEN),
                Component.text(addAnswerCommand).style(Style.style(TextDecoration.UNDERLINED)).clickEvent(ClickEvent.suggestCommand(addAnswerCommand)),
                Component.text("为它创建答案吧～", NamedTextColor.GREEN)
        ));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
