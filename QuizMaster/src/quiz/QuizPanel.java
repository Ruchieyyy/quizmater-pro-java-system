package quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class QuizPanel extends JPanel {
    private MainFrame frame;
    private DatabaseManager db;
    private User user;

    // Quiz state
    private List<Question> questions;
    private int currentIndex = 0;
    private int totalScore = 0;
    private int correctCount = 0;
    private long quizStartTime;
    private long questionStartTime;
    private javax.swing.Timer countdownTimer;
    private int timeLeft;
    private boolean answered = false;

    // UI
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel setupPanel;
    private JPanel quizPane;
    private JPanel resultPanel;

    private JLabel questionNumLabel;
    private JLabel questionTextLabel;
    private JButton[] optionBtns = new JButton[4];
    private JLabel timerLabel;
    private JLabel scoreLabel;
    private JProgressBar timerBar;

    // Colors
    private static final Color BG = new Color(10, 10, 25);
    private static final Color ACCENT = new Color(0, 212, 255);
    private static final Color CORRECT = new Color(0, 200, 100);
    private static final Color WRONG = new Color(255, 60, 80);
    private static final Color TEXT = new Color(220, 220, 240);

    private static final Color[] OPTION_COLORS = {
        new Color(230, 30, 100),   // Red-pink
        new Color(20, 120, 220),   // Blue
        new Color(220, 140, 0),    // Orange
        new Color(30, 170, 90)     // Green
    };

    public QuizPanel(MainFrame frame, DatabaseManager db, User user) {
        this.frame = frame;
        this.db = db;
        this.user = user;
        setBackground(BG);
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(BG);
        buildSetupPanel();
        buildQuizPane();
        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, "SETUP");
    }

    // ---- Setup Screen ----
    private void buildSetupPanel() {
        setupPanel = new JPanel(new GridBagLayout());
        setupPanel.setBackground(BG);

        JPanel inner = new JPanel();
        inner.setBackground(new Color(18, 18, 42));
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 2),
            BorderFactory.createEmptyBorder(35, 45, 35, 45)
        ));
        inner.setPreferredSize(new Dimension(420, 380));

        JLabel title = new JLabel("🎮 Start Quiz", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcome = new JLabel("Welcome, " + user.getUsername() + "!", SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcome.setForeground(new Color(150, 150, 190));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel catLabel = new JLabel("SELECT CATEGORY");
        catLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        catLabel.setForeground(new Color(120, 120, 160));
        catLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Set<String> cats = db.getCategories();
        JComboBox<String> catCombo = new JComboBox<>(cats.toArray(new String[0]));
        catCombo.setBackground(new Color(14, 14, 35));
        catCombo.setForeground(TEXT);
        catCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        catCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JLabel countLabel = new JLabel("NUMBER OF QUESTIONS");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        countLabel.setForeground(new Color(120, 120, 160));
        countLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSpinner countSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));
        countSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JButton startBtn = new JButton("START QUIZ ▶");
        startBtn.setBackground(ACCENT);
        startBtn.setForeground(BG);
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        startBtn.setFocusPainted(false);
        startBtn.setBorderPainted(false);
        startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton logoutBtn = new JButton("← Logout");
        logoutBtn.setBackground(new Color(18, 18, 42));
        logoutBtn.setForeground(new Color(100, 100, 140));
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> frame.showLoginPanel());

        JButton lbBtn = new JButton("🏆 Leaderboard");
        lbBtn.setBackground(new Color(18, 18, 42));
        lbBtn.setForeground(new Color(255, 200, 0));
        lbBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbBtn.setFocusPainted(false);
        lbBtn.setBorderPainted(false);
        lbBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbBtn.addActionListener(e -> frame.showLeaderboard());

        startBtn.addActionListener(e -> {
            String selectedCat = (String) catCombo.getSelectedItem();
            int count = (int) countSpinner.getValue();
            List<Question> pool = db.getQuestionsByCategory(selectedCat);
            if (pool.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No questions in this category.");
                return;
            }
            Collections.shuffle(pool);
            questions = pool.subList(0, Math.min(count, pool.size()));
            startQuiz();
        });

        inner.add(title);
        inner.add(Box.createVerticalStrut(6));
        inner.add(welcome);
        inner.add(Box.createVerticalStrut(25));
        inner.add(catLabel);
        inner.add(Box.createVerticalStrut(6));
        inner.add(catCombo);
        inner.add(Box.createVerticalStrut(14));
        inner.add(countLabel);
        inner.add(Box.createVerticalStrut(6));
        inner.add(countSpinner);
        inner.add(Box.createVerticalStrut(22));
        inner.add(startBtn);
        inner.add(Box.createVerticalStrut(10));
        inner.add(lbBtn);
        inner.add(Box.createVerticalStrut(6));
        inner.add(logoutBtn);

        setupPanel.add(inner);
        cardPanel.add(setupPanel, "SETUP");
    }

    // ---- Quiz Screen ----
    private void buildQuizPane() {
        quizPane = new JPanel(new BorderLayout(0, 0));
        quizPane.setBackground(BG);

        // Top HUD
        JPanel hud = new JPanel(new BorderLayout());
        hud.setBackground(new Color(14, 14, 35));
        hud.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        questionNumLabel = new JLabel("Question 1/5");
        questionNumLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        questionNumLabel.setForeground(new Color(150, 150, 200));

        timerLabel = new JLabel("⏱ 20");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        timerLabel.setForeground(ACCENT);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        scoreLabel.setForeground(new Color(255, 200, 0));

        hud.add(questionNumLabel, BorderLayout.WEST);
        hud.add(timerLabel, BorderLayout.CENTER);
        hud.add(scoreLabel, BorderLayout.EAST);
        ((JLabel) hud.getComponent(1)).setHorizontalAlignment(SwingConstants.CENTER);

        // Timer bar
        timerBar = new JProgressBar(0, 100);
        timerBar.setForeground(ACCENT);
        timerBar.setBackground(new Color(25, 25, 50));
        timerBar.setBorderPainted(false);
        timerBar.setPreferredSize(new Dimension(0, 6));

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(new Color(14, 14, 35));
        topSection.add(hud, BorderLayout.CENTER);
        topSection.add(timerBar, BorderLayout.SOUTH);

        // Question text
        questionTextLabel = new JLabel("", SwingConstants.CENTER);
        questionTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        questionTextLabel.setForeground(TEXT);
        questionTextLabel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));
        questionTextLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Options Grid (2x2 like Kahoot)
        JPanel optionsGrid = new JPanel(new GridLayout(2, 2, 12, 12));
        optionsGrid.setBackground(BG);
        optionsGrid.setBorder(BorderFactory.createEmptyBorder(10, 20, 25, 20));

        String[] shapes = {"▲", "●", "♦", "■"};
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            optionBtns[i] = new JButton();
            optionBtns[i].setBackground(OPTION_COLORS[i]);
            optionBtns[i].setForeground(Color.WHITE);
            optionBtns[i].setFont(new Font("Segoe UI", Font.BOLD, 14));
            optionBtns[i].setFocusPainted(false);
            optionBtns[i].setBorderPainted(false);
            optionBtns[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            optionBtns[i].addActionListener(e -> handleAnswer(idx));
            optionsGrid.add(optionBtns[i]);
        }

        quizPane.add(topSection, BorderLayout.NORTH);
        quizPane.add(questionTextLabel, BorderLayout.CENTER);
        quizPane.add(optionsGrid, BorderLayout.SOUTH);

        cardPanel.add(quizPane, "QUIZ");
    }

    private void startQuiz() {
        currentIndex = 0;
        totalScore = 0;
        correctCount = 0;
        quizStartTime = System.currentTimeMillis();
        cardLayout.show(cardPanel, "QUIZ");
        loadQuestion();
    }

    private void loadQuestion() {
        if (currentIndex >= questions.size()) {
            endQuiz();
            return;
        }
        answered = false;
        Question q = questions.get(currentIndex);
        questionNumLabel.setText("Question " + (currentIndex + 1) + " / " + questions.size());
        questionTextLabel.setText("<html><div style='text-align:center'>" + q.getQuestionText() + "</div></html>");
        scoreLabel.setText("Score: " + totalScore);

        String[] opts = q.getOptions();
        String[] shapes = {"▲  ", "●  ", "♦  ", "■  "};
        for (int i = 0; i < 4; i++) {
            optionBtns[i].setText(shapes[i] + opts[i]);
            optionBtns[i].setBackground(OPTION_COLORS[i]);
            optionBtns[i].setEnabled(true);
        }

        // Timer
        timeLeft = q.getTimeLimit();
        timerBar.setMaximum(timeLeft);
        timerBar.setValue(timeLeft);
        timerLabel.setForeground(ACCENT);
        questionStartTime = System.currentTimeMillis();

        if (countdownTimer != null) countdownTimer.stop();
        countdownTimer = new javax.swing.Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("⏱ " + timeLeft);
            timerBar.setValue(timeLeft);
            double ratio = (double) timeLeft / q.getTimeLimit();
            if (ratio < 0.3) timerLabel.setForeground(WRONG);
            else if (ratio < 0.6) timerLabel.setForeground(new Color(255, 165, 0));
            if (timeLeft <= 0) {
                countdownTimer.stop();
                if (!answered) showTimeUpAndNext();
            }
        });
        countdownTimer.start();
    }

    private void handleAnswer(int selectedIdx) {
        if (answered) return;
        answered = true;
        if (countdownTimer != null) countdownTimer.stop();

        Question q = questions.get(currentIndex);
        long responseTimeMs = System.currentTimeMillis() - questionStartTime;
        boolean isCorrect = selectedIdx == q.getCorrectOptionIndex();

        // Kahoot scoring: max 1000 pts, decreasing with time
        if (isCorrect) {
            int maxPoints = 1000;
            double timeFraction = Math.max(0, 1.0 - (responseTimeMs / (q.getTimeLimit() * 1000.0)));
            int points = (int) (maxPoints * (0.5 + 0.5 * timeFraction));
            totalScore += points;
            correctCount++;
        }

        // Reveal colors
        for (int i = 0; i < 4; i++) {
            optionBtns[i].setEnabled(false);
            if (i == q.getCorrectOptionIndex()) optionBtns[i].setBackground(CORRECT);
            else if (i == selectedIdx && !isCorrect) optionBtns[i].setBackground(WRONG);
            else optionBtns[i].setBackground(new Color(50, 50, 70));
        }

        scoreLabel.setText("Score: " + totalScore);

        // Brief pause then next
        javax.swing.Timer pause = new javax.swing.Timer(1200, e2 -> {
            currentIndex++;
            loadQuestion();
        });
        pause.setRepeats(false);
        pause.start();
    }

    private void showTimeUpAndNext() {
        Question q = questions.get(currentIndex);
        answered = true;
        for (int i = 0; i < 4; i++) {
            optionBtns[i].setEnabled(false);
            optionBtns[i].setBackground(i == q.getCorrectOptionIndex() ? CORRECT : new Color(50, 50, 70));
        }
        timerLabel.setText("⏱ 0");
        javax.swing.Timer pause = new javax.swing.Timer(1200, e -> {
            currentIndex++;
            loadQuestion();
        });
        pause.setRepeats(false);
        pause.start();
    }

    private void endQuiz() {
        if (countdownTimer != null) countdownTimer.stop();
        long totalTime = System.currentTimeMillis() - quizStartTime;

        String cat = questions.isEmpty() ? "N/A" : questions.get(0).getCategory();
        QuizAttempt attempt = new QuizAttempt(user.getUsername(), questions.size(),
            correctCount, totalScore, totalTime, cat);
        db.saveAttempt(attempt);

        showResultScreen(attempt, totalTime);
    }

    private void showResultScreen(QuizAttempt attempt, long totalTime) {
        if (cardPanel.getComponentCount() > 2) {
            cardPanel.remove(cardPanel.getComponentCount() - 1);
        }

        JPanel resultPane = new JPanel(new GridBagLayout());
        resultPane.setBackground(BG);

        JPanel card = new JPanel();
        card.setBackground(new Color(18, 18, 42));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 2),
            BorderFactory.createEmptyBorder(35, 50, 35, 50)
        ));
        card.setPreferredSize(new Dimension(450, 440));

        JLabel title = new JLabel(correctCount >= questions.size() * 0.7 ? "🎉 GREAT JOB!" : "📊 QUIZ COMPLETE", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLbl = new JLabel("SCORE: " + attempt.getTotalScore(), SwingConstants.CENTER);
        scoreLbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
        scoreLbl.setForeground(new Color(255, 200, 0));
        scoreLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        String grade = attempt.getTotalScore() >= 800 ? "⭐⭐⭐" : attempt.getTotalScore() >= 500 ? "⭐⭐" : "⭐";
        JLabel gradeLbl = new JLabel(grade, SwingConstants.CENTER);
        gradeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        gradeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        addStat(card, "✅ Correct", attempt.getCorrectAnswers() + " / " + attempt.getTotalQuestions());
        addStat(card, "📈 Accuracy", attempt.getAccuracy());
        addStat(card, "⏱ Time Taken", (totalTime / 1000) + " seconds");

        JButton playAgainBtn = makeResultButton("▶  PLAY AGAIN", ACCENT, BG);
        playAgainBtn.addActionListener(e -> { cardLayout.show(cardPanel, "SETUP"); });

        JButton lbBtn = makeResultButton("🏆  LEADERBOARD", new Color(255, 200, 0), BG);
        lbBtn.addActionListener(e -> frame.showLeaderboard());

        JButton logoutBtn = makeResultButton("← LOGOUT", new Color(30, 30, 60), new Color(120, 120, 160));
        logoutBtn.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 120)));
        logoutBtn.addActionListener(e -> frame.showLoginPanel());

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(scoreLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(gradeLbl);
        card.add(Box.createVerticalStrut(18));
        card.add(playAgainBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(lbBtn);
        card.add(Box.createVerticalStrut(6));
        card.add(logoutBtn);

        resultPane.add(card);
        cardPanel.add(resultPane, "RESULT");
        cardLayout.show(cardPanel, "RESULT");
    }

    private void addStat(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(22, 22, 48));
        row.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(140, 140, 180));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 13));
        val.setForeground(new Color(220, 220, 240));

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);

        panel.add(Box.createVerticalStrut(5));
        panel.add(row);
    }

    private JButton makeResultButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }
}