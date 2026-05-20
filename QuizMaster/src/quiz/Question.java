package quiz;

import java.io.Serializable;

public class Question implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String questionText;
    private String[] options; // Always 4 options
    private int correctOptionIndex; // 0-3
    private String category;
    private int timeLimit; // seconds

    public Question(int id, String questionText, String[] options, 
                    int correctOptionIndex, String category, int timeLimit) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.category = category;
        this.timeLimit = timeLimit;
    }

    public int getId() { return id; }
    public String getQuestionText() { return questionText; }
    public String[] getOptions() { return options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public String getCategory() { return category; }
    public int getTimeLimit() { return timeLimit; }

    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setOptions(String[] options) { this.options = options; }
    public void setCorrectOptionIndex(int correctOptionIndex) { this.correctOptionIndex = correctOptionIndex; }
    public void setCategory(String category) { this.category = category; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }

    @Override
    public String toString() {
        return "[" + id + "] " + questionText;
    }
}