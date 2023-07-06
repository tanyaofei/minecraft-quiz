package io.github.hello09x.quiz.command.award;

import io.github.hello09x.quiz.repository.AwardRepository;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AwardDeleteCommand extends ExecutableCommand {

    public final static AwardDeleteCommand instance = new AwardDeleteCommand();
    private final AwardRepository awardRepository = AwardRepository.instance;

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("quizadmin.*");
    }

    @Override
    public @NotNull Component getHelp() {
        return Component.textOfChildren(
                Component.text("删除一项奖励", NamedTextColor.YELLOW), Component.newline(),
                Component.text("用法: ", NamedTextColor.GOLD), Component.text("/quizadmin delete <ID>\n"),
                Component.text("例子: \n", NamedTextColor.GOLD),
                Component.text("    /quizadmin delete 1", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("删除 ID 为 1 的奖励")
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

        long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (ArithmeticException e) {
            return false;
        }

        var success = awardRepository.deleteById(id);
        sender.sendMessage(success > 0
                ? Component.text("删除成功", NamedTextColor.GREEN)
                : Component.text("奖励不存在", NamedTextColor.RED)
        );
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
