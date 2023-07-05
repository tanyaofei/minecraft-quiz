package io.github.hello09x.quiz.repository.model;

import io.github.hello09x.quiz.utils.database.Id;
import io.github.hello09x.quiz.utils.database.Table;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;

@Table("question")

public record Question(

        // ID
        @Id("id")
        Integer id,

        // 问题
        String title,

        // 答案
        List<String> answers
) {

    public Component getTitleComponent() {
        var full = Component.text(title);
        if (title.length() > 30) {
            return Component.text(title.substring(0, 30) + "...").style(Style.style(TextDecoration.UNDERLINED)).hoverEvent(HoverEvent.showText(full));
        }
        return full;
    }

}
