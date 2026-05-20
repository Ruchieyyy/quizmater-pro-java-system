package quiz;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPanel extends JPanel {
    private MainFrame frame;
    private DatabaseManager db;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    // Colors
    private static final Color BG = new Color(10, 10, 25);
    private static final Color CARD_BG = new Color(20, 20, 45);
    private static final Color ACCENT = new Color(0, 212, 255);
    private static final Color ACCENT2 = new Color(255, 65, 125);
    private static final Color TEXT = new Color(220, 220, 240);
    private static final Color MUTED = new Color(120, 120, 160);

    public LoginPanel(MainFrame frame, DatabaseManager db) {
        this.frame = frame;
        this.db = db;
        setBackground(BG);
        setLayout(new GridBagLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 2),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        card.setPreferredSize(new Dimension(420, 480));

        // Logo / Title
        JLabel logo = new JLabel("⚡ QuizMaster Pro", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logo.setForeground(ACCENT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Kahoot-Style Quiz Platform", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(40, 40, 80));
        sep.setMaximumSize(new Dimension(300, 2));

        // Fields
        JLabel userLabel = makeLabel("USERNAME");
        usernameField = makeTextField();

        JLabel passLabel = makeLabel("PASSWORD");
        passwordField = new JPasswordField();
        styleTextField(passwordField);

        // Status
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(ACCENT2);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons
        JButton loginBtn = makeButton("LOGIN", ACCENT, BG);
        loginBtn.addActionListener(e -> handleLogin());

        JButton registerBtn = makeButton("REGISTER", new Color(30, 30, 60), MUTED);
        registerBtn.setBorder(BorderFactory.createLineBorder(MUTED));
        registerBtn.addActionListener(e -> handleRegister());

        JLabel hint = new JLabel("Admin: admin/admin123 | User: student/student123", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        hint.setForeground(new Color(80, 80, 110));
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Enter key triggers login
        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> handleLogin());

        card.add(logo);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));
        card.add(sep);
        card.add(Box.createVerticalStrut(25));
        card.add(userLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(16));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(12));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(hint);

        add(card);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("⚠ Please fill all fields.");
            return;
        }
        User user = db.login(username, password);
        if (user == null) {
            statusLabel.setText("✗ Invalid credentials.");
            passwordField.setText("");
        } else {
            frame.setCurrentUser(user);
            if (user.isAdmin()) frame.showAdminPanel();
            else frame.showQuizPanel();
        }
    }

    private void handleRegister() {
        JTextField u = new JTextField();
        JPasswordField p = new JPasswordField();
        Object[] msg = {"Username:", u, "Password:", p};
        int result = JOptionPane.showConfirmDialog(this, msg, "Register New Account",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String username = u.getText().trim();
            String password = new String(p.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("⚠ Username/password cannot be empty.");
                return;
            }
            if (db.registerUser(username, password)) {
                statusLabel.setForeground(new Color(0, 255, 150));
                statusLabel.setText("✓ Registered! You can now login.");
            } else {
                statusLabel.setForeground(new Color(255, 65, 125));
                statusLabel.setText("✗ Username already exists.");
            }
        }
    }

    // ---- Helpers ----
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField makeTextField() {
        JTextField f = new JTextField();
        styleTextField(f);
        return f;
    }

    private void styleTextField(JTextField f) {
        f.setBackground(new Color(12, 12, 30));
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 90)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    private JButton makeButton(String text, Color bg, Color fg) {
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