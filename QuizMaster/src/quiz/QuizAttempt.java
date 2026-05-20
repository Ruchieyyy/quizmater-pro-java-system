package quiz;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QuizAttempt implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private int totalQuestions;
    private int correctAnswers;
    private int totalScore;       // Kahoot-style time-based score
    private long timeTakenMillis;
    private LocalDateTime attemptTime;
    private String category;

    public QuizAttempt(String username, int totalQuestions, int correctAnswers,
                       int totalScore, long timeTakenMillis, String category) {
        this.username = username;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.totalScore = totalScore;
        this.timeTakenMillis = timeTakenMillis;
        this.attemptTime = LocalDateTime.now();
        this.category = category;
    }

    public String getUsername() { return username; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getCorrectAnswers() { return correctAnswers; }
    public int getTotalScore() { return totalScore; }
    public long getTimeTakenMillis() { return timeTakenMillis; }
    public String getCategory() { return category; }
    public String getAccuracy() {
        if (totalQuestions == 0) return "0%";
        return String.format("%.1f%%", (correctAnswers * 100.0 / totalQuestions));
    }
    public String getAttemptTime() {
        return attemptTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}