package com.onlinejudge;

import java.util.*;
import java.io.*;

// ---------------- USER CLASS ----------------
class User {
    String username;
    int score;
    int penalties;
    List<String> history = new ArrayList<>();

    User(String username) {
        this.username = username;
        this.score = 0;
        this.penalties = 0;
    }
}

// ---------------- PROBLEM CLASS ----------------
class Problem {
    String title;
    String description;
    String difficulty;
    int[] input;
    int expectedOutput;

    Problem(String title, String description, String difficulty, int[] input, int expectedOutput) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.input = input;
        this.expectedOutput = expectedOutput;
    }
}

// ---------------- MAIN CLASS ----------------
public class OnlineJudgeFull {

    static Scanner sc = new Scanner(System.in);
    static List<User> users = new ArrayList<>();
    static List<Problem> problems = new ArrayList<>();
    static final String FILE_NAME = "problems.txt";

    public static void main(String[] args) throws Exception {

        loadProblems();

        System.out.println("1. Admin Login");
        System.out.println("2. User Login");
        int role = sc.nextInt();
        sc.nextLine();

        if (role == 1) {
            adminPanel();
        } else {
            userPanel();
        }
    }

    // ---------------- ADMIN PANEL ----------------
    static void adminPanel() throws Exception {
        System.out.print("Enter Admin Password: ");
        String pass = sc.nextLine();

        if (!pass.equals("admin123")) {
            System.out.println("❌ Wrong Password");
            return;
        }

        int choice;
        do {
            System.out.println("\n=== ADMIN PANEL ===");
            System.out.println("1. Add Problem");
            System.out.println("2. View Problems");
            System.out.println("0. Exit");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addProblem();
                    break;
                case 2:
                    viewProblems();
                    break;
            }

        } while (choice != 0);
    }

    // ---------------- USER PANEL ----------------
    static void userPanel() {
        System.out.print("Enter Username: ");
        String name = sc.nextLine();

        User currentUser = new User(name);
        users.add(currentUser);

        int choice;

        do {
            System.out.println("\n=== USER PANEL ===");
            System.out.println("1. View Problems");
            System.out.println("2. Submit Solution");
            System.out.println("3. Submission History");
            System.out.println("4. Leaderboard");
            System.out.println("0. Exit");

            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    viewProblems();
                    break;
                case 2:
                    submitSolution(currentUser);
                    break;
                case 3:
                    showHistory(currentUser);
                    break;
                case 4:
                    showLeaderboard();
                    break;
            }

        } while (choice != 0);
    }

    // ---------------- LOAD PROBLEMS FROM FILE ----------------
    static void loadProblems() throws Exception {
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
            for (int i = 0; i < inputStr.length; i++) {
                input[i] = Integer.parseInt(inputStr[i]);
            }

            int expected = Integer.parseInt(parts[4]);

            problems.add(new Problem(title, desc, difficulty, input, expected));
        }

        br.close();
    }

    // ---------------- ADD PROBLEM ----------------
    static void addProblem() throws Exception {

        System.out.print("Enter Title: ");
        String title = sc.nextLine();

        System.out.print("Enter Description: ");
        String desc = sc.nextLine();

        System.out.print("Enter Difficulty (Easy/Medium/Hard): ");
        String diff = sc.nextLine();

        System.out.print("Enter inputs (comma separated): ");
        String inputLine = sc.nextLine();

        System.out.print("Enter expected output: ");
        int expected = sc.nextInt();
        sc.nextLine();

        FileWriter fw = new FileWriter(FILE_NAME, true);
        fw.write(title + "|" + desc + "|" + diff + "|" + inputLine + "|" + expected + "\n");
        fw.close();

        System.out.println("✅ Problem Added!");
    }

    // ---------------- VIEW PROBLEMS ----------------
    static void viewProblems() {
        System.out.println("\nAvailable Problems:");
        for (int i = 0; i < problems.size(); i++) {
            Problem p = problems.get(i);
            System.out.println((i + 1) + ". " + p.title + " [" + p.difficulty + "]");
        }
    }

    // ---------------- SUBMIT SOLUTION ----------------
    static void submitSolution(User user) {
        viewProblems();
        System.out.print("Select problem: ");
        int index = sc.nextInt() - 1;

        if (index < 0 || index >= problems.size()) {
            System.out.println("Invalid selection!");
            return;
        }

        Problem p = problems.get(index);

        System.out.println("Problem: " + p.title);
        System.out.println(p.description);

        System.out.print("Enter your output: ");
        int userOutput = sc.nextInt();

        long startTime = System.currentTimeMillis();

        boolean passed = (userOutput == p.expectedOutput);

        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;

        if (passed) {
            int points = getPoints(p.difficulty);
            user.score += points;
            user.history.add(p.title + " ✅ (" + points + " pts, " + timeTaken + " ms)");
            System.out.println("✅ Passed! +" + points + " points");
        } else {
            user.penalties++;
            user.score -= 5;
            user.history.add(p.title + " ❌ (-5 penalty)");
            System.out.println("❌ Failed! -5 penalty");
        }
    }

    // ---------------- POINTS BASED ON DIFFICULTY ----------------
    static int getPoints(String diff) {
        switch (diff.toLowerCase()) {
            case "easy": return 10;
            case "medium": return 20;
            case "hard": return 30;
            default: return 10;
        }
    }

    // ---------------- HISTORY ----------------
    static void showHistory(User user) {
        System.out.println("\nSubmission History:");
        for (String s : user.history) {
            System.out.println(s);
        }
    }

    // ---------------- LEADERBOARD ----------------
    static void showLeaderboard() {
        users.sort((a, b) -> b.score - a.score);

        System.out.println("\n🏆 Leaderboard:");
        for (User u : users) {
            System.out.println(u.username + " | Score: " + u.score + " | Penalties: " + u.penalties);
        }
    }
}
