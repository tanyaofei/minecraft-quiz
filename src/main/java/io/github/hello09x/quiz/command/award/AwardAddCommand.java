package io.github.hello09x.quiz.command.award;

import io.github.hello09x.quiz.repository.AwardRepository;
import io.github.hello09x.quiz.repository.model.Award;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AwardAddCommand extends ExecutableCommand {

    public final static AwardAddCommand instance = new AwardAddCommand();
    private final AwardRepository awardRepository = AwardRepository.instance;

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("quizadmin.*");
    }

    @Override
    public @NotNull Component getHelp() {
        return Component.textOfChildren(
                Component.text("创建一项奖励\n", NamedTextColor.YELLOW),
                Component.text("用法: ", NamedTextColor.GOLD), Component.text("/quizadmin award add <奖励命令>\n"),
                Component.text("例子: ", NamedTextColor.GOLD), Component.text("/quizadmin award add experience %p add %r[1-10]; give %p diamond %r[1-10]\n"),
                Component.text("注意: ", NamedTextColor.GOLD), Component.text("多条命令使用"), Component.text(" ; ", NamedTextColor.DARK_GRAY), Component.text("分割\n"),
                Component.text("变量: \n", NamedTextColor.GOLD),
                Component.text("    %p: ", NamedTextColor.DARK_GREEN), Component.text("玩家\n"),
                Component.text("    %r[?-?]: ", NamedTextColor.DARK_GREEN), Component.text("随机数")
        );
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

        var award = new Award(
                null,
                String.join(" ", args)
        );
        awardRepository.insert(award);
        sender.sendMessage(Component.text(String.format("成功创建了一项命令为 '%s' 的奖励", award.commands())).color(NamedTextColor.DARK_GREEN));
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
