package com.onlinejudge;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OnlineJudgeGUI {

    private JFrame frame;
    private JPanel mainPanel;
    private java.util.List<User> users = new ArrayList<>();
    private java.util.List<Problem> problems = new ArrayList<>();
    private final String FILE_NAME = "problems.txt";
    private User currentUser;

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            try {
                new OnlineJudgeGUI().createGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void createGUI() throws Exception {
        loadProblems();

        frame = new JFrame("🖥 Online Judge System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        showLoginScreen();

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    // ---------------- LOGIN SCREEN ----------------
    private void showLoginScreen() {
        mainPanel.removeAll();

        JPanel panel = new JPanel(new GridLayout(3, 1, 20, 20));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(new EmptyBorder(50, 100, 50, 100));

        JLabel titleLabel = new JLabel("Select Role", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));

        JButton adminBtn = createStyledButton("Admin Login", new Color(70, 130, 180));
        JButton userBtn = createStyledButton("User Login", new Color(60, 179, 113));

        panel.add(titleLabel);
        panel.add(adminBtn);
        panel.add(userBtn);

        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        adminBtn.addActionListener(e -> showAdminLogin());
        userBtn.addActionListener(e -> showUserLogin());
    }

    // ---------------- STYLED BUTTON ----------------
    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    // ---------------- ADMIN LOGIN ----------------
    private void showAdminLogin() {
        String pass = JOptionPane.showInputDialog(frame, "Enter Admin Password:");
        if (!"admin123".equals(pass)) {
            JOptionPane.showMessageDialog(frame, "❌ Wrong Password");
            return;
        }
        showAdminPanel();
    }

    private void showAdminPanel() {
        mainPanel.removeAll();
        JPanel panel = new JPanel(new GridLayout(3, 1, 20, 20));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(new EmptyBorder(50, 150, 50, 150));

        JButton addProblemBtn = createStyledButton("Add Problem", new Color(70, 130, 180));
        JButton viewProblemBtn = createStyledButton("View Problems", new Color(218, 165, 32));
        JButton logoutBtn = createStyledButton("Logout", new Color(178, 34, 34));

        panel.add(addProblemBtn);
        panel.add(viewProblemBtn);
        panel.add(logoutBtn);

        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        addProblemBtn.addActionListener(e -> addProblemGUI());
        viewProblemBtn.addActionListener(e -> viewProblemsTable());
        logoutBtn.addActionListener(e -> showLoginScreen());
    }

    // ---------------- ADD PROBLEM GUI ----------------
    private void addProblemGUI() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField titleField = new JTextField();
        JTextField descField = new JTextField();
        JTextField diffField = new JTextField();
        JTextField inputField = new JTextField();
        JTextField expectedField = new JTextField();

        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Description:"));
        panel.add(descField);
        panel.add(new JLabel("Difficulty (Easy/Medium/Hard):"));
        panel.add(diffField);
        panel.add(new JLabel("Inputs (comma separated):"));
        panel.add(inputField);
        panel.add(new JLabel("Expected Output:"));
        panel.add(expectedField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Problem", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText();
                String desc = descField.getText();
                String diff = diffField.getText();
                String[] inputStr = inputField.getText().split(",");
                int[] input = new int[inputStr.length];
                for (int i = 0; i < inputStr.length; i++) input[i] = Integer.parseInt(inputStr[i].trim());
                int expected = Integer.parseInt(expectedField.getText());

                problems.add(new Problem(title, desc, diff, input, expected));

                FileWriter fw = new FileWriter(FILE_NAME, true);
                fw.write(title + "|" + desc + "|" + diff + "|" + inputField.getText() + "|" + expected + "\n");
                fw.close();

                JOptionPane.showMessageDialog(frame, "✅ Problem Added!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        }
    }

    // ---------------- VIEW PROBLEMS TABLE ----------------
    private void viewProblemsTable() {
        if (problems.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No problems available.");
            return;
        }

        String[] cols = {"Title", "Description", "Difficulty", "Expected Output"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        for (Problem p : problems) {
            model.addRow(new Object[]{p.title, p.description, p.difficulty, p.expectedOutput});
        }

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(650, 300));

        JOptionPane.showMessageDialog(frame, scroll, "Problems", JOptionPane.PLAIN_MESSAGE);
    }

    // ---------------- USER LOGIN ----------------
    private void showUserLogin() {
        String username = JOptionPane.showInputDialog(frame, "Enter Username:");
        currentUser = new User(username);
        users.add(currentUser);
        showUserPanel();
    }

    private void showUserPanel() {
        mainPanel.removeAll();
        JPanel panel = new JPanel(new GridLayout(5, 1, 20, 20));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(new EmptyBorder(50, 150, 50, 150));

        JButton viewProblemsBtn = createStyledButton("View Problems", new Color(218, 165, 32));
        JButton submitSolutionBtn = createStyledButton("Submit Solution", new Color(70, 130, 180));
        JButton historyBtn = createStyledButton("Submission History", new Color(60, 179, 113));
        JButton leaderboardBtn = createStyledButton("Leaderboard", new Color(123, 104, 238));
        JButton logoutBtn = createStyledButton("Logout", new Color(178, 34, 34));

        panel.add(viewProblemsBtn);
        panel.add(submitSolutionBtn);
        panel.add(historyBtn);
        panel.add(leaderboardBtn);
        panel.add(logoutBtn);

        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        viewProblemsBtn.addActionListener(e -> viewProblemsTable());
        submitSolutionBtn.addActionListener(e -> submitSolutionGUI());
        historyBtn.addActionListener(e -> showHistoryGUI());
        leaderboardBtn.addActionListener(e -> showLeaderboardGUI());
        logoutBtn.addActionListener(e -> showLoginScreen());
    }

    // ---------------- SUBMIT SOLUTION ----------------
    private void submitSolutionGUI() {
        if (problems.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No problems available.");
            return;
        }

        String[] problemTitles = problems.stream().map(p -> p.title).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(frame, "Select Problem:", "Submit Solution",
                JOptionPane.PLAIN_MESSAGE, null, problemTitles, problemTitles[0]);

        if (selected == null) return;

        Problem p = problems.stream().filter(pr -> pr.title.equals(selected)).findFirst().orElse(null);
        if (p == null) return;

        String outputStr = JOptionPane.showInputDialog(frame,
                "Problem: " + p.title + "\n" + p.description + "\nEnter your output:");

        try {
            int userOutput = Integer.parseInt(outputStr);
            boolean passed = (userOutput == p.expectedOutput);
            String result = passed ? "✅ Passed!" : "❌ Failed!";
            currentUser.history.add(p.title + " -> " + result);
            if (passed) currentUser.score++;

            JOptionPane.showMessageDialog(frame, result);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }

    private void showHistoryGUI() {
        if (currentUser.history.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No submissions yet.");
            return;
        }

        JTextArea textArea = new JTextArea(String.join("\n", currentUser.history));
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(600, 300));

        JOptionPane.showMessageDialog(frame, scroll, "Submission History", JOptionPane.PLAIN_MESSAGE);
    }

    private void showLeaderboardGUI() {
        users.sort((a, b) -> b.score - a.score);

        String[] cols = {"Username", "Score"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (User u : users) {
            model.addRow(new Object[]{u.username, u.score});
        }

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(frame, scroll, "Leaderboard", JOptionPane.PLAIN_MESSAGE);
    }

    // ---------------- LOAD PROBLEMS FROM FILE ----------------
    private void loadProblems() throws Exception {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\\|");
            String title = parts[0];
            String desc = parts[1];
            String difficulty = parts[2];

            String[] inputStr = parts[3].split(",");
            int[] input = new int[inputStr.length];
            for (int i = 0; i < inputStr.length; i++) input[i] = Integer.parseInt(inputStr[i].trim());

            int expected = Integer.parseInt(parts[4]);
            problems.add(new Problem(title, desc, difficulty, input, expected));
        }
        br.close();
    }
}
