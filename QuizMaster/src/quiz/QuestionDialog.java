package quiz;

import javax.swing.*;
import java.awt.*;

public class QuestionDialog extends JDialog {
    private DatabaseManager db;
    private Question existing;

    private JTextField questionField;
    private JTextField[] optionFields = new JTextField[4];
    private JComboBox<Integer> correctCombo;
    private JTextField categoryField;
    private JSpinner timeSpinner;

    private static final Color BG = new Color(15, 15, 35);
    private static final Color TEXT = new Color(220, 220, 240);
    private static final Color ACCENT = new Color(0, 212, 255);

    public QuestionDialog(JFrame parent, Question existing, DatabaseManager db) {
        super(parent, existing == null ? "Add Question" : "Edit Question", true);
        this.db = db;
        this.existing = existing;
        buildUI();
        if (existing != null) populateFields();
        pack();
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        JPanel panel = new JPanel();
        panel.setBackground(BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        panel.add(makeLabel("Question Text:"));
        questionField = makeTextField();
        panel.add(questionField);
        panel.add(Box.createVerticalStrut(10));

        for (int i = 0; i < 4; i++) {
            panel.add(makeLabel("Option " + (i + 1) + ":"));
            optionFields[i] = makeTextField();
            panel.add(optionFields[i]);
            panel.add(Box.createVerticalStrut(6));
        }

        panel.add(makeLabel("Correct Option (1-4):"));
        correctCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        correctCombo.setBackground(new Color(20, 20, 45));
        correctCombo.setForeground(TEXT);
        correctCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.add(correctCombo);
        panel.add(Box.createVerticalStrut(10));

        panel.add(makeLabel("Category:"));
        categoryField = makeTextField();
        panel.add(categoryField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(makeLabel("Time Limit (seconds):"));
        timeSpinner = new JSpinner(new SpinnerNumberModel(20, 5, 120, 5));
        timeSpinner.setBackground(BG);
        timeSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.add(timeSpinner);
        panel.add(Box.createVerticalStrut(15));

        JButton saveBtn = new JButton(existing == null ? "ADD QUESTION" : "SAVE CHANGES");
        saveBtn.setBackground(ACCENT);
        saveBtn.setForeground(BG);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> save());

        panel.add(saveBtn);
        setContentPane(panel);
        setBackground(BG);
        setPreferredSize(new Dimension(450, 560));
    }

    private void populateFields() {
        questionField.setText(existing.getQuestionText());
        String[] opts = existing.getOptions();
        for (int i = 0; i < 4; i++) optionFields[i].setText(opts[i]);
        correctCombo.setSelectedIndex(existing.getCorrectOptionIndex());
        categoryField.setText(existing.getCategory());
        timeSpinner.setValue(existing.getTimeLimit());
    }

    private void save() {
        String qText = questionField.getText().trim();
        String[] opts = new String[4];
        for (int i = 0; i < 4; i++) opts[i] = optionFields[i].getText().trim();
        String cat = categoryField.getText().trim();
        int correct = (int) correctCombo.getSelectedItem() - 1;
        int time = (int) timeSpinner.getValue();

        if (qText.isEmpty() || cat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Question and category are required.");
            return;
        }
        for (int i = 0; i < 4; i++) {
            if (opts[i].isEmpty()) {
                JOptionPane.showMessageDialog(this, "All 4 options are required.");
                return;
            }
        }

        if (existing == null) {
        	db.addQuestion(new Question(0, qText, opts, correct, cat, time));
        } else {
            existing.setQuestionText(qText);
            existing.setOptions(opts);
            existing.setCorrectOptionIndex(correct);
            existing.setCategory(cat);
            existing.setTimeLimit(time);
            db.updateQuestion(existing);
        }
        dispose();
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(140, 140, 180));
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return l;
    }

    private JTextField makeTextField() {
        JTextField f = new JTextField();
        f.setBackground(new Color(12, 12, 30));
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(40, 40, 80)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return f;
    }
}