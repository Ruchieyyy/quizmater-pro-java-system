package quiz;

import java.sql.*;
import java.util.*;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/quizmaster";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "1234"; // your MySQL password

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASS);
    }

    // ---- QUESTIONS ----

    public List<Question> getAllQuestions() {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapQuestion(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Question> getQuestionsByCategory(String category) {
        if (category.equals("All")) return getAllQuestions();
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE category = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapQuestion(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Set<String> getCategories() {
        Set<String> cats = new LinkedHashSet<>();
        cats.add("All");
        String sql = "SELECT DISTINCT category FROM questions";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) cats.add(rs.getString("category"));
        } catch (SQLException e) { e.printStackTrace(); }
        return cats;
    }

    public void addQuestion(Question q) {
        String sql = "INSERT INTO questions (question_text, option1, option2, option3, option4, correct_index, category, time_limit) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, q.getQuestionText());
            ps.setString(2, q.getOptions()[0]);
            ps.setString(3, q.getOptions()[1]);
            ps.setString(4, q.getOptions()[2]);
            ps.setString(5, q.getOptions()[3]);
            ps.setInt(6, q.getCorrectOptionIndex());
            ps.setString(7, q.getCategory());
            ps.setInt(8, q.getTimeLimit());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateQuestion(Question q) {
        String sql = "UPDATE questions SET question_text=?, option1=?, option2=?, option3=?, option4=?, correct_index=?, category=?, time_limit=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, q.getQuestionText());
            ps.setString(2, q.getOptions()[0]);
            ps.setString(3, q.getOptions()[1]);
            ps.setString(4, q.getOptions()[2]);
            ps.setString(5, q.getOptions()[3]);
            ps.setInt(6, q.getCorrectOptionIndex());
            ps.setString(7, q.getCategory());
            ps.setInt(8, q.getTimeLimit());
            ps.setInt(9, q.getId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteQuestion(int id) {
        String sql = "DELETE FROM questions WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public int getNextQuestionId() {
        // Auto-increment handles this, return 0 as placeholder
        return 0;
    }

    // ---- USERS ----

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new User(rs.getString("username"),
                rs.getString("password"), rs.getBoolean("is_admin"));
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean registerUser(String username, String password) {
        String check = "SELECT username FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(check)) {
            ps.setString(1, username);
            if (ps.executeQuery().next()) return false; // already exists
        } catch (SQLException e) { e.printStackTrace(); return false; }

        String sql = "INSERT INTO users (username, password, is_admin) VALUES (?, ?, FALSE)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                list.add(new User(rs.getString("username"),
                    rs.getString("password"), rs.getBoolean("is_admin")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ---- ATTEMPTS ----

    public void saveAttempt(QuizAttempt a) {
        String sql = "INSERT INTO quiz_attempts (username, total_questions, correct_answers, total_score, time_taken_ms, category) VALUES (?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getUsername());
            ps.setInt(2, a.getTotalQuestions());
            ps.setInt(3, a.getCorrectAnswers());
            ps.setInt(4, a.getTotalScore());
            ps.setLong(5, a.getTimeTakenMillis());
            ps.setString(6, a.getCategory());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<QuizAttempt> getAllAttempts() {
        List<QuizAttempt> list = new ArrayList<>();
        String sql = "SELECT * FROM quiz_attempts ORDER BY attempted_at DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                list.add(new QuizAttempt(
                    rs.getString("username"), rs.getInt("total_questions"),
                    rs.getInt("correct_answers"), rs.getInt("total_score"),
                    rs.getLong("time_taken_ms"), rs.getString("category")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<QuizAttempt> getAttemptsByUser(String username) {
        List<QuizAttempt> list = new ArrayList<>();
        String sql = "SELECT * FROM quiz_attempts WHERE username = ? ORDER BY attempted_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(new QuizAttempt(
                    rs.getString("username"), rs.getInt("total_questions"),
                    rs.getInt("correct_answers"), rs.getInt("total_score"),
                    rs.getLong("time_taken_ms"), rs.getString("category")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ---- HELPER ----

    private Question mapQuestion(ResultSet rs) throws SQLException {
        return new Question(
            rs.getInt("id"),
            rs.getString("question_text"),
            new String[]{rs.getString("option1"), rs.getString("option2"),
                         rs.getString("option3"), rs.getString("option4")},
            rs.getInt("correct_index"),
            rs.getString("category"),
            rs.getInt("time_limit")
        );
    }
}