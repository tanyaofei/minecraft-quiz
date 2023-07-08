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

    public final static AwardDeleteCommand instance = new AwardDeleteCommand(
            "删除奖励",
            "/quizadmin award delete <ID>",
            "quizadmin.*"
    );
    private final AwardRepository awardRepository = AwardRepository.instance;

    public AwardDeleteCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
        super(description, usage, permission);
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
