package io.github.hello09x.quiz.repository.model;


import io.github.tanyaofei.plugin.toolkit.database.Column;
import io.github.tanyaofei.plugin.toolkit.database.Id;
import io.github.tanyaofei.plugin.toolkit.database.Table;

@Table("statistics")
public record Statistic(
        // 玩家名称
        // 如果玩家改过名, 则他下一次答题成功更新数据时才会重新写入新的名称
        @Id("player_name")
        String playerName,

        // 正确次数
        @Column("corrects")
        Integer corrects
) {

}
