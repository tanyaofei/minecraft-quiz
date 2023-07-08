package io.github.hello09x.quiz.command.question;

import io.github.hello09x.quiz.repository.QuestionRepository;
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

import java.util.List;

public class QuestionListCommand extends ExecutableCommand {

    public final static QuestionListCommand instance = new QuestionListCommand(
            "查看所有问题",
            "/quizadmin question list [页码] [数量]",
            "quizadmin.*"
    );

    private final QuestionRepository repository = QuestionRepository.instance;

    private final static int DEFAULT_PAGE_SIZE = 10;

        public static Component help = Helps.help(
                "查看所有问题",
                new Helps.Content("用法", "/quizadmin question list [页码] [数量]"),
                new Helps.Content("例子", List.of(
                        "/quizadmin question list      - 查看第 1 页",
                        "/quizadmin question list 2    - 查看第 2 页",
                        "/quizadmin question list 2 5  - 查看第 2 页, 每页 5 条"
                ))
        );

    public QuestionListCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
        super(description, usage, permission);
    }

    @Override
    public @NotNull Component getHelp(int page) {
        return help;
    }

    @Override
    protected boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length > 2) {
            return false;
        }

        int current = 1;
        if (args.length > 0) {
            try {
                current = Integer.parseInt(args[0]);
            } catch (ArithmeticException e) {
                return false;
            }
        }

        int size = DEFAULT_PAGE_SIZE;
        if (args.length > 1) {
            try {
                size = Integer.parseInt(args[1]);
            } catch (ArithmeticException e) {
                return false;
            }
        }

        var page = repository.selectPage(current, size);
        sender.sendMessage(page.toComponent(
                "问题库",
                question -> Component.textOfChildren(
                        Component.text("[" + question.id() + "]. ", NamedTextColor.DARK_GREEN),
                        Component.text(question.title())
                                .style(Style.style(NamedTextColor.WHITE, TextDecoration.UNDERLINED))
                                .clickEvent(ClickEvent.runCommand("/quizadmin question query " + question.id()))
                ),
                String.format("/quizadmin question list %s %s", current - 1, size), String.format("/quizadmin question list %s %s", current + 1, size)));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
