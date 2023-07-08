package io.github.hello09x.quiz.command.award;

import io.github.hello09x.quiz.repository.AwardRepository;
import io.github.hello09x.quiz.repository.model.Award;
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

public class AwardAddCommand extends ExecutableCommand {

    public final static AwardAddCommand instance = new AwardAddCommand();
    private final AwardRepository awardRepository = AwardRepository.instance;

    private final static Component help = Helps.help(
            "创建一项奖励",
            "使用 ; 作为多条命令的分隔符",
            List.of(
                    new Helps.Content("用法", "/quizadmin award add <命令[;...]>"),
                    new Helps.Content("例子", "/quizadmin award add experience %p add %r[1-10]; give %p diamond %r[1-10]"),
                    new Helps.Content("变量", List.of(
                            "%p: 玩家",
                            "%r[?-?]: 随机值"
                    ))
            )
    );

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("quizadmin.*");
    }

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
