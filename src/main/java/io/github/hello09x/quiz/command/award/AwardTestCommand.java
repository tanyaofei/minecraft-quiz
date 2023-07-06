package io.github.hello09x.quiz.command.award;

import io.github.hello09x.quiz.manager.AwardManager;
import io.github.hello09x.quiz.repository.AwardRepository;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AwardTestCommand extends ExecutableCommand {

    public final static AwardTestCommand instance = new AwardTestCommand();
    private final AwardRepository awardRepository = AwardRepository.instance;
    private final AwardManager awardManager = AwardManager.instance;

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("quizadmin.*");
    }

    @Override
    public @NotNull Component getHelp() {
        return Component.textOfChildren(
                Component.text("测试发放奖励\n", NamedTextColor.YELLOW),
                Component.text("用法: ", NamedTextColor.GOLD), Component.text("/quizadmin award test [ID|玩家]\n"),
                Component.text("例子:\n", NamedTextColor.GOLD),
                Component.text("    /quizadmin award test", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("给自己随机发放方法一个奖励\n"),
                Component.text("    /quizadmin award test 1", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("给自己发放 ID 为 1 的奖励\n"),
                Component.text("    /quizadmin award test hello09x", NamedTextColor.DARK_GREEN), Component.text(" - ", NamedTextColor.GRAY), Component.text("给玩家 hello09x 随机发放一个奖励")
        );

    }

    @Override
    protected boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        long id;
        Player player;
        if (args.length == 0) {
            id = 0;
            if (!(sender instanceof Player p)) {
                sender.sendMessage(Component.text("你不是玩家, 无法给自己发放奖励", NamedTextColor.RED));
                return true;
            }
            player = p;
        } else if (args.length == 1) {
            try {
                id = Integer.parseInt(args[0]);
                if (!(sender instanceof Player p)) {
                    sender.sendMessage(Component.text("你不是玩家, 无法给自己发放奖励", NamedTextColor.RED));
                    return true;
                }
                player = p;
            } catch (NumberFormatException ignored) {
                id = 0;
                player = sender.getServer().getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage(Component.text(String.format("玩家 '%s' 不存在或者不在线", args[0]), NamedTextColor.RED));
                    return true;
                }
            }
        } else {
            return false;
        }


        var award = id == 0
                ? awardRepository.selectRandomly()
                : awardRepository.selectById(id);

        if (award == null) {
            if (id == 0) {
                sender.sendMessage(Component.text("奖励池里没有任何奖励", NamedTextColor.RED));
            } else {
                sender.sendMessage(Component.text(String.format("奖励池里没有 ID 为 '%d' 的奖励", id), NamedTextColor.RED));
            }
            return true;
        }

        var commands = awardManager.issue(award, player);
        sender.sendMessage(Component.textOfChildren(
                Component.text("给玩家 ", NamedTextColor.GREEN),
                Component.text(player.getName(), NamedTextColor.YELLOW),
                Component.text(" 发放了一个", NamedTextColor.GREEN),
                Component.text("[奖励]", NamedTextColor.GOLD).hoverEvent(HoverEvent.showText(Component.text(StringUtils.join(commands, '\n')))))
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
        if (args.length != 1) {
            return Collections.emptyList();
        }
        return sender
                .getServer()
                .getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .filter(name -> name.isBlank() || name.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }
}
