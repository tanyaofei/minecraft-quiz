package io.github.hello09x.quiz.command;

import io.github.hello09x.quiz.command.answer.AnswerCommand;
import io.github.hello09x.quiz.command.award.AwardCommand;
import io.github.hello09x.quiz.command.question.QuestionCommand;
import io.github.hello09x.quiz.command.reload.ReloadCommand;
import io.github.tanyaofei.plugin.toolkit.command.ParentCommand;
import io.github.tanyaofei.plugin.toolkit.command.help.Helps;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdminCommand extends ParentCommand {

    public static final AdminCommand instance = new AdminCommand("quizadmin.*");

    public AdminCommand(String permission) {
        super(permission);
    }

    public final static Component help = Helps.help(
            "有奖问题管理员相关命令",
            "输入 /quizadmin <命令> ? 查看更详细的帮助",
            List.of(
                    new Helps.Content("question", "问题相关命令"),
                    new Helps.Content("answer", "答案相关命令"),
                    new Helps.Content("award", "奖励相关命令"),
                    new Helps.Content("reload", "重载配置文件")
            )
    );

    static {
        instance.register("award", AwardCommand.instance);
        instance.register("question", QuestionCommand.instance);
        instance.register("answer", AnswerCommand.instance);
        instance.register("reload", ReloadCommand.instance);
    }

    @Override
    public @NotNull Component getHelp() {
        return help;
    }

}
