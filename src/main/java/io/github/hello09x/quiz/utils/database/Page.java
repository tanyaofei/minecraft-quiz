package io.github.hello09x.quiz.utils.database;

import io.github.hello09x.quiz.utils.TextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public record Page<T>(
        List<T> data,
        int total,
        int pages,
        int current,
        int size
) {
    public static <U> Page<U> emptyPage() {
        return new Page<>(
                Collections.emptyList(),
                0,
                0,
                1,
                0

        );
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int size() {
        return data.size();
    }

    public boolean hasNext() {
        return this.current < pages;
    }

    public boolean hasPrevious() {
        return this.current > 1;
    }

    public Component toComponent(
            @NotNull String title,
            @NotNull Function<T, Component> mapping,
            @NotNull String lastPageCommand,
            @NotNull String nextPageCommand
    ) {
        if (!lastPageCommand.startsWith("/")) {
            throw new IllegalArgumentException("lastPageCommand should starts with a slap");
        }
        if (!nextPageCommand.startsWith("/")) {
            throw new IllegalArgumentException("nextPageCommand should starts with a slap");
        }

        var message = Component.text().color(NamedTextColor.YELLOW).content("____/ " + title + " \\____\n");
        if (isEmpty()) {
            message.append(Component.text().color(NamedTextColor.GRAY).content("\n... 没有更多数据了 ...\n\n"));
        } else {
            for (var item : data) {
                message.append(mapping.apply(item)).append(Component.text("\n"));
            }
        }
        message.append(Component.text().color(NamedTextColor.DARK_GREEN).content("----<< "));
        message.append(
                !hasPrevious()
                        ? Component.text().color(NamedTextColor.GRAY).content("上一页")
                        : Component.text().color(NamedTextColor.DARK_GREEN).content("上一页").clickEvent(ClickEvent.runCommand(lastPageCommand))
        );

        message.append(Component.text().color(NamedTextColor.DARK_GREEN).content(" " + current))
                .append(Component.text().color(NamedTextColor.GRAY).content("/"))
                .append(Component.text().color(NamedTextColor.DARK_GREEN).content(pages + " "));

        message.append(
                !hasNext()
                        ? Component.text().color(NamedTextColor.GRAY).content("下一页")
                        : Component.text().color(NamedTextColor.DARK_GREEN).content("下一页").clickEvent(ClickEvent.runCommand(nextPageCommand))
        );

        message.append(Component.text().color(NamedTextColor.DARK_GREEN).content(" >>----"));
        return message.asComponent();
    }

}

