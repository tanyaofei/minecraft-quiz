package io.github.hello09x.quiz.command.question;

import com.google.common.base.Throwables;
import io.github.hello09x.quiz.Quiz;
import io.github.hello09x.quiz.repository.QuestionRepository;
import io.github.hello09x.quiz.repository.model.Question;
import io.github.hello09x.quiz.utils.command.ExecutableCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.csv.CSVFormat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.codehaus.plexus.util.IOUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class QuestionImportCommand extends ExecutableCommand {


    public final static QuestionImportCommand instance;
    private final static Logger log;
    private final static int PREVIEW_ROWS = 10;

    static {
        log = Quiz.getInstance().getLogger();
        instance = new QuestionImportCommand();
    }

    private final QuestionRepository repository = QuestionRepository.instance;

    public QuestionImportCommand() {
        var folder = Quiz.getInstance().getDataFolder();
        if (!folder.exists() && !folder.mkdirs()) {
            throw new ExceptionInInitializerError("无法创建插件配置目录");
        }

        var file = new File(folder, "import-template.csv");
        if (file.exists()) {
            return;
        }

        try (var in = Quiz.class.getClassLoader().getResource("import-template.csv").openStream()) {
            IOUtil.copy(in, new FileWriter(file));
            log.info("已创建导入模版: " + file.getPath());
        } catch (IOException e) {
            throw new UncheckedIOException("生成导入模版文件失败", e);
        }
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("quizadmin.*");
    }

    @Override
    public @NotNull Component getHelp() {
        return Component.textOfChildren(
                Component.text("从插件目录下导入题库\n", NamedTextColor.YELLOW),
                Component.text("用法: ", NamedTextColor.GOLD), Component.text("/quizadmin question import <CSV 文件名>\n"),
                Component.text("例子: ", NamedTextColor.GOLD), Component.text("/quizadmin question import question.csv\n"),
                Component.text("注意: ", NamedTextColor.GOLD), Component.text("CSV 文件必须位于插件的数据目录"));
    }

    @Override
    protected boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        return this.selectFile(sender, args) || confirm(sender, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length != 1) {
            return null;
        }

        var files = Quiz
                .getInstance()
                .getDataFolder()
                .listFiles(f -> {
                    String filename;
                    return f.isFile()
                            && (filename = f.getName()).endsWith(".csv")
                            && (args[0].isBlank() || filename.startsWith(args[0]));
                });

        if (files == null || files.length == 0) {
            return null;
        }

        return Arrays.stream(files).map(File::getName).collect(Collectors.toList());
    }


    private boolean selectFile(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 1) {
            return false;
        }

        var filename = args[0];
        if (args[0].isBlank()) {
            return false;
        }

        if (!filename.endsWith(".csv")) {
            sender.sendMessage(Component.text("只能导入 csv 文件", NamedTextColor.RED));
            return true;
        }

        var file = new File(Quiz.getInstance().getDataFolder(), filename);
        if (!file.exists() || !file.isFile()) {
            sender.sendMessage(Component.text("文件不存在: " + filename, NamedTextColor.RED));
            return true;
        }

        try (var in = new FileReader(file)) {
            var rows = CSVFormat.EXCEL.parse(in);
            int i = PREVIEW_ROWS;
            var message = Component.text("____/ 预览 \\____\n", NamedTextColor.YELLOW);
            for (var row : rows) {
                if (i == 0) {
                    message = message.append(Component.text("    ...    \n", NamedTextColor.GRAY));
                    break;
                }
                if (i == PREVIEW_ROWS) {
                    i--;
                    continue;
                }

                var n = row.getRecordNumber();
                if (row.size() != 2) {
                    sender.sendMessage(Component.text(String.format("每一行应当为两列, 第 %d 行不符合规范", n), NamedTextColor.RED));
                    return true;
                }

                message = message.append(Component.textOfChildren(
                        Component.text((n - 1) + ". ", NamedTextColor.DARK_GREEN),
                        Component.text(row.get(0), NamedTextColor.WHITE),
                        Component.text(" - ", NamedTextColor.GRAY),
                        Component.text(String.join("; ", parseAnswers(row.get(1))), NamedTextColor.DARK_GRAY),
                        Component.newline()
                ));
                i--;
            }

            message = message.append(Component.textOfChildren(
                    Component.text("---->> ", NamedTextColor.DARK_GRAY),
                    Component.text("确定").style(Style.style(NamedTextColor.DARK_GREEN, TextDecoration.UNDERLINED)).clickEvent(ClickEvent.runCommand("/quizadmin question import " + filename + " --confirm")),
                    Component.text(" <<----", NamedTextColor.DARK_GRAY)
            ));

            sender.sendMessage(message);
            return true;

        } catch (FileNotFoundException e) {
            sender.sendMessage(Component.text("文件不存在: " + file.getPath(), NamedTextColor.RED));
            return true;
        } catch (IOException e) {
            log.warning(Throwables.getStackTraceAsString(e));
            sender.sendMessage(Component.text("读取文件失败: " + file.getPath(), NamedTextColor.RED));
            return true;
        }

    }

    private boolean confirm(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 2 || !args[1].equals("--confirm")) {
            return false;
        }

        var filename = args[0];
        if (args[0].isBlank()) {
            return false;
        }

        if (!filename.endsWith(".csv")) {
            sender.sendMessage(Component.text("只能导入 csv 文件", NamedTextColor.RED));
            return true;
        }

        var file = new File(Quiz.getInstance().getDataFolder(), filename);

        CompletableFuture.runAsync(() -> {
            var startedAt = LocalDateTime.now();
            try (var in = new FileReader(file)) {
                var rows = CSVFormat.EXCEL.parse(in);
                var success = 0;
                var failed = 0;
                for (var row : rows) {
                    try {
                        if (row.getRecordNumber() == 1) {
                            // 跳过标题行
                            continue;
                        }
                        if (row.size() != 2) {
                            failed++;
                            continue;
                        }

                        var title = row.get(0);
                        var answers = parseAnswers(row.get(1));
                        var question = new Question(
                                null,
                                title,
                                answers
                        );

                        repository.insertOrUpdateByTitle(question);
                        success++;
                    } catch (Throwable e) {
                        log.warning(String.format("导入第 %d 行失败\n%s", row.getRecordNumber(), Throwables.getStackTraceAsString(e)));
                        failed++;
                    }
                }

                var finishedAt = LocalDateTime.now();
                var message = Component.textOfChildren(
                        Component.text("题库导入完毕, 成功 ", NamedTextColor.GREEN),
                        Component.text(success, NamedTextColor.DARK_GREEN),
                        Component.text(" 条", NamedTextColor.GREEN),
                        Component.text(", 失败 "),
                        Component.text(failed, NamedTextColor.RED),
                        Component.text(" 条", NamedTextColor.GREEN),
                        Component.text(String.format(", 共耗时 %d 秒", Duration.between(startedAt, finishedAt).toSeconds()), NamedTextColor.GREEN)
                );
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        sender.sendMessage(message);
                    }
                }.runTask(Quiz.getInstance());

            } catch (FileNotFoundException e) {
                sender.sendMessage(Component.text("导入失败, 文件不存在: " + file.getPath(), NamedTextColor.RED));
            } catch (IOException e) {
                log.warning(Throwables.getStackTraceAsString(e));
                sender.sendMessage(Component.text("导入失败, 读取文件失败: " + file.getPath(), NamedTextColor.RED));
            }
        });

        sender.sendMessage(Component.text("开始导入...", NamedTextColor.GREEN));
        return true;
    }

    private List<String> parseAnswers(String compacted) {
        return Arrays.asList(compacted.split(";\\s?"));
    }

}
