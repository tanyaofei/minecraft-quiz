package io.github.hello09x.quiz.command;

import io.github.hello09x.quiz.Quiz;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReloadCommand extends ExecutableCommand {

    public final static ReloadCommand instance = new ReloadCommand();


    @Override
    public @NotNull Component getHelp() {
        return Component.text("重载插件", NamedTextColor.YELLOW);
    }

    @Override
    protected boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length != 0) {
            return false;
        }

        Quiz.getInstance().getProperties().reload(Quiz.getInstance());
        sender.sendMessage(Component.text("重载成功", NamedTextColor.GREEN));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
