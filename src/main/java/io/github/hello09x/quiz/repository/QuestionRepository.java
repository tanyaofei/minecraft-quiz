package io.github.hello09x.quiz.repository;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.hello09x.quiz.Quiz;
import io.github.hello09x.quiz.repository.model.Question;
import io.github.tanyaofei.plugin.toolkit.database.AbstractRepository;
import io.github.tanyaofei.plugin.toolkit.database.Page;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Logger;

public class QuestionRepository extends AbstractRepository<Question> {

    public final static QuestionRepository instance;

    private final static Logger log;
    private final static Gson gson;

    static {
        log = Quiz.getInstance().getLogger();
        gson = new Gson();
        instance = new QuestionRepository(Quiz.getInstance());
    }
    private final LinkedList<Integer> queue = new LinkedList<>();

    public QuestionRepository(Plugin plugin) {
        super(plugin);

        if (requeueFromCacheFile()) {
            log.info("已加载缓存出题表");
        } else {
            requeue();
        }
    }

    public Integer insert(@NotNull Question question) {
        try (var stm = getConnection().prepareStatement("INSERT INTO question (title, answers) values (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stm.setObject(1, question.title());
            stm.setObject(2, gson.toJson(question.answers()));
            stm.executeUpdate();
            var id = stm.getGeneratedKeys().getInt(1);
            synchronized (this.queue) {
                this.queue.add(id);
            }
            return id;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Nullable
    public Question selectByTitle(@NotNull String title) {
        var sql = "SELECT * FROM question WHERE title = ?";
        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, title);
            return mapOne(stm.executeQuery());
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public @NotNull Page<Question> selectQueuePage(int page, int size) {
        if (size < 1) {
            size = 10;
        }

        List<Integer> ids;
        int total;
        int pages;
        synchronized (this.queue) {
            total = this.queue.size();
            if (total == 0) {
                return Page.emptyPage();
            }

            var from = Math.min((page - 1) * size, total - 1);
            var to = Math.min(from + size, total);
            pages = (int) Math.ceil((double) total / size);
            page = Math.min(page, pages);
            ids = this.queue.subList(from, to);
        }

        var data = selectByIds(ids);

        data.sort((a, b) -> {
            var i1 = ids.indexOf(a.id());
            var i2 = ids.indexOf(b.id());

            if (i1 != -1) {
                i1 = ids.size() - i1;
            }
            if (i2 != -1) {
                i2 = ids.size() - i2;
            }
            return i2 - i1;

        });

        return new Page<>(
                data,
                total,
                pages,
                page,
                size
        );
    }

    public @NotNull List<Question> selectByIds(@NotNull List<Integer> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        var p = new String[ids.size()];
        Arrays.fill(p, "?");

        var sql = String.format("""
                SELECT *
                 FROM question
                 WHERE id in ( %s )
                """, StringUtils.join(p, ","));

        try (var stm = getConnection().prepareStatement(sql)) {
            var itr = ids.listIterator();
            while (itr.hasNext()) {
                stm.setObject(itr.nextIndex() + 1, itr.next());
            }
            return mapMany(stm.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Question> selectListByTitleLike(@NotNull String title, int limit) {
        var sql = "SELECT * FROM question WHERE title LIKE ? LIMIT ?";
        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, "%" + title + "%");
            stm.setObject(2, limit);
            return mapMany(stm.executeQuery());
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public int deleteAnswerById(@NotNull Integer id, @Nullable Integer i) {
        if (i == null) {
            return this.updateAnswersById(id, Collections.emptyList());
        }

        var question = selectById(id);
        if (question == null) {
            return 0;
        }

        var answers = question.answers();
        answers.remove((int) i);
        return updateAnswersById(id, answers);
    }

    public int updateAnswersById(@NotNull Integer id, @NotNull List<String> answers) {
        try (var stm = getConnection().prepareStatement("UPDATE question set answers = ? WHERE id = ?")) {
            stm.setObject(1, gson.toJson(answers));
            stm.setObject(2, id);
            return stm.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public int addAnswerById(@NotNull Integer id, @NotNull String answer) {
        var question = selectById(id);
        if (question == null) {
            return 0;
        }

        var answers = new HashSet<String>(question.answers().size() + 1);
        answers.addAll(question.answers());
        answers.add(answer);
        return updateAnswersById(id, new ArrayList<>(answers));
    }

    public int insertOrUpdateByTitle(@NotNull Question question) {
        var sql = """
                INSERT OR REPLACE INTO question (
                    id,
                    title,
                    answers
                ) VALUES (
                    (SELECT id FROM question WHERE title = ?),
                     ?,
                     ?
                )
                """;
        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, question.title());
            stm.setObject(2, question.title());
            stm.setObject(3, gson.toJson(question.answers()));
            var success = stm.executeUpdate();
            if (success > 0) {
                var id = Optional
                        .ofNullable(selectByTitle(question.title()))
                        .map(Question::id)
                        .orElse(null);
                synchronized (queue) {
                    if (!queue.contains(id)) {
                        queue.add(id);
                    }
                }
            }
            return success;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected void initTables() throws SQLException {
        try (var stm = getConnection().createStatement()) {
            stm.execute("""
                    create table if not exists question
                    (
                        id      integer not null
                            primary key autoincrement,
                        title   text    unique not null,
                        answers text    not null
                    );
                    """);
        }
    }

    @Override
    public @Nullable Question selectRandomly() {
        if (refillQueue()) {
            log.info("已刷新题目清单");
        }

        if (this.queue.isEmpty()) {
            return null;
        }

        var question = selectById(this.queue.poll());
        if (question == null) {
            this.queue.clear();
        }
        return question;
    }

    public @NotNull LinkedList<Integer> selectIds() {
        try (var stm = getConnection().prepareStatement("select id from question")) {
            var ret = new LinkedList<Integer>();
            var rs = stm.executeQuery();
            while (rs.next()) {
                ret.add(rs.getInt("id"));
            }
            return ret;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int deleteById(@NotNull Serializable id) {
        var success = super.deleteById(id);
        if (success > 0) {
            this.queue.removeIf(i -> i.equals(id));
        }
        return success;
    }

    protected Question mapOne(@NotNull ResultSet rs) {
        try {
            if (!rs.next()) {
                return null;
            }
            return new Question(
                    rs.getInt("id"),
                    rs.getString("title"),
                    gson.fromJson(rs.getString("answers"), new TypeToken<>() {
                    }));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean requeueFromCacheFile() {
        var file = new File(Quiz.getInstance().getDataFolder(), "queue.json");
        if (!file.exists()) {
            return false;
        }

        LinkedList<Integer> queue;
        try (var in = new FileReader(file)) {
            queue = gson.fromJson(in, new TypeToken<>() {
            });
        } catch (IOException e) {
            log.warning("无法读取出题表缓存文件\n" + Throwables.getStackTraceAsString(e));
            return false;
        }

        if (queue.isEmpty()) {
            return false;
        }
        synchronized (this.queue) {
            this.queue.clear();
            this.queue.addAll(queue);
        }
        return true;
    }

    private boolean refillQueue() {
        if (this.queue.isEmpty()) {
            synchronized (this.queue) {
                if (this.queue.isEmpty()) {
                    queue.addAll(selectIds());
                    Collections.shuffle(queue);
                    return true;
                }
            }
        }
        return false;
    }

    public void requeue() {
        synchronized (this.queue) {
            this.queue.clear();
            refillQueue();
        }
    }

    public void cacheQueue() {
        var folder = Quiz.getInstance().getDataFolder();
        if (!folder.exists() && !folder.mkdirs()) {
            log.warning("无法创建插件数据目录");
            return;
        }

        var file = new File(folder, "queue.json");
        try (var out = new FileWriter(file)) {
            synchronized (this.queue) {
                out.write(gson.toJson(this.queue));
            }
        } catch (IOException e) {
            log.warning("无法缓存出题表\n" + Throwables.getStackTraceAsString(e));
        }
    }

}
