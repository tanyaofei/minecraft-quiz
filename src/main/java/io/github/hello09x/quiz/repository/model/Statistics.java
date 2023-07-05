package io.github.hello09x.quiz.repository.model;

import io.github.hello09x.quiz.utils.database.Id;
import io.github.hello09x.quiz.utils.database.Table;

@Table("statistics")
public record Statistics(
        // 玩家 ID
        @Id("id")
        String playerId,

        // 正确次数
        Integer correct
) {

}
