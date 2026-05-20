package quiz;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private DatabaseManager db;
    private User currentUser;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Panel names
    public static final String LOGIN_PANEL = "LOGIN";
    public static final String ADMIN_PANEL = "ADMIN";
    public static final String QUIZ_PANEL = "QUIZ";
    public static final String LEADERBOARD_PANEL = "LEADERBOARD";

    public MainFrame() {
        db = new DatabaseManager();
        setupFrame();
        showLoginPanel();
    }

    private void setupFrame() {
        setTitle("QuizMaster Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(15, 15, 30));
        add(mainPanel);
    }

    public void showLoginPanel() {
        mainPanel.removeAll();
        LoginPanel loginPanel = new LoginPanel(this, db);
        mainPanel.add(loginPanel, LOGIN_PANEL);
        cardLayout.show(mainPanel, LOGIN_PANEL);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void showAdminPanel() {
        mainPanel.removeAll();
        AdminPanel adminPanel = new AdminPanel(this, db);
        mainPanel.add(adminPanel, ADMIN_PANEL);
        cardLayout.show(mainPanel, ADMIN_PANEL);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void showQuizPanel() {
        mainPanel.removeAll();
        QuizPanel quizPanel = new QuizPanel(this, db, currentUser);
        mainPanel.add(quizPanel, QUIZ_PANEL);
        cardLayout.show(mainPanel, QUIZ_PANEL);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void showLeaderboard() {
        mainPanel.removeAll();
        LeaderboardPanel lb = new LeaderboardPanel(this, db, currentUser);
        mainPanel.add(lb, LEADERBOARD_PANEL);
        cardLayout.show(mainPanel, LEADERBOARD_PANEL);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void setCurrentUser(User user) { this.currentUser = user; }
    public User getCurrentUser() { return currentUser; }
    public DatabaseManager getDb() { return db; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new MainFrame().setVisible(true);
        });
    }
}