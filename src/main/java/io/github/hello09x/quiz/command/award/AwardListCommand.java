package io.github.hello09x.quiz.command.award;

import io.github.hello09x.quiz.repository.AwardRepository;
import io.github.hello09x.quiz.utils.TextColor;
import io.github.hello09x.quiz.utils.command.ExecutableCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AwardListCommand extends ExecutableCommand {

    public static final AwardListCommand instance = new AwardListCommand();
    private static final int DEFAULT_PAGE_SIZE = 10;
    private final AwardRepository repository = AwardRepository.instance;

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("quizadmin.*");
    }

    @Override
    public @NotNull Component getHelp() {
        return Component.textOfChildren(
                Component.text("查看所有奖励\n", NamedTextColor.YELLOW),
                Component.text("用法: ", NamedTextColor.GOLD), Component.text("/quizadmin award list [页码] [条数]\n"),
                Component.text("例子: \n", NamedTextColor.GOLD),
                Component.text("    /quizadmin award list", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("查看第 1 页\n"),
                Component.text("    /quizadmin award list 2", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("查看第 2 页\n"),
                Component.text("    /quizadmin award list 2 10", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("查看第 2 页, 每页 10 条\n")
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
                "奖励池",
                award -> Component.text(
                        TextColor.GREEN
                                + award.id()
                                + ". "
                                + TextColor.WHITE
                                + award.commands()),
                String.format("/quizadmin award list %s %s", current - 1, size), String.format("/quizadmin award list %s %s", current + 1, size)));
        return true;
    }

    /**
     * quizadmin award list
     */
    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        return Collections.emptyList();
    }
}
