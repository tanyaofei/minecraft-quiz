package io.github.hello09x.quiz.command.question;

import io.github.hello09x.quiz.repository.QuestionRepository;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
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

    public final static QuestionListCommand instance = new QuestionListCommand();

    private final QuestionRepository repository = QuestionRepository.instance;

    private final static int DEFAULT_PAGE_SIZE = 10;

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("quizadmin.*");
    }

    @Override
    public @NotNull Component getHelp() {
        return Component.textOfChildren(
                Component.text("查看所有问题\n", NamedTextColor.YELLOW),
                Component.text("用法: ", NamedTextColor.GOLD), Component.text("/quizadmin question list [页码] [条数]\n"),
                Component.text("例子: \n", NamedTextColor.GOLD),
                Component.text("    /quizadmin question list 2", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("查看第 2 页\n"),
                Component.text("    /quizadmin question list 2 10", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("查看第 2 页, 每页 10 条\n")
        );
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
