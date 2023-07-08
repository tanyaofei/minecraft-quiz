package io.github.hello09x.quiz.command.award;

import io.github.hello09x.quiz.repository.AwardRepository;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import io.github.tanyaofei.plugin.toolkit.command.help.Helps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AwardListCommand extends ExecutableCommand {

    public static final AwardListCommand instance = new AwardListCommand(
            "查看所有奖励",
            "/quizadmin award list [页码] [数量]",
            "quizadmin.*"
    );

    private static final int DEFAULT_PAGE_SIZE = 10;
    private final AwardRepository repository = AwardRepository.instance;

    public static Component help = Helps.help(
            "查看所有奖励",
            new Helps.Content("用法", "/quizadmin award list [页码] [数量]"),
            new Helps.Content("例子", List.of(
                    "/quizadmin award list      - 查看第 1 页",
                    "/quizadmin award list 2    - 查看第 2 页",
                    "/quizadmin award list 2 5  - 查看第 2 页, 每页 5 条"
            ))
    );

    public AwardListCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
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
                "奖励池",
                award -> Component.textOfChildren(
                        Component.text("[" + award.id() + "]. ", NamedTextColor.DARK_GREEN),
                        Component.text(award.commands())
                ),
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
