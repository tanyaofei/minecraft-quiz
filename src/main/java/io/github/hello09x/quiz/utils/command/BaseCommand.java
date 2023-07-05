package io.github.hello09x.quiz.utils.command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseCommand implements TabExecutor {

    @Nullable
    protected final String permission;

    protected BaseCommand(@Nullable String permission) {
        this.permission = permission;
    }

    protected BaseCommand() {
        this(null);
    }

    public boolean hasPermission(CommandSender sender) {
        return permission == null || sender.hasPermission(permission);
    }

    @NotNull
    public abstract Component getHelp();

}
