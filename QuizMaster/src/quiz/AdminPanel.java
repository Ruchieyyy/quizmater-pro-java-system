package quiz;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {
    private MainFrame frame;
    private DatabaseManager db;

    private static final Color BG = new Color(10, 10, 25);
    private static final Color CARD = new Color(18, 18, 40);
    private static final Color ACCENT = new Color(0, 212, 255);
    private static final Color ACCENT2 = new Color(255, 65, 125);
    private static final Color TEXT = new Color(220, 220, 240);
    private static final Color MUTED = new Color(100, 100, 140);

    private JTable questionTable;
    private DefaultTableModel questionModel;
    private JTable logsTable;
    private DefaultTableModel logsModel;

    public AdminPanel(MainFrame frame, DatabaseManager db) {
        this.frame = frame;
        this.db = db;
        setBackground(BG);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(15, 15, 35));
        topBar.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("⚡ Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ACCENT);

        JLabel userInfo = new JLabel("Logged in as: " + frame.getCurrentUser().getUsername() + "  |  ");
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userInfo.setForeground(MUTED);

        JButton logoutBtn = makeSmallButton("LOGOUT", ACCENT2);
        logoutBtn.addActionListener(e -> frame.showLoginPanel());

        JButton viewLBBtn = makeSmallButton("LEADERBOARD", ACCENT);
        viewLBBtn.addActionListener(e -> frame.showLeaderboard());

        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightBar.setOpaque(false);
        rightBar.add(userInfo);
        rightBar.add(viewLBBtn);
        rightBar.add(logoutBtn);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(rightBar, BorderLayout.EAST);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(CARD);
        tabs.setForeground(TEXT);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("📋 Questions", buildQuestionsTab());
        tabs.addTab("📊 User Logs", buildLogsTab());
        tabs.addTab("👥 Users", buildUsersTab());

        add(topBar, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildQuestionsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] cols = {"ID", "Question", "Category", "Correct Answer", "Time (s)"};
        questionModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        questionTable = new JTable(questionModel);
        styleTable(questionTable);
        questionTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        questionTable.getColumnModel().getColumn(1).setPreferredWidth(350);
        questionTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        questionTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        questionTable.getColumnModel().getColumn(4).setPreferredWidth(60);

        refreshQuestions();

        JScrollPane scroll = new JScrollPane(questionTable);
        scroll.getViewport().setBackground(new Color(14, 14, 32));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(30, 30, 60)));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(BG);

        JButton addBtn = makeButton("+ ADD QUESTION", new Color(0, 180, 100), Color.WHITE);
        JButton editBtn = makeButton("✏ EDIT", ACCENT, BG);
        JButton deleteBtn = makeButton("🗑 DELETE", ACCENT2, Color.WHITE);

        addBtn.addActionListener(e -> showAddQuestionDialog());
        editBtn.addActionListener(e -> showEditQuestionDialog());
        deleteBtn.addActionListener(e -> deleteSelectedQuestion());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildLogsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] cols = {"Username", "Category", "Questions", "Correct", "Score", "Accuracy", "Time", "Attempted At"};
        logsModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        logsTable = new JTable(logsModel);
        styleTable(logsTable);
        refreshLogs();

        JScrollPane scroll = new JScrollPane(logsTable);
        scroll.getViewport().setBackground(new Color(14, 14, 32));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(30, 30, 60)));

        JButton refreshBtn = makeButton("↻ REFRESH", ACCENT, BG);
        refreshBtn.addActionListener(e -> refreshLogs());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(BG);
        btnPanel.add(refreshBtn);

        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildUsersTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] cols = {"Username", "Role", "Registered At"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        styleTable(table);

        for (User u : db.getAllUsers()) {
            model.addRow(new Object[]{
                u.getUsername(),
                u.isAdmin() ? "Admin" : "Student",
                u.getRegisteredAt()
            });
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(14, 14, 32));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(30, 30, 60)));

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void refreshQuestions() {
        questionModel.setRowCount(0);
        for (Question q : db.getAllQuestions()) {
            questionModel.addRow(new Object[]{
                q.getId(),
                q.getQuestionText(),
                q.getCategory(),
                q.getOptions()[q.getCorrectOptionIndex()],
                q.getTimeLimit()
            });
        }
    }

    private void refreshLogs() {
        logsModel.setRowCount(0);
        List<QuizAttempt> attempts = db.getAllAttempts();
        for (int i = attempts.size() - 1; i >= 0; i--) {
            QuizAttempt a = attempts.get(i);
            logsModel.addRow(new Object[]{
                a.getUsername(), a.getCategory(), a.getTotalQuestions(),
                a.getCorrectAnswers(), a.getTotalScore(), a.getAccuracy(),
                (a.getTimeTakenMillis() / 1000) + "s", a.getAttemptTime()
            });
        }
    }

    private void showAddQuestionDialog() {
        QuestionDialog dialog = new QuestionDialog(frame, null, db);
        dialog.setVisible(true);
        refreshQuestions();
    }

    private void showEditQuestionDialog() {
        int row = questionTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a question first."); return; }
        int id = (int) questionModel.getValueAt(row, 0);
        Question q = null;
        for (Question question : db.getAllQuestions()) {
            if (question.getId() == id) { q = question; break; }
        }
        if (q != null) {
            QuestionDialog dialog = new QuestionDialog(frame, q, db);
            dialog.setVisible(true);
            refreshQuestions();
        }
    }

    private void deleteSelectedQuestion() {
        int row = questionTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a question first."); return; }
        int id = (int) questionModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this question?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            db.deleteQuestion(id);
            refreshQuestions();
        }
    }

    private void styleTable(JTable table) {
        table.setBackground(new Color(14, 14, 32));
        table.setForeground(TEXT);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.setGridColor(new Color(30, 30, 55));
        table.setSelectionBackground(new Color(0, 100, 150));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(new Color(20, 20, 50));
        table.getTableHeader().setForeground(ACCENT);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
    }

    private JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }

    private JButton makeSmallButton(String text, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(25, 25, 50));
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fg, 1),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}