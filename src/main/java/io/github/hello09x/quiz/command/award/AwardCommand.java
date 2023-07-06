package io.github.hello09x.quiz.command.award;

import io.github.tanyaofei.plugin.toolkit.command.ParentCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class AwardCommand extends ParentCommand {

    public static AwardCommand instance = new AwardCommand();

    static {
        instance.register("list", AwardListCommand.instance);
        instance.register("add", AwardAddCommand.instance);
        instance.register("delete", AwardDeleteCommand.instance);
        instance.register("test", AwardTestCommand.instance);
    }

    @Override
    public @NotNull Component getHelp() {
        return Component.textOfChildren(
                Component.text("奖励相关命令", NamedTextColor.YELLOW),
                Component.text("list", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("查看奖励\n"),
                Component.text("add", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("添加奖励\n"),
                Component.text("delete", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("删除奖励\n"),
                Component.text("test", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("测试奖励")
        );
    }

}
