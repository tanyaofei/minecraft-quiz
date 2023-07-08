package io.github.hello09x.quiz.command.question;

import io.github.hello09x.quiz.repository.QuestionRepository;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class QuestionRequeueCommand extends ExecutableCommand {

    public final static QuestionRequeueCommand instance = new QuestionRequeueCommand(
            "重新生成出题顺序表",
            "/quizadmin question requeue",
            "quizadmin.*"
    );

    private final QuestionRepository repository = QuestionRepository.instance;

    public QuestionRequeueCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
        super(description, usage, permission);
    }

    @Override
    protected boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        repository.requeue();
        sender.sendMessage(text("重新生成出题顺序表成功", NamedTextColor.GREEN));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length == 0) {
            return Collections.singletonList("--clear");
        }
        return null;
    }
}
