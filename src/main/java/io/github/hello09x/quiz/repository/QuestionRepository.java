package io.github.hello09x.quiz.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.hello09x.quiz.Quiz;
import io.github.hello09x.quiz.repository.model.Question;
import io.github.tanyaofei.plugin.toolkit.database.AbstractRepository;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class QuestionRepository extends AbstractRepository<Question> {

    public final static QuestionRepository instance = new QuestionRepository(Quiz.getInstance());

    private final static Gson gson = new Gson();

    private final static TypeToken<List<String>> STRING_LIST_TYPE_TOKEN = new TypeToken<>() {
    };

    private LinkedList<Integer> cachedIds = new LinkedList<>();


    public QuestionRepository(Plugin plugin) {
        super(plugin);
    }

    public Integer insert(@NotNull Question question) {
        try (var stm = getConnection().prepareStatement("INSERT INTO question (title, answers) values (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stm.setObject(1, question.title());
            stm.setObject(2, gson.toJson(question.answers()));
            stm.executeUpdate();
            return stm.getGeneratedKeys().getInt(1);
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
        var success = updateAnswersById(id, answers);
        if (success > 0) {
            cachedIds.clear();
        }
        return success;
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
        var sql = "INSERT OR REPLACE INTO question (id, title, answers) VALUES ((SELECT id FROM question WHERE title = ?), ?, ?)";
        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, question.title());
            stm.setObject(2, question.title());
            stm.setObject(3, gson.toJson(question.answers()));
            var success = stm.executeUpdate();
            if (success > 0) {
                cachedIds.clear();
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
        if (this.cachedIds.isEmpty()) {
            synchronized (this) {
                this.cachedIds = selectIds();
                Collections.shuffle(cachedIds);
            }
        }

        if (this.cachedIds.isEmpty()) {
            return null;
        }

        return selectById(this.cachedIds.poll());
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
            this.cachedIds.clear();
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
                    gson.fromJson(rs.getString("answers"), STRING_LIST_TYPE_TOKEN));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
