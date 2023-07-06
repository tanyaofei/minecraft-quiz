package io.github.hello09x.quiz.repository.model;

import io.github.tanyaofei.plugin.toolkit.database.Column;
import io.github.tanyaofei.plugin.toolkit.database.Id;
import io.github.tanyaofei.plugin.toolkit.database.Table;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


@Table("award")
public record Award(

        // ID
        @Id("id")
        Integer id,

        // 命令
        // 多个命令使用 ';' 作为分割
        @Column("commands")
        String commands
) {

    private final static Pattern RANDOME_PATTERN = Pattern.compile("%r\\[([0-9]+)-([0-9]+)]");

    @NotNull
    public List<String> getCommandLines(Player player) {
        return getCommandLines(player.getName());
    }

    @NotNull
    public List<String> getCommandLines(String player) {
        var commandLines = new ArrayList<String>();
        for (var cm : commands.split(";")) {
            if (cm.isBlank()) {
                continue;
            }
            var cl = cm.replace("%p", player);
            var match = RANDOME_PATTERN.matcher(cl);
            while (match.find()) {
                var from = Integer.parseInt(match.group(1));
                var to = Integer.parseInt(match.group(2));
                cl = StringUtils.replaceOnce(cl, match.group(0), String.valueOf(RandomUtils.nextInt(from, to + 1)));
            }
            cl = cl.trim();
            commandLines.add(cl);
        }
        return commandLines;
    }


}
