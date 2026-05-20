package quiz;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class LeaderboardPanel extends JPanel {
    private MainFrame frame;
    private DatabaseManager db;
    private User user;

    private static final Color BG = new Color(10, 10, 25);
    private static final Color CARD = new Color(18, 18, 40);
    private static final Color ACCENT = new Color(0, 212, 255);
    private static final Color GOLD = new Color(255, 200, 0);
    private static final Color TEXT = new Color(220, 220, 240);

    public LeaderboardPanel(MainFrame frame, DatabaseManager db, User user) {
        this.frame = frame;
        this.db = db;
        this.user = user;
        setBackground(BG);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(14, 14, 35));
        header.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));

        JLabel title = new JLabel("🏆  Leaderboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(GOLD);

        JButton backBtn = new JButton("← Back");
        backBtn.setBackground(new Color(20, 20, 45));
        backBtn.setForeground(new Color(150, 150, 200));
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            if (user != null && user.isAdmin()) frame.showAdminPanel();
            else frame.showQuizPanel();
        });

        header.add(title, BorderLayout.WEST);
        header.add(backBtn, BorderLayout.EAST);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(CARD);
        tabs.setForeground(TEXT);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("🥇 Top Scores", buildTopScoresTab());
        tabs.addTab("📅 Recent Activity", buildRecentTab());

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildTopScoresTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Aggregate best score per user
        Map<String, QuizAttempt> best = new LinkedHashMap<>();
        for (QuizAttempt a : db.getAllAttempts()) {
            if (!best.containsKey(a.getUsername()) ||
                a.getTotalScore() > best.get(a.getUsername()).getTotalScore()) {
                best.put(a.getUsername(), a);
            }
        }

        List<QuizAttempt> sorted = new ArrayList<>(best.values());
        sorted.sort((a, b) -> b.getTotalScore() - a.getTotalScore());

        String[] cols = {"Rank", "Username", "Best Score", "Accuracy", "Category"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        int rank = 1;
        for (QuizAttempt a : sorted) {
            String medal = rank == 1 ? "🥇 " : rank == 2 ? "🥈 " : rank == 3 ? "🥉 " : rank + ". ";
            model.addRow(new Object[]{medal + rank, a.getUsername(), a.getTotalScore(),
                a.getAccuracy(), a.getCategory()});
            rank++;
        }

        JTable table = buildTable(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);

        // Highlight current user's row
        String currUser = user != null ? user.getUsername() : "";
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                String uname = (String) t.getValueAt(row, 1);
                if (uname.equals(currUser)) {
                    c.setBackground(new Color(0, 80, 120));
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(sel ? new Color(0, 80, 120) : new Color(14, 14, 32));
                    c.setForeground(TEXT);
                }
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        styleScroll(scroll);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRecentTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] cols = {"Username", "Score", "Correct", "Accuracy", "Category", "Time (s)", "When"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        List<QuizAttempt> attempts = db.getAllAttempts();
        for (int i = attempts.size() - 1; i >= 0; i--) {
            QuizAttempt a = attempts.get(i);
            model.addRow(new Object[]{
                a.getUsername(), a.getTotalScore(),
                a.getCorrectAnswers() + "/" + a.getTotalQuestions(),
                a.getAccuracy(), a.getCategory(),
                a.getTimeTakenMillis() / 1000, a.getAttemptTime()
            });
        }

        JTable table = buildTable(model);
        JScrollPane scroll = new JScrollPane(table);
        styleScroll(scroll);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JTable buildTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(new Color(14, 14, 32));
        table.setForeground(TEXT);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setGridColor(new Color(28, 28, 55));
        table.setSelectionBackground(new Color(0, 80, 120));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(new Color(20, 20, 50));
        table.getTableHeader().setForeground(GOLD);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setShowGrid(true);
        return table;
    }

    private void styleScroll(JScrollPane scroll) {
        scroll.getViewport().setBackground(new Color(14, 14, 32));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(28, 28, 55)));
    }
}