package io.github.hello09x.quiz.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.hello09x.quiz.repository.model.Question;
import io.github.hello09x.quiz.utils.database.AbstractRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class QuestionRepository extends AbstractRepository<Question> {

    public final static QuestionRepository instance = new QuestionRepository();

    private final static Gson gson = new Gson();

    private final static TypeToken<List<String>> STRING_LIST_TYPE_TOKEN = new TypeToken<>() {
    };

    public Integer insert(Question question) {
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
        return updateAnswersById(id, answers);
    }

    public int updateAnswersById(Integer id, List<String> answers) {
        try (var stm = getConnection().prepareStatement("UPDATE question set answers = ? WHERE id = ?")) {
            stm.setObject(1, gson.toJson(answers));
            stm.setObject(2, id);
            return stm.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public int addAnswerById(Integer id, String answer) {
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
        var sql = "INSERT OR REPLACE INTO question (id, title, answers) values ((SELECT id FROM question WHERE title = ?), ?, ?)";
        try (var stm = getConnection().prepareStatement(sql)) {
            stm.setObject(1, question.title());
            stm.setObject(2, question.title());
            stm.setObject(3, gson.toJson(question.answers()));
            return stm.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected void createTableIfNotExists() throws SQLException {
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
