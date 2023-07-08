package io.github.hello09x.quiz.command.award;

import io.github.tanyaofei.plugin.toolkit.command.ParentCommand;
import io.github.tanyaofei.plugin.toolkit.command.help.Helps;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AwardCommand extends ParentCommand {

    public static AwardCommand instance = new AwardCommand();

    private final static Component help = Helps.help(
            "奖励相关命令",
            null,
            List.of(
                    new Helps.Content("list [页码] [数量]", "查看所有奖励"),
                    new Helps.Content("add <命令 [;...]>", "添加题目"),
                    new Helps.Content("delete <ID>", "删除奖励"),
                    new Helps.Content("test <玩家|ID>", "测试方法奖励")
            )
    );

    static {
        instance.register("list", AwardListCommand.instance);
        instance.register("add", AwardAddCommand.instance);
        instance.register("delete", AwardDeleteCommand.instance);
        instance.register("test", AwardTestCommand.instance);
    }

    @Override
    public @NotNull Component getHelp() {
        return help;
    }

}
