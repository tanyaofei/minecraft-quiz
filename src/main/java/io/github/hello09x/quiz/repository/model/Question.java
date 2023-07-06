package io.github.hello09x.quiz.repository.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.tanyaofei.plugin.toolkit.database.Column;
import io.github.tanyaofei.plugin.toolkit.database.Id;
import io.github.tanyaofei.plugin.toolkit.database.Table;
import io.github.tanyaofei.plugin.toolkit.database.typehandler.TypeHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Table("question")
public record Question(

        // ID
        @Id("id")
        Integer id,

        // 问题
        @Column("title")
        String title,

        // 答案
        @Column(value = "answers", typeHandler = AnswerTypeHandler.class)
        List<String> answers
) {

    public Component getTitleComponent() {
        var full = Component.text(title);
        if (title.length() > 30) {
            return Component.text(title.substring(0, 30) + "...").style(Style.style(TextDecoration.UNDERLINED)).hoverEvent(HoverEvent.showText(full));
        }
        return full;
    }

    public static class AnswerTypeHandler implements TypeHandler<List<String>> {

        private final Gson gson = new Gson();
        private final TypeToken<List<String>> typeToken = new TypeToken<>() {
        };

        @Override
        public void setParameter(
                @NotNull PreparedStatement preparedStatement,
                int i,
                @NotNull List<String> strings,
                @NotNull JDBCType jdbcType
        ) throws SQLException {
            preparedStatement.setObject(i, gson.toJson(strings));
        }

        @Override
        public @Nullable List<String> getResult(
                @NotNull ResultSet resultSet,
                @NotNull String s
        ) throws SQLException {
            return Optional.ofNullable(resultSet.getString(s)).map(json -> gson.fromJson(json, typeToken)).orElse(null);
        }
    }


}
