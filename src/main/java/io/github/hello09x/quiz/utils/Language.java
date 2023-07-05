package io.github.hello09x.quiz.utils;


import net.kyori.adventure.text.Component;

import java.util.Map;

public class Language {

    public static Component text(String code) {
        var message = "TODO";
        return Component.text(message);
    }

    public static Component text(String code, Map<String, Object> args) {
        var message = "TODO";
        for (var arg : args.entrySet()) {
            message = message.replace(arg.getKey(), String.valueOf(arg.getValue()));
        }
        return Component.text(message);
    }

}
